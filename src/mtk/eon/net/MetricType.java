package mtk.eon.net;

public enum MetricType {

	STATIC, DYNAMIC;
	
	public static MetricType valueOf2(String string) {
		return valueOf(string.toUpperCase());
	}
}
