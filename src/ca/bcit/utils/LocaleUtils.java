package ca.bcit.utils;

import ca.bcit.Settings;
import ca.bcit.i18n.LocaleEnum;

import java.util.Locale;

public class LocaleUtils {
    public static Locale getLocaleFromLocaleEnum(LocaleEnum localeEnum) {
        switch (localeEnum) {
            case PL_PL:
                return new Locale("pl", "PL");
            case PT_BR:
                return new Locale("pt", "BR");
            case EN_CA:
                return new Locale("en", "CA");
        }

        throw new IllegalArgumentException("Invalid locale");
    }

    public static String translate(String key) {
        return Settings.getCurrentResources().getString(key);
    }
}
