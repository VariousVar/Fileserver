package ru.variousvar.fileserver;

import ru.variousvar.fileserver.client.Client;
import ru.variousvar.fileserver.client.ConsoleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientMain {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String host = null;
		int port = -1;
		String rootFolder = null;

		// choose host
		System.out.println("Enter server host (press 'Enter' for localhost): ");
		host = reader.readLine();
		if ("".equals(host)) host = "localhost";

		// choose port number
		System.out.println("Enter server port number: [0-65535]");
		while (port < 0) {
			try {
				port = Integer.parseInt(reader.readLine());
				if (port > 0 && port < 1<<16)
				break;
			} catch (NumberFormatException ex) {
				System.out.println("Only digits allowed");}
		}
		Client client = new ConsoleClient(host, port);
		client.start();
	}
}
