package ru.variousvar.fileserver.operation.file;

import java.io.IOException;
import java.nio.file.Path;

public interface FileOperation {
	Object operate(Path path) throws IOException;
}
