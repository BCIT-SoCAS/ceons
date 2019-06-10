package ca.bcit.jfx;

public class SavedNodeDetails {

    private String location;
    private String connectedNodeNum;
    private String nodeNum;
    private int numRegenerators;
    private String nodeType;

    public SavedNodeDetails(){
        this.location = "";
				this.connectedNodeNum = "";
				this.nodeNum = "";
        this.numRegenerators = 100;
        this.nodeType = "Standard";
    }

    public SavedNodeDetails(String nodeNum, String location, String connectedNodeNum, int numRegernators, String nodeType){
        this.nodeNum = nodeNum;
        this.location = location;
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
}
