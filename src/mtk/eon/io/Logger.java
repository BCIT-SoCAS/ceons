package mtk.eon.io;

public class Logger {

	public enum LoggerLevel {
		NORMAL, DEBUG;
	}
	
	public static LoggerLevel loggerLevel = LoggerLevel.DEBUG;
	
	public static void debug(String message) {
		if (loggerLevel == LoggerLevel.DEBUG) System.out.println(message);
	}
	
	public static void info(String message) {
		System.out.println(message);
	}
}