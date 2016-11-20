package ru.variousvar.fileserver.client.context;

/**
 * Context properties for {@link ru.variousvar.fileserver.client.ConsoleClient} operations.
 */
public enum BasicContextProperty implements ContextProperty {
	PATH_SEPARATOR,
	PATH,
	FILE,
	CONTENT,
	DEFAULT_LOCATION,
	FILES,
	LAST_ERROR_MESSAGE,
	BROKEN
}
