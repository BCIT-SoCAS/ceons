package ca.bcit.jfx;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.Size;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

public class StaticMap {

    private ArrayList<String> locations;
    private LatLng centerPoint;
    private ArrayList<LatLng> locationsLatLng;
    private GeoApiContext context;
    private String ApiKey;
    final private Size mapSize = new Size(800,500);


    public StaticMap(String apiKey) {
        this.ApiKey = apiKey;
        this.context = new GeoApiContext.Builder().apiKey(apiKey).build();
        this.locations = new ArrayList<String>();
        this.locationsLatLng = new ArrayList<LatLng>();
    }

    public void addLocation(String location) {
        if (locations.contains(location)) {
        }
        else {
            locations.add(location);
            GeocodingApiRequest request = GeocodingApi.geocode(this.context, location);
            try {
                GeocodingResult[] result = request.await();
                double lat = result[0].geometry.location.lat;
                double lng = result[0].geometry.location.lng;
                LatLng coord = new LatLng(lat,lng);
                locationsLatLng.add(coord);
                // Handle successful request.
            } catch (Exception e) {
                // Handle error
            }

//            request.setCallback(new PendingResult.Callback<GeocodingResult[]>() {
//                @Override
//                public void onResult(GeocodingResult[] result) {
//                    // Handle successful request.
//
//                    // Printing the result in json format
//                    //Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                    //System.out.println(gson.toJson(result[0]));
//
//                    double lat = result[0].geometry.location.lat;
//                    double lng = result[0].geometry.location.lng;
//                    LatLng coord = new LatLng(lat,lng);
//                    locationsLatLng.add(coord);
//                    System.out.println(coord.toString());
//                }
//
//                @Override
//                public void onFailure(Throwable e) {
//                    // Handle error.
//                }
//            });
        }
    }

    public void generateMap() {
        StaticMapsRequest.Markers markers = new StaticMapsRequest.Markers();
        markers.size(StaticMapsRequest.Markers.MarkersSize.small);
        for(int i = 0; i < locationsLatLng.size(); i++) {
            markers.addLocation(locationsLatLng.get(i));
        }

        // System.out.println(markers.toUrlValue());
        try {
            ImageResult map = StaticMapsApi.newRequest(context, mapSize).center(this.centerPoint).markers(markers).zoom(10).scale(2).await();
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(map.imageData));

            File outputfile = new File("image.png");
            ImageIO.write(img, "png", outputfile);
            System.out.println("image generated");
        } catch (Exception e) {

        }
    }

    public void removeLocation(String location) {
        if (!locations.contains(location)) {

        } else {
            locations.remove(location);
        }
    }

    public ArrayList<String> getLocations() {
        return this.locations;
    }

    public void setCenterPoint(LatLng centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setCenterPoint(String location) {
        GeocodingApiRequest request = GeocodingApi.geocode(this.context, location);
        try {
            GeocodingResult[] result = request.await();
            double lat = result[0].geometry.location.lat;
            double lng = result[0].geometry.location.lng;
            centerPoint = new LatLng(lat,lng);
            System.out.println("set centerPoint");
            // Handle successful request.
        } catch (Exception e) {
            // Handle error
        }
    }
}