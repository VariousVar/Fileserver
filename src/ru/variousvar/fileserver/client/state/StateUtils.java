package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.FileDto;

import static ru.variousvar.fileserver.util.ConsoleHelper.readString;

public class StateUtils {

	/**
	 * Print pretty {@link FileDto} info.
	 *
	 * @param fileDto file object
	 * @return pretty formatted string
	 */
	public static String getFormattedDto(FileDto fileDto) {
		return (fileDto.isDir() ? "<dir> " : "      ") + fileDto.getName() ;
	}

	/**
	 * Returns user yes/no choice
	 *
	 * @return true, if user typed `yes`
	 */
	public static boolean askYesNo() {
		String choice;
		while (true) {
			choice = readString().trim();
			if (("yes".equals(choice) || "no".equals(choice))) break;
		}

		return "yes".equals(choice);
	}
}
