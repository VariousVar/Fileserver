package ru.variousvar.fileserver.client.state;

import ru.variousvar.fileserver.client.ClientContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static ru.variousvar.fileserver.client.context.BasicContextProperty.CONTENT;
import static ru.variousvar.fileserver.client.context.BasicContextProperty.DEFAULT_LOCATION;
import static ru.variousvar.fileserver.client.context.BasicContextProperty.FILE;
import static ru.variousvar.fileserver.client.state.StateUtils.askYesNo;
import static ru.variousvar.fileserver.util.ConsoleHelper.readString;
import static ru.variousvar.fileserver.util.ConsoleHelper.writeMessage;

/**
 * Saves downloaded file to selected user location. Offer a possibility to save location as default.
 */
public class SaveFileState implements State {
	@Override
	public void execute(ClientContext context) throws IOException, ClassNotFoundException {
		if (context.getProperty(DEFAULT_LOCATION) != null) {
			writeMessage("Save to default location? [" + context.getProperty(DEFAULT_LOCATION) + "]. [yes/no]:");

			if (askYesNo()) {
				saveToDefault((byte[]) context.getProperty(CONTENT), context);
				return;
			}
		}

		while (!saveSomewhere((byte[]) context.getProperty(CONTENT), context));
		context.getStateHandler().setState(new LoadFileListState());
	}

	private boolean saveToDefault(byte[] bytes, ClientContext context) {

		Path fullPath = ((Path) context.getProperty(DEFAULT_LOCATION)).resolve((String) context.getProperty(FILE));

		if (Files.isWritable((Path) context.getProperty(DEFAULT_LOCATION)))
			return createNewOrReplace(fullPath, bytes);
		else {
			writeMessage("Default directory not reachable.");
			context.setProperty(DEFAULT_LOCATION, null);
			return saveSomewhere(bytes, context);
		}
	}

	private boolean saveSomewhere(byte[] bytes, ClientContext context) {
		writeMessage("Enter a directory to save file.");
		writeMessage("'-d' before path to save entered location as default.");
		writeMessage("'-c' and path to clear previous default and don't save new.");
		Path possiblePath;
		boolean setDefault = false;
		boolean clearDefault = false;

		while (true) {
			String[] options = readString().trim().split(" +");

			if (options.length == 2) {
				possiblePath = Paths.get(options[1]);
				if ("-d".equals(options[0])) { setDefault = true; }
				if ("-c".equals(options[0])) { clearDefault = true;}

			} else if (options.length == 1) {
				possiblePath = Paths.get(options[0]);
			} else
				continue;

			if (Files.isWritable(possiblePath)) {
				if (setDefault) context.setProperty(DEFAULT_LOCATION, possiblePath);
				else if (clearDefault) context.setProperty(DEFAULT_LOCATION, null);
				return createNewOrReplace(possiblePath.resolve((String) context.getProperty(FILE)), bytes);
			}
			else
				writeMessage("Destination path is not reachable or not exist. Enter new choice");
		}

	}

	private boolean createNewOrReplace(Path path, byte[] bytes) {
		if (Files.exists(path)) {
			writeMessage("File already exists. Override? [yes/no]:");
			if (askYesNo()) {
				try {
					Files.write(path, bytes, StandardOpenOption.WRITE);
				} catch (IOException e) {
					writeMessage("Cannot override file. Save as copy.");
				}
			}
			else {
				// save as copy
				final String fileName = path.getFileName().toString();
				String newFilename = fileName;
				int idx = 0;

				while (Files.exists(path.getParent().resolve(newFilename))) {
					StringBuilder sb = new StringBuilder(fileName.length());

					if (fileName.contains("."))
						newFilename = sb
								.append(fileName.substring(0, fileName.lastIndexOf(".")))
								.append("_copy").append(idx++)
								.append(fileName.substring(fileName.lastIndexOf(".")))
								.toString();
					else
						newFilename = fileName + "_copy" + idx++;
				}

				try {
					Files.write(path.getParent().resolve(newFilename), bytes);

				} catch (IOException e1) {
					writeMessage("Destination path is not reachable or not exist.");
					return false;
				}
			}
		}
		else {
			try {
				Files.write(path, bytes);
			} catch (IOException e) {
				writeMessage("Destination path is not reachable or not exist.");
				return false;
			}
		}
		writeMessage("File was saved. Press 'Enter' to continue.");
		readString();
		return true;
	}

	protected void askForClearChoice() {
		// not yet implemented
	}
}
