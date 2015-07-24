package com.example.zack.zenreader;

import android.os.AsyncTask;

public class PacketReceiverTask extends AsyncTask<Broadcast.onRecieveListener, Void, Void> {

	@Override
	protected Void doInBackground(Broadcast.onRecieveListener... listener) {
		Broadcast.startReceive(listener[0]);
		return null;
	}


}
