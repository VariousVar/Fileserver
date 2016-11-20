package ru.variousvar.fileserver.client;

import ru.variousvar.fileserver.Connection;
import ru.variousvar.fileserver.client.context.ContextProperty;
import ru.variousvar.fileserver.client.state.StateHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation for console client.
 */
public class ConsoleClientContext implements ClientContext {
	private StateHandler stateHandlerObject;
	private Connection connection;
	private Map<ContextProperty, Object> properties = new HashMap<>();

	public StateHandler getStateHandler() {
		return stateHandlerObject;
	}

	public void setStateHandler(StateHandler stateHandlerObject) {
		this.stateHandlerObject = stateHandlerObject;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public Object getProperty(ContextProperty key) {
		return properties.get(key);
	}

	public void setProperty(ContextProperty key, Object property) {
		properties.put(key, property);
	}
}
