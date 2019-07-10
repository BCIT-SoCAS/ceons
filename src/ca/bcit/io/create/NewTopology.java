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

    public static int calDistance(String locationA, String locationB, String apiKey) {
       LatLng a = StaticMap.getLatLng(locationA, apiKey);
       LatLng b = StaticMap.getLatLng(locationB, apiKey);

       long disInMeters = distance(a.lat, b.lat, a.lng, b.lng);
       return (int)disInMeters/1000;
    }

    private int calYCoord(SavedNodeDetails savedNodeDetails) {
        int y = 0;
        int centerHeight = staticMap.getMapSize().height;
        LatLng centerPoint = staticMap.getCenterPoint();
        double meterPerPixel = staticMap.getMeterPerPixel()/2;
        double lat = savedNodeDetails.getLatLng().lat;
        long latDistance = distance(centerPoint.lat, lat, centerPoint.lng, centerPoint.lng);

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
        long lngDistance = distance(centerPoint.lat, centerPoint.lat, centerPoint.lng, lng);

        if ((centerPoint.lng - lng) > 0) {
            x = (int) Math.round(centerWidth - lngDistance/meterPerPixel);
        } else {
            x = (int) Math.round(lngDistance/meterPerPixel + centerWidth);
        }

        return x;
    }

    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    private static long distance(double lat1, double lat2, double lon1,
                                 double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

        double height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.round(Math.sqrt(distance)) * 1000;
    }

}
