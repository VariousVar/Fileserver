package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.context.BasicContextProperty;
import ru.variousvar.fileserver.FileDto;
import ru.variousvar.fileserver.client.ClientContext;

import java.util.List;
import java.util.stream.Collectors;

import static ru.variousvar.fileserver.client.state.StateUtils.askYesNo;
import static ru.variousvar.fileserver.client.state.StateUtils.getFormattedDto;
import static ru.variousvar.fileserver.util.ConsoleHelper.readInt;
import static ru.variousvar.fileserver.util.ConsoleHelper.readString;
import static ru.variousvar.fileserver.util.ConsoleHelper.writeMessage;

/**
 * Main user activity state. Offer user to choose file or dir, exit from application, confirm him choice, or specify
 * choice in case of ambiguity. Transfer to {@link ExitChoiceState}, {@link LoadFileState} or {@link LoadFileListState}.
 */
public class MakeChoiceState implements State {

	@Override
	public void execute(ClientContext context) {
		final String pathSeparator = (String) context.getProperty(BasicContextProperty.PATH_SEPARATOR);
		final String currentPath = (String) context.getProperty(BasicContextProperty.PATH);

		writeMessage("Choose file to download or directory to view.");
		writeMessage("Type '..' to go up.");
		writeMessage("Press enter to end communication.");
		// clear previous choose
		context.setProperty(BasicContextProperty.FILE, "");

		FileDto choice = null;
		while (true) {
			String userChoice = readString();

			// user want to exit
			if (userChoice.isEmpty()) {
				context.getStateHandler().setState(new ExitChoiceState());
				return;
			}

			// user choose go up
			if ("..".equals(userChoice)) {
				if (currentPath.contains(pathSeparator))
					context.setProperty(BasicContextProperty.PATH, currentPath.substring(0, currentPath.lastIndexOf(pathSeparator)));
				else
					context.setProperty(BasicContextProperty.PATH, "");
				context.getStateHandler().setState(new LoadFileListState());
				return;
			}

			// user choose file or dir
			String lowerUserChoice = userChoice.toLowerCase();
			@SuppressWarnings("unchecked")
			List<FileDto> filtered = ((List<FileDto>)context.getProperty(BasicContextProperty.FILES))
					.parallelStream()
					.filter(file -> file.getName().toLowerCase().startsWith(lowerUserChoice.toLowerCase()))
					.collect(Collectors.toList());

			// checking for file and dir can have equal names
			if (filtered.size() == 0) {
				writeMessage("No such file. Type new name.");
			} else if (filtered.size() == 1) {
				choice = filtered.get(0);
				break;
			} else {
				writeMessage("Choose one, type a number: ");
				int index = 1;
				for (FileDto fileDto : filtered) {
					writeMessage(index++ + ". " + getFormattedDto(fileDto));
				}

				int chosenIndex = -1;
				while (chosenIndex < 0 || chosenIndex >= filtered.size()) chosenIndex = readInt() - 1;

				choice = filtered.get(chosenIndex);
				break;
			}
		}

		writeMessage("Choose: " + choice);
		if (choice.isDir()) {
			String newPath = currentPath;
			if (!currentPath.isEmpty()) newPath += pathSeparator;
			context.setProperty(BasicContextProperty.PATH, newPath + choice.getName());
			context.getStateHandler().setState(new LoadFileListState());
		} else {
			writeMessage("Are you sure? [yes/no]");
			if (!askYesNo()) {
				context.setProperty(BasicContextProperty.FILE, "");
				context.getStateHandler().setState(new MakeChoiceState());
			} else {
				context.setProperty(BasicContextProperty.FILE, choice.getName());
				context.getStateHandler().setState(new LoadFileState());
			}
		}
	}
}
