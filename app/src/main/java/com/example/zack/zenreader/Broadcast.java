package com.example.zack.zenreader;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;


public class Broadcast {

	private static onRecieveListener mListener;



	public static void send(Context context, String messageStr, onRecieveListener listener) {
		// Hack Prevent crash (sending should be done using an async task)
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		mListener = listener;
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] sendData = messageStr.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					getBroadcast(getIpAddress()),Config.PORT);

			socket.send(sendPacket);
		} catch (IOException e) {
			Log.d("ASDFG","SEND IOException = "+e.toString());
		}
	}

//	static InetAddress getBroadcastAddress(Context context) throws IOException {
//		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		DhcpInfo dhcp = wifi.getDhcpInfo();
//		int broadcast  = dhcp.ipAddress | ~dhcp.netmask;
//		Log.d("ASDFG","broadcast = " + broadcast);
//
//		byte[] quads = new byte[4];
//
//		for (int k = 0; k < 4; k++) {
//			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//		}
//		return InetAddress.getByAddress(quads);
//	}

	/*
	This has to be run in a background thread
	 */
	public static void startReceive(onRecieveListener listener) {
		try {
			//Keep a socket open to listen to all the UDP trafic that is destined for this port
			DatagramSocket socket = new DatagramSocket(Config.PORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			mListener = listener;

			while (true) {
				Log.i("ASDFG", "Ready to receive broadcast packets!");

				//Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);

				//Packet received
				Log.i("ASDFG", "Packet received from: " + packet.getAddress().getHostAddress());
				String data = new String(packet.getData()).trim();
				Log.i("ASDFG", "Packet received; data: " + data);
				mListener.onRecieve(data);
			}
		} catch (IOException ex) {

		}
	}




	public static InetAddress getIpAddress() {
		InetAddress inetAddress = null;
		InetAddress myAddr = null;

		try {
			for (Enumeration < NetworkInterface > networkInterface = NetworkInterface
					.getNetworkInterfaces(); networkInterface.hasMoreElements();) {

				NetworkInterface singleInterface = networkInterface.nextElement();

				for (Enumeration< InetAddress > IpAddresses = singleInterface.getInetAddresses(); IpAddresses
						.hasMoreElements();) {
					inetAddress = IpAddresses.nextElement();

					if (!inetAddress.isLoopbackAddress() && (singleInterface.getDisplayName()
							.contains("wlan0") ||
							singleInterface.getDisplayName().contains("eth0") ||
							singleInterface.getDisplayName().contains("ap0"))) {

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

			for (InterfaceAddress inetAddress: addresses)

				iAddr = inetAddress.getBroadcast();
			Log.d("ASDFG", "iAddr=" + iAddr);
			return iAddr;

		} catch (SocketException e) {

			e.printStackTrace();
			Log.d("ASDFG", "getBroadcast" + e.getMessage());
		}
		return null;
	}




	public interface onRecieveListener{
		void onRecieve(String packet);
	}
}
