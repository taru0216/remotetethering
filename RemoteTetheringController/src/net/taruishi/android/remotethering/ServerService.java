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

import net.taruishi.android.remotethering.server.Server;
import net.taruishi.android.remotethering.server.ServerPresentor;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ServerService extends Service {

	private static Logger mLogger = Logger.getLogger(ServerService.class.getName());

	private final IBinder mBinder = new LocalBinder();
	private BluetoothModel mBluetoothModel;
	private Server mServer;
	private ServerPresentor mServerPresentor;

	@Override
	public void onCreate() {
		mLogger.info("creating server service");
		// Domain model
		mBluetoothModel = new BluetoothModel();
		mServer = new Server(this, mBluetoothModel);

		// Presentor
		mServerPresentor = new ServerPresentor(this, mBluetoothModel);
		mServer.addObserver(mServerPresentor);

		mServer.onCreate();
	}

	public class LocalBinder extends Binder {
		ServerService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return ServerService.this;
		}
	}

	public ServerPresentor getServerPresentor() {
		return mServerPresentor;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		mLogger.info("destroying server service");

	}

}
