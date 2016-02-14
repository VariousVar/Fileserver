package ru.variousvar.fileserver.server;

import ru.variousvar.fileserver.Connection;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.MessageOperationFactory;
import ru.variousvar.fileserver.operation.message.MessageOperation;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Server {
    private Path rootPath;
    private ServerSocket serverSocket;

	private final Logger logger = Logger.getLogger("server");

	// TODO rethrow exceptions from different io reasons
    public Server(String rootPath, int port) throws IOException{
	    this(rootPath, port, null);
    }

    public Server(String rootPath, int port, byte[] inet4Address) throws IOException {
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
			while (true) {
				Socket accept = serverSocket.accept();
				new Thread(new Handler(accept)).start();
			}
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
			try (Connection connection = new Connection(socket)) {

				while (true) {
					Message message = connection.receive();
					// TODO add connection auto closing
					if (message.getMessageType() == MessageType.CLOSE)
						break;

					MessageOperation operation = MessageOperationFactory.get(message.getMessageType());
					Message outMessage = operation.createResponse(
							message.withProperty("rootPath", rootPath)
					);

					connection.send(outMessage);
				}
			} catch (IOException | ClassNotFoundException e) {
				logger.severe("Communication error with client: " + socket.getRemoteSocketAddress());
			}
		}
	}
}
