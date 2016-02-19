package ru.variousvar.fileserver.client;

import ru.variousvar.fileserver.exception.ClientCommunicationException;

import java.io.IOException;

/**
 * {@code Client} represent any client for communication, that can be started and proceed execution.
 */
public interface Client {
	/**
	 * Starts client.
	 *
	 * @throws ClientCommunicationException if client cannot send or receive messages or it receive inoperable message
	 */
    void start();
}
