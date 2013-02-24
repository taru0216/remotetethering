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

package net.taruishi.android.remotethering.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.logging.Logger;

import net.taruishi.android.remotethering.BluetoothModel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class Server extends Observable implements Observer {

	private Logger mLogger = Logger.getLogger(Server.class.getName());

	private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	private Context mContext;
	// private ServerView mView;
	private BluetoothModel mBluetoothModel;

	private Thread mThread;
	private Boolean threadEnabled = false;

	private BluetoothServer mBluetoothServer;

	public Server(Context context, BluetoothModel bluetoothModel) {
		mContext = context;
		mBluetoothModel = bluetoothModel;
		mBluetoothModel.addObserver(this);

		mBluetoothServer = new BluetoothServer(BluetoothModel.MY_UUID);
	}

	// public void setView(ServerView view) {
	// mView = view;
	// }

	private class BluetoothServer {
		private BluetoothServerSocket mSocket;
		private UUID mUuid;

		public BluetoothServer(UUID uuid) {
			mUuid = uuid;
		}

		public void listen() throws IOException {
			mSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Test", mUuid);
		}

		public void close() throws IOException {
			if (mSocket != null) {
				mSocket.close();
			}
			mSocket = null;
		}

		public BluetoothServerSocket getBluetoothServerSocket() {
			return mSocket;
		}
	}

	private void setWifiEnabled(boolean enable) {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Activity.WIFI_SERVICE);
		wifiManager.setWifiEnabled(enable);
	}

	private void setWifiTetheringEnabled(boolean enable) {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Activity.WIFI_SERVICE);

		Method[] methods = wifiManager.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals("setWifiApEnabled")) {
				try {
					method.invoke(wifiManager, null, enable);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				break;
			}
		}
	}

	public void onCreate() {
		synchronized (threadEnabled) {
			if (mThread == null) {
				threadEnabled = true;
				mThread = new Thread() {
					@Override
					public void run() {
						mLogger.info("Server started");
						try {
							mBluetoothServer.listen();
							while (threadEnabled) {
								mLogger.info("Accepting connection.");
								BluetoothSocket socket = mBluetoothServer
										.getBluetoothServerSocket().accept();
								mLogger.info("Accepcted: " + socket);
								String msg = "Accepted: " + socket.getRemoteDevice().getName();
								Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

								setWifiEnabled(false);
								setWifiTetheringEnabled(true);
								BufferedReader in = new BufferedReader(new InputStreamReader(
										socket.getInputStream()));

								while (true) {
									try {
										String line = in.readLine();
										if (line == null) {
											break;
										}
										Toast.makeText(mContext, line, Toast.LENGTH_LONG).show();
									} catch (IOException e) {
										e.printStackTrace();
										break;
									}
								}
								msg = "Disconneced: " + socket.getRemoteDevice().getName();
								Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
								setWifiTetheringEnabled(false);
								setWifiEnabled(true);
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						mLogger.info("Server finished");
					}
				};
				mThread.start();
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}
}
