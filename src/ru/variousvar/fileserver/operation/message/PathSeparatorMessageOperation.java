package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.ErrorType;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.file.PathSeparatorFileOperation;

import java.io.IOException;
import java.nio.file.Path;

public class PathSeparatorMessageOperation implements MessageOperation {
	@Override
	public Message createResponse(Message originalMessage) {

		try {
			return new Message(
					MessageType.PATH_SEPARATOR,
					new PathSeparatorFileOperation().operate((Path) originalMessage.property("rootPath")));
		} catch (IOException e) {
			return new Message(MessageType.BAD_REQUEST, ErrorType.UNKNOWN);
		}
	}
}
