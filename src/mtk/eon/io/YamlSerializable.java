package mtk.eon.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface YamlSerializable {
	
	static List<Class<? extends YamlSerializable>> serializableClasses = new ArrayList<>();
	
	public static void registerSerializableClass(Class<? extends YamlSerializable> clazz)
			throws NoSuchMethodException, SecurityException {
		if (!serializableClasses.contains(clazz)) {
			clazz.getDeclaredConstructor(Map.class);
			serializableClasses.add(clazz);
		}
	}
	
	public Map<String, Object> serialize();
}