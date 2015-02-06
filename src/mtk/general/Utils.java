package mtk.general;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class Utils {

	public static boolean checkArrayIndex(int length, int index) {
		return index >= 0 && index < length;
	}
	
	public static void setStaticFinal(Class<?> clazz, String fieldName, Object value) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.setAccessible(true);
		field.set(null, value);
		field.setAccessible(false);
		modifiers.setInt(field, field.getModifiers() | Modifier.FINAL);
		modifiers.setAccessible(false);
	}
}
