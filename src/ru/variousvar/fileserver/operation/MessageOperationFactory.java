package ru.variousvar.fileserver.operation;

import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.message.BadRequestMessageOperation;
import ru.variousvar.fileserver.operation.message.GetMessageOperation;
import ru.variousvar.fileserver.operation.message.ListMessageOperation;
import ru.variousvar.fileserver.operation.message.MessageOperation;

import java.util.HashMap;
import java.util.Map;

public class MessageOperationFactory {
	private static Map<MessageType, MessageOperation> map = new HashMap<>();

	static {
		map.put(MessageType.LIST, new ListMessageOperation());
		map.put(MessageType.GET, new GetMessageOperation());
	}

	public static MessageOperation get(MessageType type) {
		return map.getOrDefault(type, new BadRequestMessageOperation());
	}
}
