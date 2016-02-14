package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.file.ListFileOperation;

import java.nio.file.Path;

public class ListMessageOperation implements MessageOperation {
	@Override
	public Message createResponse(Message originalMessage) {

		return new Message(
				MessageType.LIST,
				new ListFileOperation()
						.operate((Path) originalMessage.property("rootPath")));
	}
}
