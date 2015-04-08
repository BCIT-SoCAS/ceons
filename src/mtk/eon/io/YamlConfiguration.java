package mtk.eon.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class YamlConfiguration {

	public static final Yaml PARSER = new Yaml();
	Object config;
	
	public YamlConfiguration(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}
	
	public YamlConfiguration(InputStream stream) {
		config = deserializeAll(PARSER.load(stream));
	}
	
	@SuppressWarnings("rawtypes")
	public YamlConfiguration(Map map) {
		config = map;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object deserializeAll(Object object) {
		if (object instanceof Map) {
			Map map = (Map) object;
			for (Entry entry : (Set<Entry>) map.entrySet())
				entry.setValue(deserializeAll(entry.getValue()));
			if (map.containsKey("class"))
				try {
					Constructor init = Class.forName((String) map.get("class")).getDeclaredConstructor(Map.class);
					init.setAccessible(true);
					return init.newInstance(map);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException	| SecurityException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			return map;
		} else if (object instanceof List) {
			List list = (List) object;
			for (int i = 0; i < list.size(); i++) list.set(i, deserializeAll(list.get(i)));
			return list;
		} else return object;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object serializeAll(Object object) {
		if (object instanceof Map) {
			Map map = (Map) object;
			for (Entry entry : (Set<Entry>) map.entrySet())
				entry.setValue(serializeAll(entry.getValue()));
			return map;
		} else if (object instanceof List) {
			List list = (List) object;
			for (int i = 0; i < list.size(); i++) list.set(i, serializeAll(list.get(i)));
			return list;
		} else if (object instanceof YamlSerializable) {
			Map map = ((YamlSerializable) object).serialize();
			map.put("class", object.getClass().getName());
			return map;
		}
		else return object;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T get(Class<T> clazz, String path) {
		if (path.length() == 0) return (T) config;
		String[] splittedPath = path.split(".");
		if (splittedPath.length == 0) splittedPath = new String[] {path};
		Object result = config;
		for (int i = 0; i < splittedPath.length; i++)
			if (result instanceof Map) result = ((Map) result).get(splittedPath[i]);
			else if (result instanceof List)
				try {
					result = ((List) result).get(Integer.parseInt(splittedPath[i]));
				} catch (NumberFormatException e) {
					if (splittedPath[i].equals("size")) result = ((List) result).size();
					throw e;
				}
		return (T) result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T put(String path, Object value) {
		if (path.length() == 0) {
			Object oldConfig = config;
			config = value;
			return (T) oldConfig;
		}
		
		Object container;
		if (path.contains(".")) {
			int lastDotIndex = path.lastIndexOf('.');
			container = get(Object.class, path.substring(0, lastDotIndex));
			path = path.substring(lastDotIndex + 1);
		} else container = config;
		
		if (container instanceof Map)
			return (T) ((Map) container).put(path, value);
		else if (container instanceof List) {
			int index = Integer.parseInt(path);
			if (index == ((List) container).size()) {
				((List) container).add(value);
				return null;
			} else return (T) ((List) container).set(index, value);	
		} else throw new YAMLException("Tried to store a value in an unexisting container!");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T remove(String path) {
		if (path.length() == 0) {
			Object oldConfig = config;
			config = null;
			return (T) oldConfig;
		}
		
		Object container;
		if (path.contains(".")) {
			int lastDotIndex = path.lastIndexOf('.');
			container = get(Object.class, path.substring(0, lastDotIndex));
			path = path.substring(lastDotIndex + 1);
		} else container = config;
		
		if (container instanceof Map)
			return (T) ((Map) container).remove(path);
		else if (container instanceof List)
			return (T) ((List) container).remove(Integer.parseInt(path));
		else throw new YAMLException("Tried to remove a value from an unexisting container!");
	}
	
	public void save(File file) throws IOException {
		PARSER.dump(serializeAll(config), new FileWriter(file));
	}
}
