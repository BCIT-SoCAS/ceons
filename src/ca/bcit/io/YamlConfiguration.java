package ca.bcit.io;

import ca.bcit.io.create.SavedNodeDetails;
import ca.bcit.net.NetworkNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class YamlConfiguration {

	private Yaml parser;
	private DumperOptions dumperOptions;
	private Object config;

	public YamlConfiguration(InputStream stream) {
//		this.dumperOptions = new DumperOptions();
//		dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//		this.parser = new Yaml(dumperOptions);
		this.parser = new Yaml();
		config = deserializeAll(parser.load(stream));
//		System.out.println("Config: " + config);
	}
	@SuppressWarnings("rawtypes")
	public YamlConfiguration() {
		this.dumperOptions = new DumperOptions();
		dumperOptions.setWidth(100);
		this.parser = new Yaml(dumperOptions);
//		this.parser = new Yaml();
		config = new HashMap();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object deserializeAll(Object object) {
		if (object instanceof Map) {
			Map map = (Map) object;
			for (Entry entry : (Set<Entry>) map.entrySet()){
				entry.setValue(deserializeAll(entry.getValue()));
			}
			if (map.containsKey("class"))
				try {
					Constructor init = Class.forName((String) map.get("class")).getDeclaredConstructor(Map.class);
					init.setAccessible(true);
					map.remove("class");
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
			for (Entry entry : (Set<Entry>) map.entrySet()){
				entry.setValue(serializeAll(entry.getValue()));
			}
			return map;
		} else if (object instanceof List) {
			List list = (List) object;
			for (int i = 0; i < list.size(); i++) list.set(i, serializeAll(list.get(i))); {
				return list;
			}
		} else if (object instanceof YamlSerializable) {
			Map map = ((YamlSerializable) object).serialize();
			if(object instanceof SavedNodeDetails){
				NetworkNode nn = new NetworkNode();
				map.put("class", nn.getClass().getName());
			}
			map = (Map) serializeAll(map);
			return map;
		} else {
			return object;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T get(String path) {
		if (path.length() == 0) return (T) config;
		String[] splittedPath = path.split("\\.");
		if (splittedPath.length == 0) splittedPath = new String[] {path};
		Object result = config;
		for (String aSplittedPath : splittedPath)
			if (result instanceof Map) result = ((Map) result).get(aSplittedPath);
			else if (result instanceof List)
				try {
					result = ((List) result).get(Integer.parseInt(aSplittedPath));
				} catch (NumberFormatException e) {
					if (aSplittedPath.equals("size")) return (T) new Integer(((List) result).size());
					throw e;
				}
		return (T) result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T put(String path, Object value) {
		Object container;

		if (path.length() == 0) {
			Object oldConfig = config;
			config = value;
			return (T) oldConfig;
		}

		if (path.contains(".")) {
			int lastDotIndex = path.lastIndexOf('.');
			String containerPath = path.substring(0, lastDotIndex);
			path = path.substring(lastDotIndex + 1);
			container = get(containerPath);
		} else {
			container = config;
		}

		if (container instanceof Map)
			return (T) ((Map) container).put(path, value);

		else if (container instanceof List) {
			int index = Integer.parseInt(path);
			if (index == ((List) container).size()) {
				((List) container).add(value);
				return null;
			} else return (T) ((List) container).set(index, value);
		} else throw new YAMLException("Tried to store a value in an non-existent container!");
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
			container = get(path.substring(0, lastDotIndex));
			path = path.substring(lastDotIndex + 1);
		} else container = config;

		if (container instanceof Map)
			return (T) ((Map) container).remove(path);
		else if (container instanceof List)
			return (T) ((List) container).remove(Integer.parseInt(path));
		else throw new YAMLException("Tried to remove a value from an unexisting container!");
	}

	public void save(Writer output) {
		parser.dump(serializeAll(config), output);
	}
}
