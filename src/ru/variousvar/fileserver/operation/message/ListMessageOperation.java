package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.FileDto;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.file.ListFileOperation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ListMessageOperation implements MessageOperation {
	@Override
	public Message createResponse(Message originalMessage) {
		try {
			// TODO secure rootPath from relative path from message to prevent exit out from rootPath
			Path root, operative;
			root = operative = (Path) originalMessage.property("rootPath");
			String relativePath = (String) originalMessage.property("rel");

			if (relativePath != null) {
				operative = operative.resolve(Paths.get(relativePath)).normalize();
				if (!operative.startsWith(root))
					throw new IOException("Attempt to go out server operative bounds.");
			}

			List<FileDto> list = new ListFileOperation().operate(operative);

			return new Message(MessageType.LIST, list);
		} catch (IOException e) {
			return new Message(MessageType.BAD_REQUEST, e.getMessage());
		}
	}
}
