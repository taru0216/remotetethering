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

import java.util.logging.Logger;

import net.taruishi.android.remotethering.ServerService.LocalBinder;
import net.taruishi.android.remotethering.client.Client;
import net.taruishi.android.remotethering.client.ClientPresentor;
import net.taruishi.android.remotethering.server.Server;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;

public class MainActivity extends Activity {

	private static Logger mLogger = Logger.getLogger(MainActivity.class.getName());

	private BluetoothModel mBluetoothModel;
	private ClientPresentor mPresentation;
	private SimpleView mView;
	private Client mClient;
	private Server mServer;
	private boolean mBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// domain model
		mBluetoothModel = new BluetoothModel();

	
		Intent intent = new Intent(this, ServerService.class);
		startService(intent);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		private ServerService mService;

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			mLogger.info("bound to server service");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

		mClient = new Client(this, mBluetoothModel);

		mPresentation = new ClientPresentor(this, mClient);

		mView = new SimpleView(this, mPresentation);
		mPresentation.addView(mView);

		mClient.addObserver(mPresentation);

		mClient.onStart();

		Intent intent = new Intent(this, ServerService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			mClient.onStop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Unbind from the service
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
