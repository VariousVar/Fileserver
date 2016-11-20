package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.context.BasicContextProperty;
import ru.variousvar.fileserver.client.ClientContext;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;

import java.io.IOException;

/**
 * Request files related to chosen directory. In success, transfer to {@link PrintFileListState}.
 */
public class LoadFileListState implements State {

	@Override
	public void execute(ClientContext context) throws IOException, ClassNotFoundException {
		Message send = new Message(MessageType.LIST).withProperty("rel", context.getProperty(BasicContextProperty.PATH));
		context.getConnection().send(send);

		Message receive = context.getConnection().receive();
		if (receive.getMessageType() == MessageType.LIST) {
			context.setProperty(BasicContextProperty.FILES, receive.getData());
			context.getStateHandler().setState(new PrintFileListState());
		} else if (receive.getMessageType() == MessageType.BAD_REQUEST) {
			context.setProperty(BasicContextProperty.LAST_ERROR_MESSAGE, send);
			context.getStateHandler().setState(new BrokenState());
		}
	}
}
