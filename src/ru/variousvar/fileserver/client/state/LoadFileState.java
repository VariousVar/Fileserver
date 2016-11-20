package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.ClientContext;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;

import java.io.IOException;

import static ru.variousvar.fileserver.util.ConsoleHelper.writeMessage;
import static ru.variousvar.fileserver.client.context.BasicContextProperty.*;

/**
 * Receives selected file from server. In success case, transfer to {@link SaveFileState}.
 */
public class LoadFileState implements State {
	@Override
	public void execute(ClientContext context) throws IOException, ClassNotFoundException {
		Message send = new Message(MessageType.GET)
				.withProperty("rel", context.getProperty(PATH))
				.withProperty("file", context.getProperty(FILE));
		context.getConnection().send(send);

		Message fileMessage = context.getConnection().receive();
		if (!(fileMessage.getMessageType() == MessageType.SEND_FILE)) {
			context.setProperty(BROKEN, fileMessage);
			context.setProperty(LAST_ERROR_MESSAGE, send);
			context.getStateHandler().setState(new BrokenState());
			writeMessage("File cannot be received");
			return;
		}

		context.setProperty(CONTENT, fileMessage.getData());
		context.getStateHandler().setState(new SaveFileState());
	}
}
