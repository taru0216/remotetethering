/* 
 * Copyright (c) 2007 Masato Taruishi
 * 
 * This code is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License 
 * version 3 for more details.
 *
 * You should have received a copy of the GNU General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.taruishi.android.remotethering.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Observable;
import java.util.logging.Logger;

import net.taruishi.android.remotethering.Presentor;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.wifi.WifiManager;

public class ClientPresentor extends Presentor {

	Logger mLogger = Logger.getLogger(ClientPresentor.class.getName());

	private Client mClient;

	public ClientPresentor(Context context, Client client) {
		super(context);
		mClient = client;
	}

	// queries from views
	public void setServerDevice(BluetoothDevice serverDevice) {
		mClient.setServerDevice(serverDevice);
	}

	private void setWifiEnabled(boolean enable) {
		WifiManager wifiManager = (WifiManager) getContext().getSystemService(Activity.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enable);
		if (enable) {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			wifiManager.startScan();
		}
	}
	
	@Override
	public void update(Observable observable, Object type) {
		if (type == Client.NotifyType.BONDED_DEVICE_CHANGED) {
			for (ClientView view : mViews) {
				view.updateBondedDevices(mClient.getBondedDevices());
			}
		} else if (type == Client.NotifyType.SERVER_CONNECTED) {
			try {
				Writer writer = new OutputStreamWriter(mClient.getBluetoothSocket()
						.getOutputStream());
				for (ClientView view : mViews) {
					view.showMessage("Connected to " + mClient.getBluetoothSocket().getRemoteDevice().getName());
				}			
				mLogger.info("Sending Hello, world!\n");
				writer.write("Hello, world!\n");
				writer.flush();
				setWifiEnabled(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (type == Client.NotifyType.SERVER_CONNECT_FAILED) {
			for (ClientView view : mViews) {
				view.showMessage("Connection failed!");
			}			
		}
	}
}
