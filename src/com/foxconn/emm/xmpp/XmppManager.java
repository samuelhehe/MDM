/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foxconn.emm.xmpp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.EMMPreferences;
import com.foxconn.emm.utils.EMMReqParamsUtils;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.utils.NetUtil;
import com.foxconn.emm.xmpp.listener.NotificationPacketListener;
import com.foxconn.emm.xmpp.listener.PersistentConnectionListener;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

	private static final String XMPP_RESOURCE_NAME = "EMM3.0";

	private Context context;

	private NotificationService.TaskSubmitter taskSubmitter;

	private NotificationService.TaskTracker taskTracker;

	private SharedPreferences sharedPrefs;

	private String xmppHost;

	private int xmppPort;

	private XMPPConnection connection;

	private String username;

	private String password;

	private String userId;

	private ConnectionListener connectionListener;

	private PacketListener notificationPacketListener;

	private Handler handler;

	private List<Runnable> taskList;

	private boolean running = false;

	private Future<?> futureTask;

	private Thread reconnection;

	public XmppManager(NotificationService notificationService) {
		context = notificationService;
		taskSubmitter = notificationService.getTaskSubmitter();
		taskTracker = notificationService.getTaskTracker();
		sharedPrefs = notificationService.getSharedPreferences();

		xmppHost = sharedPrefs.getString(EMMContants.XMPPCONF.XMPP_HOST,
				EMMContants.XMPPCONF.XMPP_BASEHOST);
		xmppPort = sharedPrefs.getInt(EMMContants.XMPPCONF.XMPP_PORT,
				EMMContants.XMPPCONF.XMPP_BASEPORT);

		// xmpp username
		username = sharedPrefs
				.getString(EMMContants.XMPPCONF.XMPP_USERNAME, "");

		// 密码
		password = sharedPrefs
				.getString(EMMContants.XMPPCONF.XMPP_PASSWORD, "");

		// 工号
		userId = sharedPrefs.getString(EMMContants.XMPPCONF.USER_ID, "");
		connectionListener = new PersistentConnectionListener(this);
		notificationPacketListener = new NotificationPacketListener(this);
		handler = new Handler();
		taskList = new ArrayList<Runnable>();
		reconnection = new ReconnectionThread(this);
		EMMContants.XMPPCONF.xmppManager = this;
	}

	public Context getContext() {
		return context;
	}

	public void connect() {
		L.d(XmppManager.class, "connect()...");
		if(NetUtil.checkNetWorkInfo(getContext())){
			submitLoginTask();
		}
	}

	public void disconnect() {
		L.d(XmppManager.class, "disconnect()...");
		terminatePersistentConnection();
	}

	public void terminatePersistentConnection() {
		L.d(XmppManager.class, "terminatePersistentConnection()...");
		Runnable runnable = new Runnable() {
			final XmppManager xmppManager = XmppManager.this;

			public void run() {
				if (xmppManager.isConnected()) {
					L.d(XmppManager.class,
							"terminatePersistentConnection()... run()");
					xmppManager.getConnection().removePacketListener(
							xmppManager.getNotificationPacketListener());
					xmppManager.getConnection().disconnect();
				}
				xmppManager.runTask();
			}
		};
		addTask(runnable);
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	public void startReconnectionThread() {
		synchronized (reconnection) {
			if (!reconnection.isAlive()) {
				reconnection.setName("Xmpp Reconnection Thread");
				reconnection.start();
			}
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void reregisterAccount() {
		removeAccount();
		submitLoginTask();
		runTask();
	}

	public List<Runnable> getTaskList() {
		return taskList;
	}

	public Future<?> getFutureTask() {
		return futureTask;
	}

	public void runTask() {
		L.d(XmppManager.class, "runTask()...");
		synchronized (taskList) {
			running = false;
			futureTask = null;
			if (!taskList.isEmpty()) {
				Runnable runnable = (Runnable) taskList.get(0);
				taskList.remove(0);
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			}
		}
		taskTracker.decrease();
		L.d(XmppManager.class, "runTask()...done");
	}

	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}

	private boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	private boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	private boolean isRegistered() {
		return sharedPrefs.contains(EMMContants.XMPPCONF.XMPP_USERNAME)
				&& sharedPrefs.contains(EMMContants.XMPPCONF.XMPP_PASSWORD);
	}

	private void submitConnectTask() {
		L.d(XmppManager.class, "submitConnectTask()...");
		addTask(new ConnectTask());
	}

	private void submitRegisterTask() {
		L.d(XmppManager.class, "submitRegisterTask()...");
		submitConnectTask();
		addTask(new RegisterTask());
	}

	private void submitLoginTask() {
		L.d(XmppManager.class, "submitLoginTask()...");
		submitRegisterTask();
		addTask(new LoginTask());
	}

	private void addTask(Runnable runnable) {
		L.d(XmppManager.class, "addTask(runnable)...");
		taskTracker.increase();
		synchronized (taskList) {
			if (taskList.isEmpty() && !running) {
				running = true;
				futureTask = taskSubmitter.submit(runnable);
				if (futureTask == null) {
					taskTracker.decrease();
				}
			} else {
				taskList.add(runnable);
			}
		}
		L.d(XmppManager.class, "addTask(runnable)... done");
	}

	private void removeAccount() {
		Editor editor = sharedPrefs.edit();
		editor.remove(EMMContants.XMPPCONF.XMPP_USERNAME);
		editor.remove(EMMContants.XMPPCONF.XMPP_PASSWORD);
		editor.remove(EMMContants.XMPPCONF.USER_ID);
		editor.commit();
	}

	/**
	 * A runnable task to connect the server.
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private ConnectTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			L.i(XmppManager.class, "ConnectTask.run()...");

			if (!xmppManager.isConnected()) {
				// Create the configuration for this new connection
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						xmppHost, xmppPort);
				// connConfig.setSecurityMode(SecurityMode.disabled);
				connConfig.setSecurityMode(SecurityMode.required);
				connConfig.setSASLAuthenticationEnabled(false);// 不使用SASL验证，设置为false
				connConfig.setCompressionEnabled(false);

				XMPPConnection connection = new XMPPConnection(connConfig);
				xmppManager.setConnection(connection);

				try {
					// Connect to the server
					connection.connect();
					L.i(XmppManager.class, "XMPP connected successfully");

					// packet provider
					ProviderManager.getInstance().addIQProvider("notification",
							"androidpn:iq:notification",
							new NotificationIQProvider());

				} catch (XMPPException e) {
					L.i(XmppManager.class, "XMPP connection failed", e);
				}

				xmppManager.runTask();

			} else {
				L.i(XmppManager.class, "XMPP connected already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class RegisterTask implements Runnable {

		final XmppManager xmppManager;

		private RegisterTask() {
			xmppManager = XmppManager.this;
		}

		public void run() {
			L.i(XmppManager.class, "RegisterTask.run()...");

			if (!xmppManager.isRegistered()) {

				final String newUsername = EMMReqParamsUtils.getWifiMac(
						getContext()).replace(":", "");
				final String newUserId = EMMPreferences.getUserID(context);
				Registration registration = new Registration();

				PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
						registration.getPacketID()), new PacketTypeFilter(
						IQ.class));

				PacketListener packetListener = new PacketListener() {

					public void processPacket(Packet packet) {
						L.d(XmppManager.class, "RegisterTask.PacketListener"
								+ "processPacket().....");
						L.d(XmppManager.class, "RegisterTask.PacketListener"
								+ "packet=" + packet.toXML());

						if (packet instanceof IQ) {
							IQ response = (IQ) packet;
							if (response.getType() == IQ.Type.ERROR) {
								if (!response.getError().toString()
										.contains("409")) {
									L.i(XmppManager.class,
											"Unknown error while registering XMPP account! "
													+ response.getError()
															.getCondition());
								}
							} else if (response.getType() == IQ.Type.RESULT) {
								xmppManager.setUsername(newUsername);
								xmppManager.setPassword(newUserId);
								xmppManager.setUserId(newUserId);

								L.d(XmppManager.class, "username="
										+ newUsername);
								L.d(XmppManager.class, "userid=" + newUserId);

								Editor editor = sharedPrefs.edit();
								editor.putString(
										EMMContants.XMPPCONF.XMPP_USERNAME,
										newUsername);
								editor.putString(EMMContants.XMPPCONF.USER_ID,
										newUserId);
								editor.commit();
								L.i(XmppManager.class,
										"Account registered successfully");
								xmppManager.runTask();
							}
						}
					}
				};

				connection.addPacketListener(packetListener, packetFilter);

				registration.setType(IQ.Type.SET);
				// registration.setTo(xmppHost);
				// Map<String, String> attributes = new HashMap<String,
				// String>();
				// attributes.put("username", rUsername);
				// attributes.put("password", rPassword);
				// registration.setAttributes(attributes);
				registration.addAttribute("username", newUsername);
				registration.addAttribute("password", newUserId);
//				registration.addAttribute("user_id", newUserId);
				connection.sendPacket(registration);

			} else {
				L.i(XmppManager.class, "Account registered already");
				xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to log into the server.
	 */
	private class LoginTask implements Runnable {

		final XmppManager xmppManager;

		private LoginTask() {
			this.xmppManager = XmppManager.this;
		}

		public void run() {
			L.i(XmppManager.class, "LoginTask.run()...");

			// // 如果没有通过验证,则进行登陆验证
			if (!xmppManager.isAuthenticated()) {
				L.d(XmppManager.class, "username=" + username);
				L.d(XmppManager.class, "password=" + password);
				L.d(XmppManager.class, "name=" + userId);

				try {
					xmppManager.getConnection().login(
							xmppManager.getUsername(),
							xmppManager.getUserId(), XMPP_RESOURCE_NAME);
					L.d(XmppManager.class, "Loggedn in successfully");

					// connection listener
					if (xmppManager.getConnectionListener() != null) {
						xmppManager.getConnection().addConnectionListener(
								xmppManager.getConnectionListener());
					}

					// packet filter
					PacketFilter packetFilter = new PacketTypeFilter(
							NotificationIQ.class);
					// packet listener
					PacketListener packetListener = xmppManager
							.getNotificationPacketListener();
					connection.addPacketListener(packetListener, packetFilter);

					getConnection().startKeepAliveThread();

				} catch (XMPPException e) {
					L.i(XmppManager.class, "LoginTask.run()... xmpp error");
					L.i(XmppManager.class,
							"Failed to login to xmpp server. Caused by: "
									+ e.getMessage());
					String INVALID_CREDENTIALS_ERROR_CODE = "401";
					String errorMessage = e.getMessage();
					if (errorMessage != null
							&& errorMessage
									.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
						xmppManager.reregisterAccount();
						return;
					}
					xmppManager.startReconnectionThread();

				} catch (Exception e) {
					L.i(XmppManager.class, "LoginTask.run()... other error");
					L.i(XmppManager.class,
							"Failed to login to xmpp server. Caused by: "
									+ e.getMessage());
					xmppManager.startReconnectionThread();
				}

				xmppManager.runTask();
			} else {
				L.i(XmppManager.class, "Logged in already");
				xmppManager.runTask();
			}

		}
	}

}
