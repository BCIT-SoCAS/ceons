package ca.bcit.io.create;

import com.google.maps.ImageResult;
import com.google.maps.model.LatLng;
import java.util.ArrayList;


public class NewTopology {
    private StaticMap staticMap;
    private ArrayList<SavedNodeDetails> savedNodeDetailsList;
    private final Boolean hasMarker = false;

    public NewTopology(String key) {
        this.staticMap = new StaticMap(key);
        this.savedNodeDetailsList = new ArrayList<SavedNodeDetails>();
    }

    public void addNode(SavedNodeDetails savedNodeDetails){
        staticMap.addLocation(savedNodeDetails);
        savedNodeDetailsList.add(savedNodeDetails);
    }

    public ImageResult getMap() {
        ImageResult staticMap = this.staticMap.generateMap(this.hasMarker);
        for (int i = 0; i < savedNodeDetailsList.size(); i++) {
            SavedNodeDetails savedNodeDetails = savedNodeDetailsList.get(i);
            savedNodeDetails.setX(calXCoord(savedNodeDetails));
            savedNodeDetails.setY(calYCoord(savedNodeDetails));
            System.out.println(savedNodeDetails.toString());
        }

        return staticMap;
    }

    private int calYCoord(SavedNodeDetails savedNodeDetails) {
        int y = 0;
        int centerHeight = staticMap.getMapSize().height;
        LatLng centerPoint = staticMap.getCenterPoint();
        double meterPerPixel = staticMap.getMeterPerPixel()/2;
        double lat = savedNodeDetails.getLatLng().lat;
        long latDistance = StaticMap.distance(centerPoint.lat, lat, centerPoint.lng, centerPoint.lng);

        if ((centerPoint.lat - lat) > 0) {
            y = (int) Math.round(latDistance/meterPerPixel + centerHeight);
        } else {
            y = (int) Math.round(centerHeight - latDistance/meterPerPixel);
        }

        return y;
    }

    private int calXCoord(SavedNodeDetails savedNodeDetails) {
        int x= 0;
        int centerWidth = staticMap.getMapSize().width;
        LatLng centerPoint = staticMap.getCenterPoint();
        double meterPerPixel = staticMap.getMeterPerPixel()/2;
        double lng = savedNodeDetails.getLatLng().lng;
        long lngDistance = StaticMap.distance(centerPoint.lat, centerPoint.lat, centerPoint.lng, lng);

        if ((centerPoint.lng - lng) > 0) {
            x = (int) Math.round(centerWidth - lngDistance/meterPerPixel);
        } else {
            x = (int) Math.round(lngDistance/meterPerPixel + centerWidth);
        }

        return x;
    }
}
