package ru.variousvar.fileserver.client;

import ru.variousvar.fileserver.Connection;
import ru.variousvar.fileserver.ErrorType;
import ru.variousvar.fileserver.FileDto;
import ru.variousvar.fileserver.exception.ClientCommunicationException;
import ru.variousvar.fileserver.message.Message;
import ru.variousvar.fileserver.message.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

import static ru.variousvar.fileserver.util.ConsoleHelper.*;

public class SimpleConsoleClient implements Client {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private Connection connection;

    private String FILE_SEPARATOR;
    private String path = "";
    private String nextPath = "";
    private String file = "";
    private Path defaultLocation;
    private List<FileDto> files;

	private Message lastErrorMessage;
	private String errorType;

	private boolean broken = false;
	private boolean recoverable = true;
    private boolean exit;

    public SimpleConsoleClient(int port) throws IOException {
		Socket socket = new Socket("localhost", port);
		this.connection = new Connection(socket);
	}

	public SimpleConsoleClient(String host, int port) throws IOException {
		Socket socket = new Socket(host, port);
		this.connection = new Connection(socket);
	}

	@Override
	public void start() {
		try {
			getPathSeparator();

            while (true) {
	            getFiles();
                printFiles();
                makeChoice();
                String nextOperation = prepareAndGetNextOp();

                switch (nextOperation) {
                    case "dir":
                        // we already make all preparations, so just go to start
                        continue;
                    case "file":
                        saveFile();
                        askForClearChoice();
                        break;
                    case "nop":
                        continue;
                    case "exit":
                        endCommunication();
                        return;
                }
            }
        } catch (ClassNotFoundException e) {
            ClientCommunicationException ex = new ClientCommunicationException("Client don't understand data type");
            ex.initCause(e);
            throw ex;
        } catch (IOException e) {
            ClientCommunicationException ex = new ClientCommunicationException("Cannot receive message due to reason: " + e.getMessage());
            ex.initCause(e);
            throw ex;
        }
	}

    /**
     * Check client consistency, which may be broken after any operation that perform network connection operation.
     * It try to recover client state and throw exception if it not possible.
     *
     * @throws ClientCommunicationException if client recovering is not possible.
     */
	protected void checkConsistency() {
		if (broken) {
			tryToRecover();

			if (broken) {
				if (lastErrorMessage != null)
					throw new ClientCommunicationException(
							"Client broken due to reason: " + lastErrorMessage.getData());
				else
					throw new ClientCommunicationException(
							"Client broken due to communication reason");
			}

		}

	}

	protected void tryToRecover() {
        // not yet implemented
	}

	protected void getPathSeparator() throws IOException, ClassNotFoundException {
		Message send = new Message(MessageType.FILE_SEPARATOR);
		connection.send(send);

		Message receive = connection.receive();
		if (receive.getMessageType() == MessageType.FILE_SEPARATOR)
			FILE_SEPARATOR = (String) receive.getData();
		else if (receive.getMessageType() == MessageType.BAD_REQUEST) {
			lastErrorMessage = send;
			errorType = (String) receive.getData();
			broken = true;
			recoverable = false;
		}

        checkConsistency();

	}

	protected void getFiles() throws IOException, ClassNotFoundException {
		Message send = new Message(MessageType.LIST).withProperty("rel", path);
		connection.send(send);

		Message receive = connection.receive();
		if (receive.getMessageType() == MessageType.LIST)
			files = (List<FileDto>) receive.getData();
		else if (receive.getMessageType() == MessageType.BAD_REQUEST) {
			broken = true;
			lastErrorMessage = send;
		}
        checkConsistency();
	}

    protected void printFiles() {
		writeMessage("--------------------------------");
        writeMessage("Current path is '" + FILE_SEPARATOR + path + "'. ");
        writeMessage("Files: ");
	    writeMessage("===============");
        if (!path.isEmpty()) writeMessage("      ..");
        for (FileDto fileDto : files) {
            writeMessage(getFormattedDto(fileDto));
        }

	    writeMessage("===============");
    }
    private String getFormattedDto(FileDto fileDto) {
        return (fileDto.isDir() ? "<dir> " : "      ") + fileDto.getName() ;
    }

    protected void makeChoice() {
        writeMessage("Choose file to download or directory to view.");
	    writeMessage("Type '..' to go up.");
        writeMessage("Press enter to end communication.");
        // clear previous choose
        nextPath = "";
        file = "";
        exit = false;

        FileDto choice = null;
        while (true) {
            String filename = readString();

	        if (filename.isEmpty()) {
                exit = true;
                return;
            }

            if ("..".equals(filename)) {
                nextPath = filename;
                return;
            }

            List<FileDto> filtered = files.parallelStream()
                    .filter(file -> file.getName().toLowerCase().startsWith(filename))
                    .collect(Collectors.toList());

            // checking for file and dir can have equal names
            if (filtered.size() == 0) {
                writeMessage("No such file. Type new name.");
            } else if (filtered.size() == 1) {
                choice = filtered.get(0);
                break;
            } else {
                writeMessage("Choose one, type a number:");
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

        if (choice.isDir())
            nextPath = choice.getName();
        else
            file = choice.getName();

	    writeMessage("Chosen file: " + choice);
	    if (!choice.isDir()) {
		    writeMessage("Are you sure? [yes/no]");
		    if (!askYesNo()) {
			    nextPath = "";
			    file = "";
		    }
	    }
    }

    protected String prepareAndGetNextOp() {
        if (exit)
            return "exit";
        else if (nextPath.isEmpty() && file.isEmpty())
            return "nop";
        // operate on path
        else if (!nextPath.isEmpty()) {
            if (nextPath.equals("..")) {
                if (path.contains(FILE_SEPARATOR))
                    path = path.substring(0, path.lastIndexOf(FILE_SEPARATOR));
	            else
	                path = "";
            } else {
	            if (!path.isEmpty()) path += FILE_SEPARATOR;
                path += nextPath;
            }

            return "dir";
        }
        // operate on file
        else if (!file.isEmpty()) {
            return "file";
        }

        return "exit";
    }

    protected void saveFile() throws IOException, ClassNotFoundException {
        Message send = new Message(MessageType.GET)
                .withProperty("rel", path)
                .withProperty("file", file);
        connection.send(send);

        Message fileMessage = connection.receive();
        if (!(fileMessage.getMessageType() == MessageType.SEND_FILE)) {
            broken = true;
            lastErrorMessage = send;
            errorType = (String) fileMessage.getData();
            writeMessage("File cannot be received");
            return;
        }
        byte[] bytes = (byte[]) fileMessage.getData();

        if (defaultLocation != null) {
		    writeMessage("Save to default location? [" + defaultLocation + "]. [yes/no]:");

		    if (askYesNo()) {
			    saveToDefault(bytes);
				return;
		    }
	    }

        while (!saveSomewhere(bytes));

    }

	private boolean saveToDefault(byte[] bytes) {

		Path fullPath = defaultLocation.resolve(file);

		if (Files.isWritable(defaultLocation))
			return createNewOrReplace(fullPath, bytes);
		else {
			writeMessage("Default directory not reachable.");
            defaultLocation = null;
			return saveSomewhere(bytes);
		}
	}

	private boolean saveSomewhere(byte[] bytes) {
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
				if (setDefault) defaultLocation = possiblePath;
                if (clearDefault) defaultLocation = null;
				return createNewOrReplace(possiblePath.resolve(file), bytes);
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
                String newFilename = file;
                int idx = 0;

                while (Files.exists(path.getParent().resolve(newFilename))) {
                    StringBuilder sb = new StringBuilder(file.length());

                    if (file.contains("."))
                        newFilename = sb
                                .append(file.substring(0, file.lastIndexOf(".")))
                                .append("_copy").append(idx++)
                                .append(file.substring(file.lastIndexOf(".")))
                                .toString();
                    else
                        newFilename = file + "_copy" + idx++;
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


    protected void endCommunication() {
        try {
            connection.close();
        } catch (IOException e) {
            // nothing to do
        }
    }

	private boolean askYesNo() {
		String choice;
		while (true) {
			choice = readString().trim();
			if (("yes".equals(choice) || "no".equals(choice))) break;
		}

		return "yes".equals(choice);
	}
}
