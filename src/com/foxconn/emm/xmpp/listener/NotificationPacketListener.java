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
package com.foxconn.emm.xmpp.listener;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.content.Intent;

import com.foxconn.emm.service.EMMBaseIntentService;
import com.foxconn.emm.utils.EMMContants;
import com.foxconn.emm.utils.L;
import com.foxconn.emm.xmpp.NotificationIQ;
import com.foxconn.emm.xmpp.XmppManager;

/**
 * This class notifies the receiver of incoming notifcation packets
 * asynchronously.
 * 
 */
public class NotificationPacketListener implements PacketListener {

	private static final String DEFAULT_INTENT_SERVICE_CLASS_NAME = ".EMMIntentService";

	public static final String FLAG_NOTIFICATION_PACKETNAME = "FLAG_NOTIFICATION_PACKETNAME";

	public static final String FLAG_NOTIFICATION_PACKETID = "FLAG_NOTIFICATION_PACKETID";

	public static final String FLAG_NOTIFICATION_ID = "FLAG_NOTIFICATION_ID";

	private final XmppManager xmppManager;

	public NotificationPacketListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	@Override
	public void processPacket(Packet packet) {
		L.d("NotificationPacketListener.processPacket()...");
		L.d("packet.toXML()=" + packet.toXML());

		if (packet instanceof NotificationIQ) {
			NotificationIQ notification = (NotificationIQ) packet;
			if (notification.getChildElementXML().contains(
					"androidpn:iq:notification")) {
				Intent intent = new Intent(EMMContants.XMPPCONF.ACTION_PROCESS_NOTIFICATION);
				intent.putExtra(FLAG_NOTIFICATION_PACKETNAME, notification);
				IQ result = NotificationIQ.createResultIQ(notification);
				try {
					xmppManager.getConnection().sendPacket(result);
				} catch (Exception e) {
				}

				// String className =
				// getEMMMIntentServiceClassName(xmppManager.getContext());

				// String className =
				// "com.foxconn.emm.service"+DEFAULT_INTENT_SERVICE_CLASS_NAME;
				String className = "com.foxconn.emm.service.EMMIntentService";
				EMMBaseIntentService.runIntentInService(xmppManager.getContext(), intent, className);

			}
		}
	}

	/**
	 * Gets the class name of the intent service that will handle EMM messages.
	 */
	protected String getEMMMIntentServiceClassName(Context context) {
		return getDefaultIntentServiceClassName(context);
	}

	/**
	 * Gets the default class name of the intent service that will handle EMM
	 * messages.
	 */
	static final String getDefaultIntentServiceClassName(Context context) {
		String className = context.getPackageName()
				+ DEFAULT_INTENT_SERVICE_CLASS_NAME;
		return className;
	}

}
