package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.file.GetFileOperation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetMessageOperation implements MessageOperation {
	@Override
	public Message createResponse(Message originalMessage) {
		Path root = (Path) originalMessage.property("rootPath");
		String rel = (String) originalMessage.property("rel");
		String file = (String) originalMessage.property("file");

		if (rel != null)
			root = root.resolve(Paths.get(rel));

		root = root.resolve(file);

		try {
			return new Message(MessageType.SEND_FILE, new GetFileOperation().operate(root));
		} catch (IOException e) {
			return new Message(MessageType.BAD_REQUEST, e.getMessage());
		}
	}
}
