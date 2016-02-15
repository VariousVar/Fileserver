package ru.variousvar.fileserver;

import java.io.File;
import java.io.Serializable;

public class FileDto implements Serializable, Comparable<FileDto> {
	private String name;
	private boolean dir;

	public FileDto(File file) {
		this.name = file.getName();
		this.dir = file.isDirectory();
	}

	public String getName() {
		return name;
	}

	public boolean isDir() {
		return dir;
	}

	@Override
	public String toString() {
		return name + (dir ? " <dir>" : "");
	}

	@Override
	public int compareTo(FileDto o) {
		int compare = Boolean.compare(o.dir, this.dir);

		if (compare != 0)
			compare = o.name.compareTo(this.name);

		return compare;
	}
}
