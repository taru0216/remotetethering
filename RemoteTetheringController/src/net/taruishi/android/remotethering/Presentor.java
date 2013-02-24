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