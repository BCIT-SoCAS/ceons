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

    /**
     * Constructor of SavedNodeDetails
     * @param nodeNum node number
     * @param location location name
     * @param connectedNodeNum node numbers of the connected node, ex: "1,2,3"
     * @param numRegenerators number of regenerators
     * @param nodeType node type (international, data center, standard)
     */
    public SavedNodeDetails(int nodeNum, String location, String connectedNodeNum, int numRegenerators, String nodeType) {
        setNodeNum(nodeNum);
        setLocation(location);
        setConnectedNodeNum(connectedNodeNum);
        setNumRegenerators(numRegenerators);
        setNodeType(nodeType);

        initConnectedNodeLinkMap(connectedNodeNum);
    }

    /**
     * Getter for nodeNum
     * @return int node number
     */
    public int getNodeNum() {
        return nodeNum;
    }

    /**
     * Setter for nodeNum
     * @param nodeNum node number
     */
    public void setNodeNum(int nodeNum) {
        if(nodeNum >= 0){
            this.nodeNum = nodeNum;
        } else {
            throw new IllegalArgumentException("Node num can't be negative");
        }
    }

    /**
     * Getter for location
     * @return String location name
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter for location
     * @param location location name
     */
    public void setLocation(String location) {
        if(location != null && !location.isEmpty()){
            this.location = location;
        } else {
            throw new IllegalArgumentException("Node location can't be null or empty");
        }
    }

    /**
     * Getter for connectedNodeNum
     * @return String node numbers of the connected node, ex: "1,2,3"
     */
    public String getConnectedNodeNum() {
        return connectedNodeNum;
    }

    /**
     * Setter for connectedNodeNum
     * @param connectedNodeNum node numbers of the connected node, ex: "1,2,3"
     */
    public void setConnectedNodeNum(String connectedNodeNum) {
        if(connectedNodeNum != null){
            this.connectedNodeNum = connectedNodeNum;
        } else {
            throw new IllegalArgumentException("Node connection from map creation can't be null");
        }
    }

    /**
     * Parse connectedNodeNum and make a map of all network links for this current node
     * @param connectedNodeNum node numbers of the connected node, ex: "1,2,3"
     */
    public void initConnectedNodeLinkMap(String connectedNodeNum) {
        List<String> connectedNodeNumStringList = Arrays.asList(connectedNodeNum.split(","));

        for(String otherConnectionNum : connectedNodeNumStringList){
            ArrayList<String> currentNodeConnection = new ArrayList<String>();
            HashMap<String, Object> currentNodeConnectionLinkLength = new HashMap<String, Object>();

            currentNodeConnection.add(this.nodeNumToString());
            currentNodeConnection.add("Node_" + otherConnectionNum);
            Collections.sort(currentNodeConnection);

            currentNodeConnectionLinkLength.put("length", 100);
            currentNodeConnectionLinkLength.put("class", NetworkLink.class.getName());
            this.connectedNodeLinkMap.put(currentNodeConnection, currentNodeConnectionLinkLength);
        }
    }

    /**
     * Getter for numRegenerators
     * @return int number of regenerators
     */
    public int getNumRegenerators() {
        return numRegenerators;
    }

    /**
     * Setter for numRegenrators
     * @param numRegenerators number of regenerators
     */
    public void setNumRegenerators(int numRegenerators) {
        if(numRegenerators >= 0){
            this.numRegenerators = numRegenerators;
        } else {
            throw new IllegalArgumentException("Number of regenerators can't be negative");
        }
    }

    /**
     * Getter for nodeType
     * @return String node type (international, data center, standard)
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * Setter for nodeType
     * @param nodeType node type (international, data center, standard)
     */
    public void setNodeType(String nodeType) {
        if(nodeType != null && !nodeType.isEmpty()){
            this.nodeType = nodeType;
        } else {
            throw new IllegalArgumentException("Node type can't be null or empty");
        }
    }

    /**
     * Getter for connectedNodeLinkMap
     * @return HashMap formatted to store in yml
     */
    public HashMap<ArrayList<String>, HashMap<String, Object>> getConnectedNodeLinkMap(){
        return connectedNodeLinkMap;
    }

    /**
     * Getter for latLng
     * @return LatLng latitude and longitude
     */
    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * Getter for x position
     * @return int x position
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for y position
     * @return int y position
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for latLng
     * @param latLng latitude and longitude
     */
    public void setLatLng(LatLng latLng) {
        if(latLng != null){
            this.latLng = latLng;
        } else {
            throw new IllegalArgumentException("Latitude and Longitude can't be null");
        }
    }

    /**
     * Setter for x position
     * @param x x position
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Setter for y position
     * @param y y position
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Convert nodeNum to String
     * @return String nodeNum in String, ex: "Node_0"
     */
    public String nodeNumToString(){
        return "Node_" + this.nodeNum;
    }

    /**
     * Convert current object to String for printing
     * @return String
     */
    @Override
    public String toString() {
        return "Node_" + this.nodeNum + ": " + this.location + " (" + this.x + ", " + this.y + ")";
    }

    /**
     * Serialize from yml
     * @return Map
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", nodeNumToString());
        map.put("regenerators", numRegenerators);
        map.put("xcoordinate", x);
        map.put("ycoordinate", y);
        return map;
    }
}
