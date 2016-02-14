package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.message.Message;

public class BadRequestMessageOperation implements MessageOperation {
	@Override
	public Message createResponse(Message originalMessage) {
		return null;
	}
}
