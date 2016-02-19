package ru.variousvar.fileserver.operation.message;

import ru.variousvar.fileserver.message.Message;

/**
 * Represent operation on {@link Message} that handle it and decide what message will be returned.
 */
public interface MessageOperation {
	/**
	 * Handles message.
	 * @param originalMessage original or received message
	 * @return new message according to actual message-based logic
	 */
	Message createResponse(Message originalMessage);
}
