package nia.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by honey.
 *
 * Listing 1.1 Blocking I/O example
 */
public class BlockingIoExample {

	/**
	 * Listing 1.1 Blocking I/O example
	 */
	public void serve(int portNumber) {
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			Socket clientSocket = serverSocket.accept();
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

			String request;
			String response;

			while ((request = in.readLine()) != null) {
				if ("Done".equals(request)) {
					break;
				}
				response = processRequest(request);
				out.println(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String processRequest(String request) {
		System.out.println(request);
		return "Processed";
	}

	public static void main(String[] args) {
		BlockingIoExample blockingIoExample = new BlockingIoExample();
		blockingIoExample.serve(25);
	}
}
