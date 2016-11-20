package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.ClientContext;

import java.io.IOException;

import static ru.variousvar.fileserver.util.ConsoleHelper.writeMessage;

/**
 * Closes client-server connection. Prints bye message.
 */
public class ExitChoiceState implements State {
	@Override
	public void execute(ClientContext context) {

		writeMessage("Bye!");
		try {
			context.getConnection().close();
		} catch (IOException e) {
			// nothing to do
		}
	}
}
