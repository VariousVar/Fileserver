package ru.variousvar.fileserver.client;

import ru.variousvar.fileserver.Connection;
import ru.variousvar.fileserver.client.state.InitState;
import ru.variousvar.fileserver.client.state.State;
import ru.variousvar.fileserver.client.state.StateHandler;
import ru.variousvar.fileserver.exception.ClientCommunicationException;

import java.io.IOException;
import java.net.Socket;

public class ConsoleClient implements StateHandler, Client {

	private State currentState = new InitState();
	private ClientContext context = new ConsoleClientContext();

	public ConsoleClient(int port) throws IOException {
		Socket socket = new Socket("localhost", port);
		context.setConnection(new Connection(socket));
	}

	public ConsoleClient(String host, int port) throws IOException {
		Socket socket = new Socket(host, port);
		context.setConnection(new Connection(socket));
	}

	@Override
	public void start() {
		context.setStateHandler(this);
		try {
			while (true) {
				currentState.execute(context);
			}
		} catch (ClassNotFoundException e) {
			ClientCommunicationException ex = new ClientCommunicationException("Client don't understand data type");
			ex.initCause(e);
			throw ex;
		} catch (IOException e) {
			ClientCommunicationException ex = new ClientCommunicationException("Cannot receive message due to reason: " + e.getMessage());
			ex.initCause(e);
			throw ex;
		}
	}

	@Override
	public void setState(State state) {
		currentState = state;
	}

	@Override
	public State getState() {
		return currentState;
	}
}
