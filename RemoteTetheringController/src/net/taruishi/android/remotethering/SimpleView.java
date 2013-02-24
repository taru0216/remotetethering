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

package net.taruishi.android.remotethering;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import net.taruishi.android.remotethering.R;
import net.taruishi.android.remotethering.client.ClientView;
import net.taruishi.android.remotethering.client.ClientPresentor;
import net.taruishi.android.remotethering.server.ServerView;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SimpleView implements ClientView, ServerView {

	private static Logger mLogger = Logger.getLogger(SimpleView.class.getName());

	private ClientPresentor mPresentation;

	private Activity mActivity;
	private Handler mHandler;

	private ListView mBondedDevicesList;

	private class BluetoothDeviceItem {
		private final BluetoothDevice mDevice;

		public BluetoothDeviceItem(BluetoothDevice device) {
			mDevice = device;
		}

		public BluetoothDevice getBluetoothDevice() {
			return mDevice;
		}

		public String toString() {
			return mDevice.getName() + "\n" + mDevice.getAddress();
		}
	}

	private ArrayAdapter<BluetoothDeviceItem> mBondedDevicesAdapter;

	public SimpleView(Activity activity, ClientPresentor presentation) {
		mActivity = activity;
		mPresentation = presentation;

		// list
		mBondedDevicesList = (ListView) mActivity.findViewById(R.id.activityMainListView1);

		mBondedDevicesAdapter = new ArrayAdapter<BluetoothDeviceItem>(mActivity,
				android.R.layout.simple_list_item_1, new ArrayList<BluetoothDeviceItem>());
		mBondedDevicesList.setAdapter(mBondedDevicesAdapter);

		mBondedDevicesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, android.view.View view, int position,
					long id) {
				BluetoothDeviceItem item = (BluetoothDeviceItem) parent.getItemAtPosition(position);
				mLogger.info("item clicked: " + item.getBluetoothDevice());
				mPresentation.setServerDevice(item.getBluetoothDevice());
			}
		});

		// handler
		mHandler = new Handler();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.taruishi.android.remotethering.client.IClientView#updateBondedDevices
	 * (java.util.Set)
	 */
	@Override
	public void updateBondedDevices(final Set<BluetoothDevice> devices) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// mLogger.info("Updating bonded devices...");
				mBondedDevicesAdapter.clear();
				for (BluetoothDevice device : devices) {
					mBondedDevicesAdapter.add(new BluetoothDeviceItem(device));
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.taruishi.android.remotethering.client.IClientView#showMessage(java
	 * .lang.String)
	 */
	@Override
	public void showMessage(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

}
