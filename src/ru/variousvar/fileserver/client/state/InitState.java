package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.context.BasicContextProperty;
import ru.variousvar.fileserver.Connection;
import ru.variousvar.fileserver.client.ClientContext;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;

import java.io.IOException;

/**
 * Initiation state. Clears properties, set necessary properties. In success case, transfer to {@link LoadFileListState}.
 */
public class InitState implements State {

	@Override
	public void execute(ClientContext context) throws IOException, ClassNotFoundException {
		context.setProperty(BasicContextProperty.PATH, "");
		context.setProperty(BasicContextProperty.FILE, "");

		Message send = new Message(MessageType.FILE_SEPARATOR);
		Connection connection = context.getConnection();

		connection.send(send);
		Message receive = connection.receive();
		if (receive.getMessageType() == MessageType.FILE_SEPARATOR) {
			context.setProperty(BasicContextProperty.PATH_SEPARATOR, receive.getData());
			context.getStateHandler().setState(new LoadFileListState());
		} else if (receive.getMessageType() == MessageType.BAD_REQUEST) {
			context.setProperty(BasicContextProperty.LAST_ERROR_MESSAGE, send);
			context.getStateHandler().setState(new BrokenState());
		}
	}
}
