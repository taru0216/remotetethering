package net.taruishi.android.remotethering;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import android.bluetooth.BluetoothClass.Device;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

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
