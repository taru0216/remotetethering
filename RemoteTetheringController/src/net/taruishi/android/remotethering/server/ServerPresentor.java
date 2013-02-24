package net.taruishi.android.remotethering.server;

import java.util.Observable;

import net.taruishi.android.remotethering.BluetoothModel;
import net.taruishi.android.remotethering.Presentor;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public class ServerPresentor extends Presentor {

	private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	public ServerPresentor(Context context, BluetoothModel blueoothModel) {
		super(context);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
