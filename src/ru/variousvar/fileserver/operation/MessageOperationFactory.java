package ru.variousvar.fileserver.operation;

import ru.variousvar.fileserver.message.MessageType;
import ru.variousvar.fileserver.operation.message.*;

import java.util.HashMap;
import java.util.Map;

public class MessageOperationFactory {
	private static Map<MessageType, MessageOperation> map = new HashMap<>();

	static {
		map.put(MessageType.LIST, new ListMessageOperation());
		map.put(MessageType.GET, new GetMessageOperation());
		map.put(MessageType.PATH_SEPARATOR, new PathSeparatorMessageOperation());
	}

	public static MessageOperation get(MessageType type) {
		return map.getOrDefault(type, new BadRequestMessageOperation());
	}
}
