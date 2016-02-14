package ru.variousvar.fileserver.operation.file;

import ru.variousvar.fileserver.message.MessageType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListFileOperation implements FileOperation {
	@Override
	public List<String> operate(Path path) throws IOException{

		return Files.list(path).map(path::relativize).map(Object::toString).collect(Collectors.toList());
	}
}
