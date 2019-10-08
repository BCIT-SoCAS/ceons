package ca.bcit.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LocaleEnum {
    EN_CA("English (Canada)"),
    PL_PL("Polski"),
    PT_BR("Português (Brasil)");

    public final String label;

    private LocaleEnum(String localeString) {
        this.label = localeString;
    }

    public static List<String> labels() {
        return Arrays.stream(LocaleEnum.values()).map(e -> e.label).collect(Collectors.toList());
    }

    public static LocaleEnum getEnumByString(String code) throws Exception {
        for(LocaleEnum locale : LocaleEnum.values())
            if(code.equals(locale.label)) return locale;

        throw new Exception("Invalid locale: " + code);
    }
}
