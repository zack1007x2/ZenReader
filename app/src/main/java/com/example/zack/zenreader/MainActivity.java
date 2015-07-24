package com.example.zack.zenreader;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity implements Broadcast.onRecieveListener {

    private Button btSendB, btMCMode;
    private EditText etMessage;
    private WifiManager wm;
    private WifiConfiguration wifiCon;
//    private Method wifiApConfigurationMethod;
//    private String buffer;
//    private Thread networkThread;
    private PacketReceiverTask startRecieveTask;
    private final static int RECIEVE_MESSAGE = 1;
    private String mPacket;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECIEVE_MESSAGE:
                    etMessage.setText(mPacket);
                    Toast.makeText(MainActivity.this,"Recieve packet content = "+mPacket,Toast
                            .LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btSendB = (Button)findViewById(R.id.btSend);
        btMCMode = (Button)findViewById(R.id.btMode);
        etMessage = (EditText)findViewById(R.id.etMessage);
        btSendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Broadcast.send(MainActivity.this, etMessage.getText().toString(), MainActivity.this);
            }
        });
        btMCMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this,Shelf.class);
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        startRecieveTask = new PacketReceiverTask();
        startRecieveTask.execute(MainActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        startRecieveTask.cancel(true);
    }


    @Override
    public void onRecieve(String packet) {
        mPacket = packet;
        mHandler.sendEmptyMessage(RECIEVE_MESSAGE);
    }
}
