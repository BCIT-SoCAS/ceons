package ca.bcit.io.create;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SavedNodeTypes class to hold all user-defined node types prior to saving into YAML file
 * @author Derek Wong
 * @version 1.0.0
 */
public class SavedNodeTypes {
    private ArrayList<String> nodeNumReplicas;
    private ArrayList<String> nodeNumInternationals;
    private HashMap<String, ArrayList<String>> toSeralizeNodeTypes;

    /**
     * No-args constructor that initializes an array for data centres and international nodes
     * Both are placed into a hash map
     */
    public SavedNodeTypes(){
        nodeNumReplicas = new ArrayList<String>();
        nodeNumInternationals = new ArrayList<String>();
        toSeralizeNodeTypes = new HashMap<String, ArrayList<String>>();
        toSeralizeNodeTypes.put("replicas", nodeNumReplicas);
        toSeralizeNodeTypes.put("international", nodeNumInternationals);
    }

    /**
     * Depending on what the user specifies, the string representation of the node number is placed into the appropriate arraylist
     * @param nodeDetails compares user selection of either/both international and/or data centre node types
     */
    public void setNodeNumType(SavedNodeDetails nodeDetails){
        if(nodeDetails.getNodeType().equals("International")){
            nodeNumInternationals.add(nodeDetails.nodeNumToString());
        } else if(nodeDetails.getNodeType().equals("Data Center, International")){
            nodeNumReplicas.add(nodeDetails.nodeNumToString());
            nodeNumInternationals.add(nodeDetails.nodeNumToString());
        } else if(nodeDetails.getNodeType().equals("Data Center")){
            nodeNumReplicas.add(nodeDetails.nodeNumToString());
        }
    }

    /**
     * Getter of HashMap to be serialized
     * @return HashMap of node types to node numbers
     */
    public HashMap<String, ArrayList<String>> getToSerializeNodeTypes(){
        return toSeralizeNodeTypes;
    }




}
