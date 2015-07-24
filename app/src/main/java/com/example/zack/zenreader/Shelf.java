package com.example.zack.zenreader;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Zack on 15/6/29.
 */
public class Shelf extends Activity implements Multicast.onMRecieveListener {
    private Button btSendM,btBCMode;
    private EditText etMessage;
    private MultiPacketReceiverTask startRecieveTask;
    private final static int RECIEVE_MESSAGE = 1;
    private String mPacket;
    WifiManager.MulticastLock mLock;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECIEVE_MESSAGE:
                    etMessage.setText(mPacket);
                    Toast.makeText(Shelf.this, "Recieve packet content = " + mPacket, Toast
                            .LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btSendM = (Button)findViewById(R.id.btSend);
        btBCMode = (Button)findViewById(R.id.btMode);
        btSendM.setText("Send MultiCast");
        btBCMode.setText("BroadCastMode");
        etMessage = (EditText)findViewById(R.id.etMessage);

        btSendM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Shelf.this,etMessage.getText().toString(),Toast.LENGTH_SHORT);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Multicast.send(Shelf.this, etMessage.getText().toString(), Shelf.this);
                    }
                }).start();


            }
        });
        btBCMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ASDFG","onResume ");
        startRecieveTask = new MultiPacketReceiverTask();
        startRecieveTask.execute(Shelf.this);

        WifiManager wifi = (WifiManager) getSystemService(getApplicationContext().WIFI_SERVICE);
        if(wifi!=null) {
            mLock = wifi.createMulticastLock("mylock");
            mLock.acquire();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        startRecieveTask.cancel(true);
        if(mLock.isHeld())
            mLock.release();
    }




    @Override
    public void onMRecieve(String packet) {
        mPacket = packet;
        mHandler.sendEmptyMessage(RECIEVE_MESSAGE);
    }
}
