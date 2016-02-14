package ru.variousvar.fileserver.operation.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GetFileOperation implements FileOperation {
	@Override
	public byte[] operate(Path path) throws IOException{
		if (path.toFile().exists())
			return Files.readAllBytes(path);

		throw new IOException("No such file: " + path.toString());
	}
}
