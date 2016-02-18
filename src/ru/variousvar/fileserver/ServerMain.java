package ru.variousvar.fileserver;

import ru.variousvar.fileserver.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerMain {
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String host = null;
		int port = -1;
		String rootFolder = null;

		// choose host
		System.out.println("Enter host (press 'Enter' for localhost): ");
		host = reader.readLine();
		if ("".equals(host)) host = "localhost";

		// choose port number
		System.out.println("Enter port number: [0-65535]");
		while (port < 0) {
			try {
				port = Integer.parseInt(reader.readLine());
				if (port > 0 && port < 1<<16)
				break;
			} catch (NumberFormatException ex) {
				System.out.println("Only digits allowed");}
		}

		// choose file server folder
		System.out.println("Enter file server folder: ");
		rootFolder = reader.readLine();

		Server server = new Server(rootFolder, port, host);
		server.start();
	}
}
