package ca.bcit.io.create;

import com.google.maps.model.LatLng;

public class NewNode {
    private String name;
    private int nodeNum;
    private LatLng latLng;
    private int x;
    private int y;

    public NewNode(String name, int nodeNum) {
        this.name = name;
        this.nodeNum = nodeNum;
    }

    public NewNode(String name, String nodeNum) {
        this.name = name;
        this.nodeNum = getNumFromNodeNum(nodeNum);
    }

    public String getName() {
        return name;
    }

    public int getNodeNum() {
        return nodeNum;
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
        this.latLng = latLng;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    private int getNumFromNodeNum(String nodeNum) {
        String[] parts = nodeNum.split("_");
        String numPart = parts[1];
        return Integer.parseInt(numPart);
    }

    @Override
    public String toString() {
        return "Node_" + this.nodeNum + ": " + this.name + " (" + this.x + ", " + this.y + ")";
    }
}
