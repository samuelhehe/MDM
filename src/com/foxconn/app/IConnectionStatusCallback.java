package com.foxconn.app;

public interface IConnectionStatusCallback {
	public void connectionStatusChanged(int connectedState, String reason);
	
}
