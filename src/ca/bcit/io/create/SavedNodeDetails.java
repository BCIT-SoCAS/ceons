package ca.bcit.io.create;

import com.google.maps.model.LatLng;
import ca.bcit.io.YamlSerializable;

import java.util.HashMap;
import java.util.Map;

public class SavedNodeDetails implements YamlSerializable {

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
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getConnectedNodeNum() {
        return connectedNodeNum;
    }

    public void setConnectedNodeNum(String connectedNodeNum) {
        this.connectedNodeNum = connectedNodeNum;
    }

    public int getNumRegenerators() {
        return numRegenerators;
    }

    public void setNumRegenerators(int numRegenerators) {
        this.numRegenerators = numRegenerators;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    //Shaun's

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
        this.latLng = latLng;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    private String nodeNumToString(){
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
        map.put("regenerators", numRegenerators);
        map.put("xcoordinate", x);
        map.put("ycoordinate", y);
        return map;
    }
}
