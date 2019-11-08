package ca.bcit.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface YamlSerializable {
	List<Class<? extends YamlSerializable>> serializableClasses = new ArrayList<>();
	
	static void registerSerializableClass(Class<? extends YamlSerializable> clazz) throws NoSuchMethodException, SecurityException {
		if (!serializableClasses.contains(clazz)) {
			clazz.getDeclaredConstructor(Map.class);
			serializableClasses.add(clazz);
		}
	}
	
	Map<String, Object> serialize();
}