package ru.variousvar.fileserver.server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private Path rootPath;
    private ServerSocket serverSocket;
    private ExecutorService executor = Executors.newCachedThreadPool();

	private final Logger logger = Logger.getLogger("server");

	// TODO rethrow exceptions from different io reasons
    public Server(String rootPath, int port) throws IOException{
	    this(rootPath, port, null);
    }

    public Server(String rootPath, int port, byte[] inet4Address) throws IOException{
		this.rootPath = Paths.get(rootPath).toRealPath();

	    if (inet4Address != null)
		    this.serverSocket = new ServerSocket(port, 50, Inet4Address.getByAddress(inet4Address));
	    else
	        this.serverSocket = new ServerSocket(port, 50);

	    logger.info("File server configured.");
    }

	public void start() {
		logger.info("File server starts to work");

		try {
			Socket accept = serverSocket.accept();
			executor.submit(new Handler(accept));
		} catch (IOException e) {
			logger.severe("Server cannot receive messages");
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// not interesting?
			}
		}
	}

	class Handler implements Runnable {
		private Socket socket;

		Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {

		}
	}
}
