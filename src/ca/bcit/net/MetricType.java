package ca.bcit.net;
/**
 * Metrics refer to IEEE ICC paper
 *
 */
public enum MetricType {

	STATIC, DYNAMIC;
	
	public static MetricType valueOf2(String string) {
		return valueOf(string.toUpperCase());
	}
}
