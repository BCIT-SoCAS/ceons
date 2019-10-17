package ca.bcit.io.create;

import ca.bcit.Main;
import ca.bcit.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

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
        ResourceBundle resources = ResourceBundle.getBundle("ca.bcit.bundles.lang", LocaleUtils.getLocaleFromLocaleEnum(Main.CURRENT_LOCALE));

        String nodeType = nodeDetails.getNodeType();

        if (nodeType.equals(resources.getString("international")) || nodeType.equals(resources.getString("data_center") + ", " + resources.getString("international")))
            nodeNumInternationals.add(nodeDetails.nodeNumToString());

        if (nodeType.equals(resources.getString("data_center")) || nodeType.equals(resources.getString("data_center") + ", " + resources.getString("international")))
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
