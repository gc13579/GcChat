package com.example.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SocketUtil {
	private static Socket socket;
	private static ArrayList<Socket> sockets = new ArrayList<Socket>();

	public static Socket getSocket(boolean isDisconnected) {
		if (sockets.size() == 0 || isDisconnected == true) {
			try {
				socket = new Socket(ServerIp.serverIp, 9999);
				sockets.clear();
				sockets.add(socket);
			} catch (UnknownHostException e) {
				System.out.println("SocketUtil±¨´í");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("SocketUtil±¨´í");
				e.printStackTrace();
			}
		} else {
			socket = sockets.get(0);
		}
		return socket;
	}
}
