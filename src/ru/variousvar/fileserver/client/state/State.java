package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.ClientContext;

import java.io.IOException;

/**
 * Object state.
 */
public interface State {

	/**
	 * State execution logic.
	 *
	 * @param context properties for state execution and transfer process.
	 *
	 * @throws IOException if connection has broken
	 * @throws ClassNotFoundException if uninterpretable class has received during execution
	 */
	void execute(ClientContext context) throws IOException, ClassNotFoundException;
}
