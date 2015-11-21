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

import java.io.Serializable;

import org.jivesoftware.smack.packet.IQ;

import android.text.TextUtils;

/**
 * This class represents a notifcatin IQ packet.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationIQ extends IQ implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	private String id ; 
	
	private String packetID ; 
	
	public void setPacketID(String packetID){
		this.packetID = packetID;
	}
	
	public String getPacketID(){
		return this.packetID;
	}
	
	
	public NotificationIQ() {
		
	}

	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
		buf.append("<").append("notification").append(" xmlns=\"")
				.append("androidpn:iq:notification").append("\">");
		if (!TextUtils.isEmpty(message)) {
			buf.append("<message>").append(message).append("</message>");
		}
		if (!TextUtils.isEmpty(id)) {
			buf.append("<id>").append(id).append("</id>");
		}
		buf.append("</").append("notification").append("> ");
		return buf.toString();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	

}
