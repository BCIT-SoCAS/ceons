package ca.bcit.net;

import ca.bcit.graph.positioned.PositionedNode;
import ca.bcit.io.YamlSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Network Node with information about the regenerators
 * 
 * @author Michal
 *
 */
public class NetworkNode extends PositionedNode implements YamlSerializable {

	private final String name;
	int regeneratorsCount, occupiedRegenerators;
	private int maxOccupiedRegenerators = 0;
	private ArrayList<Integer> occupiedRegeneratorRecords = new ArrayList<Integer>();
	private ArrayList<Integer> linkLengths = new ArrayList<Integer>();
	private boolean isReplica;
	private boolean isInternational;

	public NetworkNode(String name) {
		this.name = name;
        this.resetNodeStat();
	}

	public String getName() {
		return name;
	}
	
	public void clearOccupied(){
		this.occupiedRegenerators = 0;
	}

	public void resetNodeStat(){
        this.occupiedRegenerators = 0;
        this.maxOccupiedRegenerators = 0;
        this.occupiedRegeneratorRecords.clear();
        this.linkLengths.clear();
        this.setReplicaStatus(false);
        this.setInternationalStatus(false);
    }

	public void setRegeneratorsCount(int regeneratorsCount) {
		this.regeneratorsCount = regeneratorsCount;
	}

	public int getFreeRegenerators() {
		return regeneratorsCount - occupiedRegenerators;
	}

	public boolean hasFreeRegenerators() {
		return regeneratorsCount - occupiedRegenerators > 0;
	}

	public void occupyRegenerators(int count, boolean deallocate) {
		if (deallocate) {
			occupiedRegenerators += count;
		} else if ((count > regeneratorsCount - occupiedRegenerators || occupiedRegenerators < 0) && !deallocate) {
			throw new NetworkException(
					"Regenerators occupation exception! (" + occupiedRegenerators + "/" + regeneratorsCount + ")");
		} else {
			occupiedRegenerators += count;
		}
	}

	public void recordCurrRegStats(){
		this.updateMaxOccupiedCount();
		this.saveCurrOccupiedCount();
	}

	private void updateMaxOccupiedCount(){
		int currentOccupiedRegCount = this.occupiedRegenerators;
		if (currentOccupiedRegCount > this.maxOccupiedRegenerators)
			this.maxOccupiedRegenerators = currentOccupiedRegCount;
	}

	private void saveCurrOccupiedCount(){
		this.occupiedRegeneratorRecords.add(this.occupiedRegenerators);
	}

	public double getAvgOccupiedRegCount(){
	    double recordCount = this.occupiedRegeneratorRecords.size();
	    double recordOccupiedRegSum = 0;
	    for(int currOccupiedReg : this.occupiedRegeneratorRecords)
	        recordOccupiedRegSum += currOccupiedReg;

	    return (Math.round((recordOccupiedRegSum / recordCount) * 100.00) / 100.00);
    }

    public int getMaxOccupiedRegenerators(){
	    return this.maxOccupiedRegenerators;
    }

	public void addLinkLength(int newLength){
		this.linkLengths.add(newLength);
	}

	public void setReplicaStatus(boolean inputFlag){
	    this.isReplica = inputFlag;
    }

    public void setInternationalStatus(boolean inputFlag){
        this.isInternational = inputFlag;
    }

	public boolean getReplicaStatus(){
	    return this.isReplica;
    }

    public boolean getInternationalStatus() {
	    return this.isInternational;
    }

	public int getLinkCount() {
		return this.linkLengths.size();
	}

	public int getSumLinkLength() {
		int sum = 0;
		for(int length : this.linkLengths){
			sum += length;
		}
		return sum;
	}

	public double getAvgLinkLength() {
		double linkCount = this.getLinkCount();
		double linkSum = this.getSumLinkLength();
		return (Math.round((linkSum / linkCount) * 100.00) / 100.00);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NetworkNode && ((NetworkNode) o).getName().equals(name);
	}

	@Override
	public String toString() {
		return "{name: " + name + ", regenerators: " + regeneratorsCount + "}";
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private NetworkNode(Map map) {
		name = (String) map.get("name");
		regeneratorsCount = (Integer) map.get("regenerators");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("regenerators", regeneratorsCount);
		return map;
	}
}
