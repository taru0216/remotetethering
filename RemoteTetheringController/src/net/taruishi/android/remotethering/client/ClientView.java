package net.taruishi.android.remotethering.client;

import java.util.Set;

import net.taruishi.android.remotethering.View;

import android.bluetooth.BluetoothDevice;

public interface ClientView extends View {
	public void updateBondedDevices(Set<BluetoothDevice> devices);
}