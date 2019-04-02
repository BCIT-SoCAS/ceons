package ca.bcit.jfx;

public class SavedNodeDetails {

    private String nodeNum;
    private String locationName;
    private String connectedNodeNum;
    private int numRegenerators;
    private String nodeType;

    public SavedNodeDetails(){
        this.nodeNum = "";
        this.locationName = "";
        this.connectedNodeNum = "";
        this.numRegenerators = 100;
        this.nodeType = "Standard";
    }

    public SavedNodeDetails(String nodeNum, String locationName, String connectedNodeNum, int numRegernators, String nodeType){
        this.nodeNum = nodeNum;
        this.locationName = locationName;
        this.connectedNodeNum = connectedNodeNum;
        this.numRegenerators = numRegernators;
        this.nodeType = nodeType;
    }

    public String getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(String nodeNum) {
        this.nodeNum = nodeNum;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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
}
