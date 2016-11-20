package ru.variousvar.fileserver.client.state;

/**
 * Operation with state-oriented object.
 */
public interface StateHandler {
	void setState(State state);
	State getState();
}
