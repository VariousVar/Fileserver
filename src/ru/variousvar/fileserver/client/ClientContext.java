package ru.variousvar.fileserver.client;

import ru.variousvar.fileserver.Connection;
import ru.variousvar.fileserver.client.context.ContextProperty;
import ru.variousvar.fileserver.client.state.StateHandler;

/**
 * State execution context. Store connection property, state-oriented object and execution properties.
 */
public interface ClientContext {
	/**
	 * Returns connection between client and server.
	 *
	 * @return client-server connection
	 */
	Connection getConnection();

	/**
	 * Sets client-server connection.
	 *
	 * @param connection client-server connection
	 */
	void setConnection(Connection connection);

	/**
	 * Returns object, that execution flow is state-oriented.
	 *
	 * @return state-oriented object
	 */
	StateHandler getStateHandler();

	/** Sets object, that execution flow is state-oriented.
	 *
	 * @param stateHandler state-oriented object
	 */
	void setStateHandler(StateHandler stateHandler);

	/**
	 * Returns execution property.
	 *
	 * @param property any context property
	 * @return context property value
	 */
	Object getProperty(ContextProperty property);

	/**
	 * Sets execution property.
	 *
	 * @param property any context property
	 * @param value context property value
	 */
	void setProperty(ContextProperty property, Object value);
}
