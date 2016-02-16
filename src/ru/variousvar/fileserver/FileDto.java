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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDto fileDto = (FileDto) o;

        if (dir != fileDto.dir) return false;
        if (!name.equals(fileDto.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (dir ? 1 : 0);
        return result;
    }

    @Override
	public String toString() {
		return name + (dir ? " <dir>" : "");
	}

	@Override
	public int compareTo(FileDto o) {
		int compare = Boolean.compare(o.dir, this.dir);

		if (compare == 0)
			compare = this.name.compareTo(o.name);

		return compare;
	}
}
