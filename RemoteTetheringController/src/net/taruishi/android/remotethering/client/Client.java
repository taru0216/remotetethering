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
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import net.taruishi.android.remotethering.BluetoothModel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class Client extends Observable implements Observer {

	private static Logger mLogger = Logger.getLogger(Client.class.getName());

	private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	public static enum NotifyType {
		BONDED_DEVICE_CHANGED, SERVER_CONNECTED, SERVER_CONNECT_FAILED,
	};

	private Activity mActivity;
	private BluetoothModel mBluetoothModel;
	private BluetoothConnection mServerConnection;

	private Thread mThread;
	private Boolean threadEnabled = false;

	public Client(Activity activity, BluetoothModel model) {
		mActivity = activity;
		mBluetoothModel = model;
		mBluetoothModel.addObserver(this);
	}

	public Set<BluetoothDevice> getBondedDevices() {
		return mBluetoothAdapter.getBondedDevices();
	}

	public void setServerDevice(BluetoothDevice serverDevice) {
		mBluetoothModel.setServerDevice(serverDevice);
	}

	public void onStart() {
		synchronized (threadEnabled) {
			if (mThread == null) {
				threadEnabled = true;
				mThread = new Thread() {
					@Override
					public void run() {
						mLogger.info("Client started");
						while (threadEnabled) {
							setChanged();
							notifyObservers(NotifyType.BONDED_DEVICE_CHANGED);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						mLogger.info("Client finished");
					}
				};
				mThread.start();
			}
		}
	}

	public void onStop() throws InterruptedException {
		synchronized (threadEnabled) {
			threadEnabled = false;
			mThread.join();
			mThread = null;
		}
	}

	private Object mServerConnectionLock = new Object();

	private class BluetoothConnection {
		private BluetoothDevice mServerDevice;
		private BluetoothSocket mSocket;
		private UUID mUuid;

		public BluetoothConnection(BluetoothDevice serverDevice, UUID uuid) throws IOException {
			mServerDevice = serverDevice;
			mUuid = uuid;
			mSocket = mServerDevice.createRfcommSocketToServiceRecord(mUuid);
		}

		public void connect() throws IOException {
			mLogger.info("Connecting to " + mServerDevice + " as " + mUuid);
			mSocket.connect();
			mLogger.info("Connected to " + mServerDevice + " as " + mUuid);
		}

		public void close() throws IOException {
			mLogger.info("Close the connection to " + mServerDevice);
			if (mSocket != null) {
				mSocket.close();
			}
			mSocket = null;
		}

		public BluetoothSocket getBluetoothSocket() {
			return mSocket;
		}
	}

	public BluetoothSocket getBluetoothSocket() {
		synchronized (mServerConnectionLock) {
			if (mServerConnection != null) {
				return mServerConnection.getBluetoothSocket();
			}
		}
		return null;
	}

	private void connectServer() throws IOException {
		synchronized (mServerConnectionLock) {
			if (mServerConnection != null) {
				mServerConnection.close();
			}
			mServerConnection = new BluetoothConnection(mBluetoothModel.getServerDevice(),
					BluetoothModel.MY_UUID);
			new Thread() {
				@Override
				public void run() {
					try {
						mServerConnection.connect();
						setChanged();
						notifyObservers(NotifyType.SERVER_CONNECTED);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						setChanged();
						notifyObservers(NotifyType.SERVER_CONNECT_FAILED);
					}
				}

			}.start();
		}
	}

	@Override
	public void update(Observable observable, Object type) {
		if (type == BluetoothModel.NotifyType.SERVER_DEVICE_CHANGED) {
			mLogger.info("got notify event for server changed.");
			try {
				connectServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				setChanged();
				notifyObservers(NotifyType.SERVER_CONNECT_FAILED);
				e.printStackTrace();
			}
			setChanged();
		}
	}
}
