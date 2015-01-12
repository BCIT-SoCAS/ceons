package mtk.eon.io;

import javafx.application.Platform;

public class Logger {

	public enum LoggerLevel {
		NORMAL, DEBUG;
	}
	
	public static LoggerLevel loggerLevel = LoggerLevel.DEBUG;
	
	public static void debug(String message) {
		if (loggerLevel == LoggerLevel.DEBUG)
			if (Platform.isFxApplicationThread()) System.out.println(message);
			else Platform.runLater(() -> System.out.println(message));
	}
	
	public static void info(String message) {
		if (Platform.isFxApplicationThread()) System.out.println(message);
		else Platform.runLater(() -> System.out.println(message));
	}
}