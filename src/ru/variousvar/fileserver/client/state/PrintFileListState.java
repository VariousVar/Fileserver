package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.context.BasicContextProperty;
import ru.variousvar.fileserver.FileDto;
import ru.variousvar.fileserver.client.ClientContext;

import java.util.List;

import static ru.variousvar.fileserver.client.state.StateUtils.getFormattedDto;
import static ru.variousvar.fileserver.util.ConsoleHelper.writeMessage;

/**
 * Prints current, user selected directory content.
 */
public class PrintFileListState implements State {

	@Override
	public void execute(ClientContext context) {
		String path = (String)context.getProperty(BasicContextProperty.PATH);
		writeMessage("--------------------------------");
		writeMessage("Current path is '" + context.getProperty(BasicContextProperty.PATH_SEPARATOR) + path + "'. ");
		writeMessage("Files: ");
		writeMessage("===============");
		if (!path.isEmpty()) writeMessage("      ..");

		for (FileDto fileDto : (List<FileDto>) context.getProperty(BasicContextProperty.FILES)) {
			writeMessage(getFormattedDto(fileDto));
		}

		writeMessage("===============");
		context.getStateHandler().setState(new MakeChoiceState());
	}
}
