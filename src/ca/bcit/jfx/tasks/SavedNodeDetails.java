package ca.bcit.jfx.tasks;

public class SavedNodeDetails {

    private String cityName;
    private int connectedNodeNum;
    private int numRegenerators;
    private String nodeType;

    public SavedNodeDetails(){
        this.cityName = "";
        this.connectedNodeNum = 0;
        this.numRegenerators = 100;
        this.nodeType = "Normal";
    }

    public SavedNodeDetails(String cityName, int connectedNodeNum, int numRegernators, String nodeType){
        this.cityName = cityName;
        this.connectedNodeNum = connectedNodeNum;
        this.numRegenerators = numRegernators;
        this.nodeType = nodeType;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getConnectedNodeNum() {
        return connectedNodeNum;
    }

    public void setConnectedNodeNum(int connectedNodeNum) {
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
