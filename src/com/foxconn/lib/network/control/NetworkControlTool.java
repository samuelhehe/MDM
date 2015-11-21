package com.foxconn.lib.network.control;

import android.content.Context;

import com.foxconn.emm.utils.L;
import com.foxconn.lib.network.control.Api.DroidApp;

public class NetworkControlTool {
	
	private Context context;

	public NetworkControlTool(Context context){
		this.context = context;
		Api.setEnabled(context, true);
	}
	
	/**
	 * shutdown wifi network 
	 * @param context
	 */
	public   void shutdownWifiNetwork() {
		DroidApp[] apps = Api.getApps(context);
			for (int i = 0; i < apps.length; i++) {
				DroidApp app = apps[i];
				app.selected_wifi = true;
			}
			if (Api.hasRootAccess(context, true)
					&& Api.applyIptablesRules(context, true)) {
				L.i(context.getClass(), "shutdown wifi network is complete...");
			}
			
	}
	
	/**
	 * shutdown data network 
	 * @param context
	 */
	public   void shutdownDataNetwork(){
		DroidApp[] apps = Api.getApps(context);
		for (int i = 0; i < apps.length; i++) {
			DroidApp app = apps[i];
			app.selected_3g = true;
		}
		if (Api.hasRootAccess(context, true)
				&& Api.applyIptablesRules(context, true)) {
			L.i(context.getClass(), "shutdown data network is complete...");
		}
	}
	
	
	
	
	
}
