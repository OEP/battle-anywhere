package org.oep.net;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public abstract class MACHandler {
	
	public static MACHandler getWirelessHandler(Context ctx, final MACReceiver receiver) {
		WirelessHandler handler = WirelessHandler.Holder.sInstance;
		handler.setContext(ctx);
		
		IntentFilter i = new IntentFilter();
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		
		ctx.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> results = manager.getScanResults();
				List<MACResult> output = new ArrayList<MACResult>();
				
				for(ScanResult result : results) {
					String mac = MACHandler.formatMAC(result.BSSID);
					int resId = org.oep.battle.R.drawable.creature_router;
					
					System.out.println("The router drawable is : " + resId);
					
					output.add(new MACResult(mac, resId));
				}
				
				receiver.onReceive(output);
			}
		}, i);
		
		return WirelessHandler.Holder.sInstance;
	}
	
	public static MACHandler getBluetoothHandler(Context ctx) {
		if(Integer.parseInt(Build.VERSION.SDK) >= 5) {
			return BluetoothHandler.Holder.sInstance;
		}
		
		return null;
	}
	
	public abstract String getMAC();
	public abstract boolean startScan();
	public abstract boolean isEnabled();
	public abstract void setContext(Context ctx);
	
	
	private static class WirelessHandler extends MACHandler {
		private WifiManager mManager;
		private Context mContext;
		
		private static class Holder {
			private static final WirelessHandler sInstance = new WirelessHandler();
		}
		
		@Override
		public String getMAC() {
			WifiInfo info = mManager.getConnectionInfo();
			
			String mac = info.getMacAddress();
			mac = (mac != null) ? mac : "DEFECADE";
			
			return MACHandler.formatMAC(mac);
		}
		
		@Override
		public boolean isEnabled() {
			return mManager.isWifiEnabled();
		}
		
		protected void setup(Context ctx) {
			mManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		}

		@Override
		public boolean startScan() {
			return mManager.startScan();
		}

		@Override
		public void setContext(Context ctx) {
			mContext = ctx;
			setup(mContext);
		}

	}
	

	public static class BluetoothHandler extends MACHandler {
		
		private static class Holder {
			private static final BluetoothHandler sInstance = new BluetoothHandler();
		}

		@Override
		public String getMAC() {
			return Long.toHexString(Long.MAX_VALUE);
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean startScan() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setContext(Context ctx) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static String formatMAC(String mac) {
		return mac.replaceAll(":", "").toUpperCase();
	}

}
