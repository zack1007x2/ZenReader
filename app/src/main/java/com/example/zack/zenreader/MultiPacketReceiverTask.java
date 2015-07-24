package com.example.zack.zenreader;

import android.os.AsyncTask;
import android.util.Log;

public class MultiPacketReceiverTask extends AsyncTask<Multicast.onMRecieveListener , Void, Void> {

	@Override
	protected Void doInBackground(Multicast.onMRecieveListener... listener) {
		Log.d("ASDFG","Start MultiCast Recieve");
		Multicast.startReceive(listener[0]);
		return null;
	}


}
