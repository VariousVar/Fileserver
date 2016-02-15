package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;

public class BadRequestMessageOperation implements MessageOperation {
	@Override
	public Message createResponse(Message originalMessage) {
		return new Message(MessageType.BAD_REQUEST);
	}
}
