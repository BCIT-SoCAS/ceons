package ca.bcit.io;

import ca.bcit.jfx.components.Console;

public class Logger {

	public enum LoggerLevel {
		DEBUG
	}
	
	private static final LoggerLevel loggerLevel = LoggerLevel.DEBUG;
	
	public static void debug(String message) {
		if (loggerLevel == LoggerLevel.DEBUG) Console.cout.println(message);
	}
	
	public static void debug(Exception e) {
		e.printStackTrace(Console.cout);
	}
	
	public static void info(String message) {
		Console.cout.println(message);
	}
}