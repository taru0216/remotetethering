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

import java.util.HashSet;
import java.util.Observer;
import java.util.Set;

import android.content.Context;

import net.taruishi.android.remotethering.client.ClientView;

public abstract class Presentor implements Observer {

	protected Set<ClientView> mViews = new HashSet<ClientView>();

	private Context mContext;

	public Presentor(Context context) {
		mContext = context;
	}

	protected Context getContext() {
		return mContext;
	}

	public void addView(ClientView view) {
		mViews.add(view);
	}

}