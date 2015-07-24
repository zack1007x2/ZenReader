package com.example.zack.zenreader;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class Multicast {

	private static onMRecieveListener mListener;

	private static MulticastSocket multiSocket;
	private static InetAddress group;
	private static NetworkInterface NetInterF;

	public static void send(Context context, String messageStr, onMRecieveListener listener) {
		// Hack Prevent crash (sending should be done using an async task)
		byte[] sendData = messageStr.getBytes();
		mListener = listener;

		try{
			group = getBroadcast(getIpAddress()).getByName(Config.GROUP_ADDRESS);
			multiSocket = new MulticastSocket(Config.PORT);
			multiSocket.joinGroup(new InetSocketAddress(group, Config.PORT), NetInterF);
//			multiSocket.joinGroup(group);
			Log.d("ASDFG", "SENT GROUP = " + group.toString());
			DatagramPacket requestPacket = new DatagramPacket(sendData, sendData.length,
					group, Config.PORT);
			multiSocket.send(requestPacket);
			multiSocket.leaveGroup(new InetSocketAddress(group, Config.PORT), NetInterF);
//			multiSocket.leaveGroup(group);
			multiSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("ASDFG", "SENT IOException = " + e.toString());
		}
	}


	/*
	This has to be run in a background thread
	 */
	public static void startReceive(onMRecieveListener listener) {
		byte[] requestData = new byte[1024];
		Log.d("ASDFG","Ready to receive MultiCast packets");
		mListener = listener;
		try{

			group = getBroadcast(getIpAddress()).getByName(Config.GROUP_ADDRESS);
			multiSocket = new MulticastSocket(Config.PORT);
			multiSocket.joinGroup(new InetSocketAddress(group, Config.PORT), NetInterF);
//			multiSocket.joinGroup(group);
			Log.d("ASDFG", "Receive GROUP = " + group.toString());
			while(true){
				DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length);
				multiSocket.receive(requestPacket);

				String requestString = new String(requestPacket.getData(), 0, requestPacket.getLength());
				Log.d("ASDFG","Got Message = "+ requestString);
				mListener.onMRecieve(requestString);
			}
//			multiSocket.leaveGroup(new InetSocketAddress(group, Config.PORT), NetInterF);
//			multiSocket.leaveGroup(group);
//			multiSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("ASDFG", "Receive IOException = " + e.toString());
		}

	}



	public static InetAddress getIpAddress() {
		InetAddress inetAddress = null;
		InetAddress myAddr = null;

		try {
			int i = 0;
			for (Enumeration< NetworkInterface > networkInterface = NetworkInterface
					.getNetworkInterfaces(); networkInterface.hasMoreElements();) {

				NetworkInterface singleInterface = networkInterface.nextElement();
				Log.d("ASDFG","NetworkInterface"+i +"=" + singleInterface.toString());
				i++;
				for (Enumeration< InetAddress > IpAddresses = singleInterface.getInetAddresses(); IpAddresses
						.hasMoreElements();) {
					inetAddress = IpAddresses.nextElement();
					if(inetAddress!=null)
						Log.d("ASDFG","EXIST ADDRESS = "+inetAddress.toString());

					if (!inetAddress.isLoopbackAddress() && (
							singleInterface.getDisplayName().contains("wlan0") ||
							singleInterface.getDisplayName().contains("eth0") ||
							singleInterface.getDisplayName().contains("ap0"))) {
						if(myAddr!=null){
							Log.d("ASDFG","GET ADDRESS = "+myAddr.toString());}
						myAddr = inetAddress;
					}
				}
			}

		} catch (SocketException ex) {
			Log.e("ASDFG", ex.toString());
		}
		return myAddr;
	}


	public static InetAddress getBroadcast(InetAddress inetAddr) {

		NetworkInterface temp;
		InetAddress iAddr = null;
		try {
			temp = NetworkInterface.getByInetAddress(inetAddr);
			List< InterfaceAddress > addresses = temp.getInterfaceAddresses();
			Log.d("ASDFG","List ADDr size = "+addresses.size());
			for (InterfaceAddress inetAddress: addresses)
				iAddr = inetAddress.getBroadcast();
				Log.d("ASDFG", "iAddr=" + iAddr);
				NetInterF = temp;
			return iAddr;

		} catch (SocketException e) {

			e.printStackTrace();
			Log.d("ASDFG", "getBroadcast" + e.getMessage());
		}
		return null;
	}

	public interface onMRecieveListener{
		void onMRecieve(String packet);
	}
}
