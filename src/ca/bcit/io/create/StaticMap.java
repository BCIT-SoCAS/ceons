package ca.bcit.io.create;

import ca.bcit.Settings;
import com.google.maps.*;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.Size;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.*;

public class StaticMap {
    private LatLng centerPoint;
    private GeoApiContext context;
    private int zoomLevel;
    private double meterPerPixel;
    private String key;

    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
    private ArrayList<LatLng> coordinates;
    private boolean isFirst = true;
    final private Size mapSize = new Size(475,302);

    /**
     * Constructor for StaticMap
     * @param apiKey google static map api
     */
    public StaticMap(String apiKey) {
        this.key = apiKey;
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
        this.coordinates = new ArrayList<LatLng>();
    }

    /**
     * create and generate a static map from google
     * @param hasMarker decide whether the generated static map should have markers on each location
     */
    public ImageResult generateMap(Boolean hasMarker) {
        try {
            setCenterPoint();
            setZoomLevel();
            StaticMapsRequest.Markers markers = new StaticMapsRequest.Markers();
            if (hasMarker) {
                markers.size(StaticMapsRequest.Markers.MarkersSize.tiny);
                for(LatLng latLng : this.coordinates)
                    markers.addLocation(latLng);

                if (Settings.GENERATE_MAPS_WITH_CENTRAL_POINT_MARKER)
                    markers.addLocation(this.centerPoint);
            }
            ImageResult map = StaticMapsApi.newRequest(context, mapSize).center(this.centerPoint).markers(markers).zoom(this.zoomLevel).scale(4).await();
            ImageIO.read(new ByteArrayInputStream(map.imageData));
            return map;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public SavedNodeDetails addLocation(SavedNodeDetails savedNodeDetails) {
        LatLng latlng = getLatLng(savedNodeDetails.getLocation(), this.key);
        savedNodeDetails.setLatLng(latlng);
        coordinates.add(latlng);
        if (this.isFirst) {
            this.minLat = latlng.lat;
            this.maxLat = latlng.lat;
            this.minLng = latlng.lng;
            this.maxLng = latlng.lng;
            this.isFirst = false;
        }
        else {
            if (latlng.lat < minLat)
                minLat = latlng.lat;
            else if (latlng.lat > maxLat)
                maxLat = latlng.lat;

            if (latlng.lng < minLng)
                minLng = latlng.lng;
            else if (latlng.lng > maxLng)
                maxLng = latlng.lng;
        }

        return savedNodeDetails;
    }

    /**
     * Getter for meterPerPixel
     * @return double meter per pixel
     */
    public double getMeterPerPixel() {
        return meterPerPixel;
    }

    /**
     * Getter for centerPoint
     * @return LatLng center point coordinate
     */
    public LatLng getCenterPoint() {
        return centerPoint;
    }

    /**
     * Getter for mapSize
     * @return Size google static map size
     */
    public Size getMapSize() {
        return mapSize;
    }

    /**
     * Calculate and set the center point of google static map
     */
    private void setCenterPoint() {
        LatLng center = new LatLng(0,0);
        center.lat = (this.minLat + this.maxLat)/2;
        center.lng = (this.minLng + this.maxLng)/2;
        this.centerPoint = center;
    }

    /**
     * Calculate and set the zoom level of google static map
     */
    private void setZoomLevel() {
        long minWidth = distance(maxLat, maxLat, maxLng, minLng);
        long minHeight = distance(maxLat, minLat, maxLng, maxLng);
        long minHorizontalDistancePerPixel = minWidth/this.mapSize.width;
        long minVerticalDistancePerPixel = minHeight/this.mapSize.height;
        long minDistancePerPixel = 0;

        minDistancePerPixel = Math.max(minHorizontalDistancePerPixel, minVerticalDistancePerPixel);

        for (int i = 20; i > 0; i--)
            if (minDistancePerPixel < calMetersPerPx(this.centerPoint.lat, i)) {
                this.zoomLevel = i;
                break;
            }
    }

    /**
     * Returns the distance each pixel on the map is in meters
     * @param centerLat latitude value of the center point
     * @param zoomLevel zoom level
     * @return meters per pixel
     */
    private double calMetersPerPx(double centerLat, int zoomLevel) {
        this.meterPerPixel = 156543.03392 * Math.cos(centerLat * Math.PI / 180) / Math.pow(2, zoomLevel);
        return this.meterPerPixel;
    }

    /**
     * Returns the latitude and longitude of a location
     * @param location location name
     * @param apiKey google map api key
     * @return LatLng latitude and longtude
     */
    public static LatLng getLatLng(String location, String apiKey) {
        LatLng latLng = new LatLng(0,0);
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();
            GeocodingApiRequest request = GeocodingApi.geocode(context, location);
            GeocodingResult[] result = request.await();
            // Handle successful request.
            latLng.lng = result[0].geometry.location.lng;
            latLng.lat = result[0].geometry.location.lat;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return latLng;
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
    public static long distance(double lat1, double lat2, double lon1, double lon2) {
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

    public int getZoomLevel() {
        return zoomLevel;
    }
}
