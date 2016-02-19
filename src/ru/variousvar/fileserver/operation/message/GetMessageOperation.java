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
		try {
			Path root, operative;
			root = operative = (Path) originalMessage.property("rootPath");
			String rel = (String) originalMessage.property("rel");
			String file = (String) originalMessage.property("file");

			if (rel != null)
				operative = operative.resolve(rel).normalize();
			if (file != null)
				operative = operative.resolve(file).normalize();

			if (!operative.startsWith(root))
				throw new IOException("Attempt to go out server operative bounds.");


			return new Message(MessageType.SEND_FILE, new GetFileOperation().operate(operative));
		} catch (IOException e) {
			return new Message(MessageType.BAD_REQUEST, e.getMessage());
		}
	}
}
