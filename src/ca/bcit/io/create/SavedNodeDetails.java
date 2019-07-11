package ca.bcit.io.create;

import ca.bcit.net.NetworkLink;
import com.google.maps.model.LatLng;
import ca.bcit.io.YamlSerializable;

import java.util.*;

public class SavedNodeDetails implements YamlSerializable {

    private HashMap<ArrayList<String>, HashMap<String, Object>> connectedNodeLinkMap = new HashMap<ArrayList<String>, HashMap<String, Object>>();
    private String location;
    private String connectedNodeNum;
    private int nodeNum;
    private int numRegenerators;
    private String nodeType;
    private LatLng latLng;
    private int x;
    private int y;

    public SavedNodeDetails() {
        this.location = "";
        this.connectedNodeNum = "";
        this.nodeNum = 0;
        this.numRegenerators = 100;
        this.nodeType = "Standard";
    }

    public SavedNodeDetails(int nodeNum, String location, String connectedNodeNum, int numRegenerators, String nodeType) {
        setNodeNum(nodeNum);
        setLocation(location);
        setConnectedNodeNum(connectedNodeNum);
        setNumRegenerators(numRegenerators);
        setNodeType(nodeType);

        initConnectedNodeLinkMap(connectedNodeNum);
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(int nodeNum) {
        if(nodeNum >= 0){
            this.nodeNum = nodeNum;
        } else {
            throw new IllegalArgumentException("Node num can't be negative");
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if(location != null && !location.isEmpty()){
            this.location = location;
        } else {
            throw new IllegalArgumentException("Node location can't be null or empty");
        }
    }

    public String getConnectedNodeNum() {
        return connectedNodeNum;
    }

    public void setConnectedNodeNum(String connectedNodeNum) {
        if(connectedNodeNum != null){
            this.connectedNodeNum = connectedNodeNum;
        } else {
            throw new IllegalArgumentException("Node connection from map creation can't be null");
        }
    }

    public void initConnectedNodeLinkMap(String connectedNodeNum) {
        //will split the user defined node connections then make a map of all network links to this current node
        List<String> connectedNodeNumStringList = Arrays.asList(connectedNodeNum.split(","));

        for(String otherConnectionNum : connectedNodeNumStringList){
            ArrayList<String> currentNodeConnection = new ArrayList<String>();
            HashMap<String, Object> currentNodeConnectionLinkLength = new HashMap<String, Object>();

            currentNodeConnection.add(this.nodeNumToString());
            currentNodeConnection.add("Node_" + otherConnectionNum);
            Collections.sort(currentNodeConnection);

            //length is placeholder for now
            currentNodeConnectionLinkLength.put("length", 100);
            currentNodeConnectionLinkLength.put("class", NetworkLink.class.getName());
            this.connectedNodeLinkMap.put(currentNodeConnection, currentNodeConnectionLinkLength);
        }
    }

    public int getNumRegenerators() {
        return numRegenerators;
    }

    public void setNumRegenerators(int numRegenerators) {
        if(numRegenerators >= 0){
            this.numRegenerators = numRegenerators;
        } else {
            throw new IllegalArgumentException("Number of regenerators can't be negative");
        }
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        if(nodeType != null && !nodeType.isEmpty()){
            this.nodeType = nodeType;
        } else {
            throw new IllegalArgumentException("Node type can't be null or empty");
        }
    }

    public HashMap<ArrayList<String>, HashMap<String, Object>> getConnectedNodeLinkMap(){
        return connectedNodeLinkMap;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setLatLng(LatLng latLng) {
        if(latLng != null){
            this.latLng = latLng;
        } else {
            throw new IllegalArgumentException("Latitude and Longitude can't be null");
        }
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String nodeNumToString(){
        return "Node_" + this.nodeNum;
    }

    @Override
    public String toString() {
        return "Node_" + this.nodeNum + ": " + this.location + " (" + this.x + ", " + this.y + ")";
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", nodeNumToString());
        map.put("location", getLocation());
        map.put("regenerators", numRegenerators);
        map.put("xcoordinate", x);
        map.put("ycoordinate", y);
        return map;
    }
}
