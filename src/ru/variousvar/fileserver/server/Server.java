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

/**
 * {@code Server} represent file server. It should execute communication through TCP socket and operate on messages.
 */
public class Server {
    private Path rootPath;
    private ServerSocket serverSocket;

	private final Logger logger = Logger.getLogger("server");

	// TODO rethrow exceptions from different io reasons
    public Server(String rootPath, int port) throws IOException{
	    this(rootPath, port, null);
    }

    public Server(String rootPath, int port, String host) throws IOException {
		this.rootPath = Paths.get(rootPath).toRealPath();

	    if (host != null)
		    this.serverSocket = new ServerSocket(port, 50, Inet4Address.getByName(host));
	    else
	        this.serverSocket = new ServerSocket(port, 50);

	    logger.info("File server configured.");
    }

	/**
	 * Starts a server. Process input messages via communication based on {@code Handler}.
	 */
	public void start() {
		logger.info("File server started");

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
				// not interesting
			}
		}
	}

	/**
	 *  Handler receives new connection and operate over it. It send/receive messages based on {@link Message}.
	 */
	class Handler implements Runnable {
		private Socket socket;

		Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try (Connection connection = new Connection(socket)) {
                logger.info("File server establish new connection with: " + socket.getRemoteSocketAddress());

				while (true) {
					Message message = connection.receive();
					if (message.getMessageType() == MessageType.CLOSE) {
                        connection.close();
                        break;
                    }

                    MessageOperation operation = MessageOperationFactory.get(message.getMessageType());
					Message outMessage = operation.createResponse(
							message.withProperty("rootPath", rootPath)
					);

					connection.send(outMessage);
				}
			} catch (IOException | ClassNotFoundException e) {
				logger.warning(
                        "Communication error with client: " + socket.getRemoteSocketAddress() +
                        ". The reason was: " + e.getMessage());
			}
		}
	}
}
