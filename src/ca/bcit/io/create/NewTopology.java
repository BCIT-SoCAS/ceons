package ca.bcit.io.create;

import ca.bcit.Settings;
import com.google.maps.ImageResult;
import com.google.maps.model.LatLng;

import java.util.ArrayList;

public class NewTopology {
    private StaticMap staticMap;
    private ArrayList<SavedNodeDetails> savedNodeDetailsList;

    /**
     * Constructor for NewTopology
     * @param key google api key
     */
    public NewTopology(String key) {
        this.staticMap = new StaticMap(key);
        this.savedNodeDetailsList = new ArrayList<SavedNodeDetails>();
    }

    /**
     * Add new node
     * @param savedNodeDetails node details
     */
    public void addNode(SavedNodeDetails savedNodeDetails){
        staticMap.addLocation(savedNodeDetails);
        savedNodeDetailsList.add(savedNodeDetails);
    }

    /**
     * Generate a google static map
     * @return ImageResult google static map
     */
    public ImageResult getMap() {
        ImageResult staticMap = this.staticMap.generateMap(Settings.GENERATE_MAPS_WITH_MARKERS);
        for (int i = 0; i < savedNodeDetailsList.size(); i++) {
            SavedNodeDetails savedNodeDetails = savedNodeDetailsList.get(i);
            savedNodeDetails.setX(calXCoord(savedNodeDetails));
            savedNodeDetails.setY(calYCoord(savedNodeDetails));
        }
        return staticMap;
    }

    /**
     * Calculate and return y position of the node on google static map
     * @param savedNodeDetails node details
     * @return int y position
     */
    private int calYCoord(SavedNodeDetails savedNodeDetails) {
        int centerHeight = staticMap.getMapSize().height;
        LatLng centerPoint = staticMap.getCenterPoint();
        double meterPerPixel = staticMap.getMeterPerPixel()/2;
        double lat = savedNodeDetails.getLatLng().lat;
        long latDistance = StaticMap.distance(centerPoint.lat, lat, centerPoint.lng, centerPoint.lng);
        double correctionFactor = 0;
        switch (staticMap.getZoomLevel()) {
            case 3:
                correctionFactor = 0.11;
                break;
            case 5:
                correctionFactor = 0.02;
                break;
            default:
                correctionFactor = 0;
        }
        double correction = correctionFactor * latDistance/meterPerPixel ;

        return (int) Math.round(((centerPoint.lat - lat) > 0) ? centerHeight + latDistance/meterPerPixel - correction : centerHeight - latDistance/meterPerPixel - correction);
    }

    /**
     * Calculate and return x position of the node on google static map
     * @param savedNodeDetails node details
     * @return int x position
     */
    private int calXCoord(SavedNodeDetails savedNodeDetails) {
        int centerWidth = staticMap.getMapSize().width;
        LatLng centerPoint = staticMap.getCenterPoint();
        double meterPerPixel = staticMap.getMeterPerPixel()/2;
        double lng = savedNodeDetails.getLatLng().lng;
        long lngDistance = StaticMap.distance(centerPoint.lat, centerPoint.lat, centerPoint.lng, lng);

        return (int) Math.round(((centerPoint.lng - lng) > 0) ? centerWidth - lngDistance/meterPerPixel : centerWidth + lngDistance/meterPerPixel);
    }
}
