package ru.variousvar.fileserver.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
	private MessageType messageType;
	private Object data;
	private Map<String, Object> properties;

	public Message (MessageType messageType) {
		this.messageType = messageType;
	}

	public Message (MessageType messageType, Object data) {
		this.messageType = messageType;
		this.data = data;
	}

	public Message withProperty(String property, Object value) {
		if (properties == null)
			properties = new HashMap<>();

		properties.put(property, value);

		return this;
	}

	public Object property(String property) {
		return properties == null ? null : properties.get(property);
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Object getData() {
		return data;
	}
}
