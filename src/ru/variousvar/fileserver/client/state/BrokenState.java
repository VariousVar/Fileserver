package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.context.BasicContextProperty;
import ru.variousvar.fileserver.client.ClientContext;
import ru.variousvar.fileserver.message.Message;

import static ru.variousvar.fileserver.util.ConsoleHelper.writeMessage;

/**
 * Realize broken communication between client and server.
 */
public class BrokenState implements State {
	private static final String NO_REASON_BROKE = "Client was exited with unknown error.";
	private static final String REASONED_BROKE = "Client was exited with error: ";

	@Override
	public void execute(ClientContext context) {
		Message cause = (Message) context.getProperty(BasicContextProperty.LAST_ERROR_MESSAGE);
		String message = cause == null ? NO_REASON_BROKE : REASONED_BROKE + cause.getData();

		writeMessage(message);
	}
}
