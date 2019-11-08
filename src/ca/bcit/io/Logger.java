package ca.bcit.io;

import ca.bcit.jfx.components.Console;

import java.util.Arrays;

public class Logger {

	public enum LoggerLevel {
		DEBUG,
		INFO
	}

	public static void setLoggerLevel(LoggerLevel loggerLevel) {
		Logger.loggerLevel = loggerLevel;
	}

	private static LoggerLevel loggerLevel = LoggerLevel.DEBUG;

	public static void debug(String message) {
		if (loggerLevel == LoggerLevel.DEBUG)
			Console.cout.appendText(message + '\n');
	}

	public static void debug(Exception e) {
		Console.cout.appendText(e.getLocalizedMessage() + '\n');
		Console.cout.appendText(Arrays.toString(e.getStackTrace()) + '\n');
	}

	public static void info(String message) {
		Console.cout.appendText(message + '\n');
	}
}