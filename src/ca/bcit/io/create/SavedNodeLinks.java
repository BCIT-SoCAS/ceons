package ca.bcit.io.create;

import ca.bcit.net.NetworkLink;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SavedNodeLinks class to hold all user-defined links prior to saving into YAML file
 * @author Shaun Tseng, Derek Wong
 * @version 1.0.0
 */
public class SavedNodeLinks {
    private HashMap<ArrayList<String>, HashMap<String, Object>> toSerializeNodeLinks;

    /**
     * No-args constructor that initializes the HashMap to map complex keys (individual link) to value (length of link)
     */
    public SavedNodeLinks(){
        toSerializeNodeLinks = new HashMap<ArrayList<String>, HashMap<String, Object>>();
    }

    /**
     * Will add unique links to the HashMap, making calls to Google Maps API to calculate the distance between them
     * @param nodeDetails provides all user defined links to one particular node
     * @param tableList contains all user defined node details, used to query location in distance calculation
     * @param apiKey user-provided google maps API key
     */
    public void setNodeNumLinks(SavedNodeDetails nodeDetails, ObservableList<SavedNodeDetails> tableList, String apiKey){
        for(Map.Entry<ArrayList<String>, HashMap<String, Object>> entry : nodeDetails.getConnectedNodeLinkMap().entrySet()) {
            if(!toSerializeNodeLinks.containsKey(entry.getKey())){
                //Will see if there is a connected node specified by user
                boolean hasLink = !(entry.getKey().get(0).split("_").length == 1);
                if(hasLink) {
                    int nodeANum = Integer.parseInt(entry.getKey().get(0).split("_")[1]);
                    String nodeA = tableList.get(nodeANum).getLocation();
                    int nodeBNum = Integer.parseInt(entry.getKey().get(1).split("_")[1]);
                    String nodeB = tableList.get(nodeBNum).getLocation();
                    int length = NewTopology.calDistance(nodeA, nodeB, apiKey);
                    // ------------------------------------------------------------------------------------------------------------------------------------------
                    System.out.println("Link: nodeANum=" + nodeANum + ", nodeA=" + nodeA + ", nodeBNum=" + nodeBNum + ", nodeB=" + nodeB + ", length=" + length);
                    // ------------------------------------------------------------------------------------------------------------------------------------------
                    HashMap<String, Object> link = new HashMap<String, Object>();
                    link.put("length", length);
                    link.put("class", NetworkLink.class.getName());
                    toSerializeNodeLinks.put(entry.getKey(), link);
                }
            }
        }
    }

    /**
     * Getter for the HashMap to be serialized
     * @return HashMap of complex key (ArrayList of individual node links) to the value (HashMap of distance between links)
     */
    public HashMap<ArrayList<String>, HashMap<String, Object>> getToSerializeNodeLinks(){
        return toSerializeNodeLinks;
    }
}
