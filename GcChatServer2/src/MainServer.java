import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MainServer {
	private static ServerSocket serverSocket;
	private static Socket socket;
	private static HashMap<Integer, Socket> allSockets = new HashMap<Integer, Socket>();

	// private static ArrayList<Socket> allSockets=new ArrayList<Socket>();

	public static void main(String[] args) {
		try {
			serverSocket = new ServerSocket(9999);
			while (true) {
				socket = serverSocket.accept();
				System.out.println("有用户连接服务器");
				// new ServerMainThread(socket, allSockets).start();
				new ServerMainThread(socket, allSockets).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
