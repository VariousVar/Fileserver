package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.message.Message;

public interface MessageOperation {
	Message createResponse(Message originalMessage);
}
