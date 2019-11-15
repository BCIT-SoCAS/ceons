package ca.bcit;

import ca.bcit.i18n.LocaleEnum;
import ca.bcit.net.algo.IRMSAAlgorithm;
import ca.bcit.utils.LocaleUtils;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.scene.image.Image;

public class Settings {
    final static int SPLASH_SCREEN_TIMER = 3000;
    public static LocaleEnum CURRENT_LOCALE = LocaleEnum.EN_CA;
    public static HashMap<String, IRMSAAlgorithm> registeredAlgorithms = new HashMap<>();
    private static ResourceBundle resourceBundle;
    static Image bcitLogo = new Image(Settings.class.getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png"));
    static URL mainWindowResourceUrl = Settings.class.getResource("/ca/bcit/jfx/res/views/MainWindow.fxml");
    static URL splashScreenResourceUrl = Settings.class.getResource("/ca/bcit/jfx/res/views/SplashScreen.fxml");
    public static float topLeftCornerXCoordinate = 0;
    public static float topLeftCornerYCoordinate = 0;
    public static float zoomLevel = 1.0f;
    public static final double ZOOM_MIN_LEVEL = 1;
    public static final double ZOOM_MAX_LEVEL = 3;
    public static final boolean GENERATE_MAPS_WITH_MARKERS = false;
    public static final boolean GENERATE_MAPS_WITH_CENTRAL_POINT_MARKER = true;


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

    public static ResourceBundle getCurrentResources() {
        if (resourceBundle == null || !resourceBundle.getLocale().equals(LocaleUtils.getLocaleFromLocaleEnum(Settings.CURRENT_LOCALE)))
            resourceBundle = ResourceBundle.getBundle("ca.bcit.bundles.lang", LocaleUtils.getLocaleFromLocaleEnum(Settings.CURRENT_LOCALE));

        return resourceBundle;
    }
}
