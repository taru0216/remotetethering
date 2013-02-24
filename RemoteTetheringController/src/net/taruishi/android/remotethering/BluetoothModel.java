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

import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.logging.Logger;

import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothDevice;

public class BluetoothModel extends Observable {

	public static enum NotifyType {
		SERVER_DEVICE_CHANGED, CANDIDATE_SERVERS_CHANGED
	};

	public static final UUID MY_UUID = UUID.nameUUIDFromBytes(MainActivity.class.getPackage()
			.getName().getBytes());

	private static Logger mLogger = Logger.getLogger(BluetoothModel.class.getName());

	private List<Device> mCandidateServers;
	private BluetoothDevice mServerDevice;

	public List<Device> getCandidateServers() {
		return mCandidateServers;
	}

	public void setCandidateServers(List<Device> candidateServers) {
		this.mCandidateServers = candidateServers;
		notifyImmediately(NotifyType.CANDIDATE_SERVERS_CHANGED);
	}

	public void setServerDevice(BluetoothDevice serverDevice) {
		mLogger.info("updating server device to " + serverDevice);
		mServerDevice = serverDevice;
		notifyImmediately(NotifyType.SERVER_DEVICE_CHANGED);
	}

	public BluetoothDevice getServerDevice() {
		return mServerDevice;
	}

	private void notifyImmediately(Object data) {
		setChanged();
		notifyObservers(data);
	}

}
