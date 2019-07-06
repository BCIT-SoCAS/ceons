package ca.bcit.io.create;

import com.google.maps.*;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.Size;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

public class StaticMap {
    private Map<String, String> locations;
    private LatLng centerPoint;
    private GeoApiContext context;
    private int zoomLevel;
    private double meterPerPixel;

    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
    private ArrayList<LatLng> coordinates;
    boolean isFirst = true;

    final private Size mapSize = new Size(500,365);
    final private int MERCATOR_RANGE = 256;

    /**
     * Constructor for StaticMap
     * @param apiKey
     */
    public StaticMap(String apiKey) {
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
        this.locations = new HashMap<String, String>();
        this.coordinates = new ArrayList<LatLng>();
    }

    /**
     * create and generate a static map from google
     * @param hasMarker
     */
    public ImageResult generateMap(Boolean hasMarker) {
        try {
            setCenterPoint();
            setZoomLevel();
            StaticMapsRequest.Markers markers = new StaticMapsRequest.Markers();
            if (hasMarker) {
                markers.size(StaticMapsRequest.Markers.MarkersSize.tiny);
//                for(Map.Entry l : locations.entrySet()) {
//                    markers.addLocation(l.getValue().toString());
//                }
                for(LatLng latLng : this.coordinates) {
                    markers.addLocation(latLng);
                }
                // show center point
                markers.addLocation(this.centerPoint);
            }
            ImageResult map = StaticMapsApi.newRequest(context, mapSize).center(this.centerPoint).markers(markers).
                    zoom(this.zoomLevel).scale(2).await();
//            ImageResult map = StaticMapsApi.newRequest(context, mapSize).center(this.centerPoint).markers(markers).
//                    scale(2).await();
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(map.imageData));
            System.out.println("image generated");
            return map;
//            File outPutFile = new File("image.png");
//            ImageIO.write(img, "png", outPutFile);
        } catch (Exception e) {
            System.out.println("Failed to generate static map:" + e);
            return null;
        }
    }

    /**
     * add a location to the map
     * @param location
     * @param nodeNum
     */
    public void addLocation(String location, String nodeNum) {
        locations.put(nodeNum, location);
        LatLng latlng = getLatLng(location);

        coordinates.add(latlng);

        if (this.isFirst) {
            this.minLat = latlng.lat;
            this.maxLat = latlng.lat;
            this.minLng = latlng.lng;
            this.maxLng = latlng.lng;
            this.isFirst = false;
        } else {
            if (latlng.lat < minLat) {
                minLat = latlng.lat;
            } else if (latlng.lat > maxLat) {
                maxLat = latlng.lat;
            }
            if (latlng.lng < minLng) {
                minLng = latlng.lng;
            } else if (latlng.lng > maxLng) {
                maxLng = latlng.lng;
            }
        }

        System.out.println(location + " added, coordinate: " + latlng.lat + ", " + latlng.lng);
    }

    public SavedNodeDetails addLocation(SavedNodeDetails savedNodeDetails) {
        locations.put("Node_" + savedNodeDetails.getNodeNum(), savedNodeDetails.getLocation());
        LatLng latlng = getLatLng(savedNodeDetails.getLocation());

        savedNodeDetails.setLatLng(latlng);

        coordinates.add(latlng);

        if (this.isFirst) {
            this.minLat = latlng.lat;
            this.maxLat = latlng.lat;
            this.minLng = latlng.lng;
            this.maxLng = latlng.lng;
            this.isFirst = false;
        } else {
            if (latlng.lat < minLat) {
                minLat = latlng.lat;
            } else if (latlng.lat > maxLat) {
                maxLat = latlng.lat;
            }
            if (latlng.lng < minLng) {
                minLng = latlng.lng;
            } else if (latlng.lng > maxLng) {
                maxLng = latlng.lng;
            }
        }

        System.out.println(savedNodeDetails.getLocation() + " added, coordinate: " + latlng.lat + ", " + latlng.lng);
        return savedNodeDetails;
    }

    /**
     * Getter for meterPerPixel
     * @return
     */
    public double getMeterPerPixel() {
        return meterPerPixel;
    }

    /**
     * Getter for centerPoint
     * @return
     */
    public LatLng getCenterPoint() {
        return centerPoint;
    }

    /**
     * Getter for mapSize
     * @return
     */
    public Size getMapSize() {
        return mapSize;
    }

    /**
     * set the center point of the map
     */
    private void setCenterPoint() {
        LatLng center = new LatLng(0,0);
        System.out.println("MAXLAT: " + this.maxLat);
        System.out.println("MINLAT: " + this.minLat);
        System.out.println("MAXLNG: " + this.maxLng);
        System.out.println("MINLNG: " + this.minLng);
        center.lat = (this.minLat + this.maxLat)/2;
        center.lng = (this.minLng + this.maxLng)/2;
        this.centerPoint = center;

        System.out.println("center point set: " + this.centerPoint.lat + ", " + this.centerPoint.lng);
    }

    /**
     * Set the zoom level of the map
     */
    private void setZoomLevel() {
//        LatLng topLeft = new LatLng(maxLat, minLng);
//        LatLng topRight = new LatLng(maxLat, maxLng);
//        LatLng bottomLeft = new LatLng(minLat, minLng);
//        LatLng bottomRight = new LatLng(minLat, maxLng);
        long minWidth = distance(maxLat, maxLat, maxLng, minLng);
        long minHeight = distance(maxLat, minLat, maxLng, maxLng);
        System.out.println("min width: " + minWidth);
        System.out.println("min height: " + minHeight);
        System.out.println(this.mapSize.width);
        System.out.println(this.mapSize.height);

        long minHorizontalDistancePerPixel = minWidth/this.mapSize.width;
        long minVerticalDistancePerPixel = minHeight/this.mapSize.height;
        System.out.println("minHorizontalDistancePerPixel: " + minHorizontalDistancePerPixel);
        System.out.println("minVerticalDistancePerPixel: " + minVerticalDistancePerPixel);

        long minDistancePerPixel = 0;

        if(minHorizontalDistancePerPixel > minVerticalDistancePerPixel) {
            minDistancePerPixel = minHorizontalDistancePerPixel;
        } else {
            minDistancePerPixel = minVerticalDistancePerPixel;
        }

        System.out.println("minDistancePerPixel: " + minDistancePerPixel);

//        for (int i = 0; i < this.zoomLevelDistance.length; i++) {
//            if (this.zoomLevelDistance[i] < minDistancePerPixel) {
//                this.zoomLevel = i;
//                break;
//            }
//        }

        for (int i = 20; i > 0; i--) {
            if (minDistancePerPixel < calMetersPerPx(this.centerPoint.lat, i)) {
                this.zoomLevel = i;
                break;
            }
        }
        System.out.println("Zoom level: " + this.zoomLevel);
    }

    /**
     * Returns the distance each pixel on the map is in meters
     * @param centerLat
     * @param zoomLevel
     * @return
     */
    private double calMetersPerPx(double centerLat, int zoomLevel) {
        this.meterPerPixel = 156543.03392 * Math.cos(centerLat * Math.PI / 180) / Math.pow(2, zoomLevel);
        return this.meterPerPixel;
    }

    /**
     * Returns the latitude and longitude of a location
     * @param location
     * @return
     */
    public LatLng getLatLng(String location) {
        LatLng latLng = new LatLng(0,0);
        try {
            GeocodingApiRequest request = GeocodingApi.geocode(this.context, location);
            GeocodingResult[] result = request.await();
            // Handle successful request.
            latLng.lng = result[0].geometry.location.lng;
            latLng.lat = result[0].geometry.location.lat;
        } catch (Exception e) {
            // Handle error
            System.out.println("Failed to get lat and lng for " + location + " :" + e);
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
        System.out.println(Math.round(Math.sqrt(distance)));

        return Math.round(Math.sqrt(distance)) * 1000;
    }

}
