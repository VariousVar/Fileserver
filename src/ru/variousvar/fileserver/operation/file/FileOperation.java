package ru.variousvar.fileserver.operation.file;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Perform some file operation on received path. May return any object corresponding to concrete operation
 * or throw {@code IOException} if operation cannot be performed.
 */
public interface FileOperation {
	/**
	 * Do operation.
	 *
	 * @param path file operation path
	 * @return corresponding to concrete operation object
	 * @throws IOException if path not exist, not reachable or any IO errors
	 */
	Object operate(Path path) throws IOException;
}
