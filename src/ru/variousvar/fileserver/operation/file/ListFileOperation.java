package ru.variousvar.fileserver.operation.file;

import ru.variousvar.fileserver.FileDto;
import ru.variousvar.fileserver.message.MessageType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListFileOperation implements FileOperation {
	@Override
	public List<FileDto> operate(Path path) throws IOException{
		List<FileDto> dtos = Files.list(path)
				.map(path::relativize)
				.map(p -> new FileDto(p.toFile()))
				.sorted()
				.collect(Collectors.toList());

		return dtos;
	}
}
