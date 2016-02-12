package ru.variousvar.fileserver.message;

public class Message {
	private MessageType messageType;
	private Object data;

	public Message (MessageType messageType) {
		this.messageType = messageType;
	}

	public Message (MessageType messageType, Object data) {
		this.messageType = messageType;
		this.data = data;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public Object getData() {
		return data;
	}
}
