package ca.bcit.io.create;

import ca.bcit.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SavedNodeTypes class to hold all user-defined node types prior to saving into YAML file
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
        String nodeType = nodeDetails.getNodeType();

        if (nodeType.equals(LocaleUtils.translate("international")) || nodeType.equals(LocaleUtils.translate("data_center") + ", " + LocaleUtils.translate("international")))
            nodeNumInternationals.add(nodeDetails.nodeNumToString());

        if (nodeType.equals(LocaleUtils.translate("data_center")) || nodeType.equals(LocaleUtils.translate("data_center") + ", " + LocaleUtils.translate("international")))
            nodeNumReplicas.add(nodeDetails.nodeNumToString());
    }

    /**
     * Getter of HashMap to be serialized
     * @return HashMap of node types to node numbers
     */
    public HashMap<String, ArrayList<String>> getToSerializeNodeTypes(){
        return toSeralizeNodeTypes;
    }
}
