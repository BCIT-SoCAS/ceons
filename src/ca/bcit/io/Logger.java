package ca.bcit.io;

import ca.bcit.jfx.components.Console;

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
		if (loggerLevel == LoggerLevel.DEBUG) System.out.println((message));
	}
	
	public static void debug(Exception e) {
		e.printStackTrace();
	}
	
	public static void info(String message) {
		System.out.println(message);
	}
}