package ca.bcit;

import ca.bcit.i18n.LocaleEnum;
import ca.bcit.net.algo.IRMSAAlgorithm;
import ca.bcit.net.modulation.IModulation;
import ca.bcit.utils.LocaleUtils;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.image.Image;

public class Settings {
    public final static int DEFAULT_YEAR = 2018;
    public final static int DEFAULT_ERLANG = 500;
    public final static double CAGR = 0.26;
    public final static int SPLASH_SCREEN_TIMER = 3000;
    public static LocaleEnum CURRENT_LOCALE = LocaleEnum.EN_CA;
    public static HashMap<String, IRMSAAlgorithm> registeredAlgorithms = new HashMap<>();
    public static HashMap<String, IModulation> registeredModulations = new HashMap<>();
    private static ResourceBundle resourceBundle;
    static Image bcitLogo = new Image(Settings.class.getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png"));
    static URL mainWindowResourceUrl = Settings.class.getResource("/ca/bcit/jfx/res/views/MainWindow.fxml");
    static URL splashScreenResourceUrl = Settings.class.getResource("/ca/bcit/jfx/res/views/SplashScreen.fxml");
    public static float topLeftCornerXCoordinate = 0;
    public static float topLeftCornerYCoordinate = 0;
    public static float zoomLevel = 1.0f;
    public static final double ZOOM_MIN_LEVEL = 1;
    public static final double ZOOM_MAX_LEVEL = 5;
    public static final boolean GENERATE_MAPS_WITH_MARKERS = false;
    public static final boolean GENERATE_MAPS_WITH_CENTRAL_POINT_MARKER = true;
    public static final String[] MAIL_PASSWORD = new String[]{"%14d$29#6e_hmMKqJOrFlJhryjiggw3CObTwz6B0OOd7u6owl8Jowl8Jy3Z0L", "%6$21#78d_D5Sy2jq1bauH73AvvIJBltZmewh9E9eeNXmN0c1kN0c1kgrnuv", "%1a0$33#f5_aKx3wInTNU6XCo8cTK9eH3igzEuCYzHzoSXjHPX2jHPX2zZ4hF", "%b7$38#5a2_bKFaP0AXlyLhyi6lMLxKcVVnhPj2HMsTyy4ThnAkThnAkQjtWu"};
    public static final String MAIL_USERNAME = "ceons";
    public static final int DEFAULT_NUMBER_OF_REGENERATORS = 200;

    static void registerAlgorithm(String algorithmClassFullName) throws Exception {
        try {
            Class algoClass = Class.forName(algorithmClassFullName);
            Method getKeyMethod = algoClass.getMethod("getKey");
            Object algo = algoClass.newInstance();

            if (!registeredAlgorithms.containsKey(getKeyMethod.invoke(algo)))
                registeredAlgorithms.put((String) getKeyMethod.invoke(algo), (IRMSAAlgorithm) algo);
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new Exception(LocaleUtils.translate("the_algorithm_could_not_be_registered") + ": " + algorithmClassFullName);
        }
    }

    static void registerModulation(String modulationClassFullName) throws Exception {
        try {
            Class modulationClass = Class.forName(modulationClassFullName);
            Method getKeyMethod = modulationClass.getMethod("getKey");
            Object modulation = modulationClass.newInstance();

            if (!registeredModulations.containsKey(getKeyMethod.invoke(modulation)))
                registeredModulations.put((String) getKeyMethod.invoke(modulation), (IModulation) modulation);
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new Exception(LocaleUtils.translate("the_modulation_could_not_be_registered") + ": " + modulationClassFullName);
        }
    }

    public static ResourceBundle getCurrentResources() {
        if (resourceBundle == null || !resourceBundle.getLocale().equals(LocaleUtils.getLocaleFromLocaleEnum(Settings.CURRENT_LOCALE)))
            resourceBundle = ResourceBundle.getBundle("ca.bcit.bundles.lang", LocaleUtils.getLocaleFromLocaleEnum(Settings.CURRENT_LOCALE));

        return resourceBundle;
    }
}
