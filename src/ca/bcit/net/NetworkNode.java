package ca.bcit.net;

import ca.bcit.Settings;
import ca.bcit.graph.positioned.PositionedNode;
import ca.bcit.io.YamlSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.bcit.drawing.Node;

/**
 * Network Node with information about the regenerators
 */
public class NetworkNode extends PositionedNode implements YamlSerializable {

	private final String name;
	private final String location;
	Regenerator[] regenerators;

	private Node figureNode;
	private HashMap<String, Boolean> nodeGroups = new HashMap<>();

	public NetworkNode(){
		this.name = "unknown";
		this.location = "unknown";

		initializeRegenerators(Settings.DEFAULT_NUMBER_OF_REGENERATORS);
	}

	public NetworkNode(String name, String location) {
		this.name = name;
		this.location = location;

		initializeRegenerators(Settings.DEFAULT_NUMBER_OF_REGENERATORS);
	}

	private void initializeRegenerators(int numberOfRegenerators) {
		regenerators = new Regenerator[numberOfRegenerators];
		for (int i = 0; i < regenerators.length; i++)
			regenerators[i] = new Regenerator();
	}

	public void setFigure() {
		this.figureNode = new Node(getPosition(), getName(), getFreeRegenerators(), getNodeGroups());
	}

	public Node getFigure() {
		return this.figureNode;
	}

	public void updateRegeneratorCount() {
		this.figureNode.setNumberOfRegenerators(getFreeRegenerators());
	}

	public String getName() {
		return name;
	}

	public int getNodeNum(){
		return Integer.parseInt(name.split("_")[1]);
	}

	public String getLocation(){
		return location;
	}

	public int getRegeneratorsCount(){
		return regenerators.length;
	}

	public void clearRegenerators(){
		for (Regenerator regenerator : regenerators)
			regenerator.setOccupiedStatuses(new boolean[Settings.numberOfCores]);
	}

	public void setNodeGroup(String groupName, Boolean value) {
		this.nodeGroups.put(groupName, value);
	}

	public HashMap<String, Boolean> getNodeGroups() {
		return this.nodeGroups;
	}

	public int getFreeRegenerators() {
		int freeRegenerators = 0;

		for (Regenerator regenerator : regenerators) {
			boolean isFree = false;
			for (boolean occupied : regenerator.getOccupiedStatuses())
				if (!occupied) {
					isFree = true;
					break;
				}

			if (isFree)
				freeRegenerators++;
		}

		return freeRegenerators;
	}

	public boolean hasFreeRegenerators() {
		for (Regenerator regenerator : regenerators) {
			for (boolean occupied : regenerator.getOccupiedStatuses())
				if (!occupied)
					return true;
		}

		return false;
	}

	public void allocateRegenerator(int coreId) {
		for (Regenerator r : regenerators ) {
			if (r.getOccupiedStatuses()[coreId])
				continue;

			r.getOccupiedStatuses()[coreId] = true;
			return;
		}

		throw new NoRegeneratorsAvailableException("No regenerators available to allocate to this core id in this network node");
	}

	public void deallocateRegenerator(int coreId) {
		for (Regenerator r : regenerators ) {
			if (!r.getOccupiedStatuses()[coreId])
				continue;

			r.getOccupiedStatuses()[coreId] = false;
			return;
		}

		throw new NetworkException("No regenerators allocated for this core id");
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NetworkNode && ((NetworkNode) o).getName().equals(name);
	}

	@Override
	public String toString() {
        return "{name: " + name + ", regenerators: " + regenerators.length + ", xcoordinate: " + getPosition().getX() + ", ycoordinate: " + getPosition().getY() + "}";
	}

	private NetworkNode(Map map) {
		name = (String) map.get("name");
		location = (String) map.get("location");
		initializeRegenerators((Integer) map.get("regenerators"));
        setPosition((Integer) map.get("xcoordinate"),(Integer) map.get("ycoordinate"));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("regenerators", regenerators.length);
		map.put("xcoordinate", getPosition().getX());
		map.put("ycoordinate", getPosition().getY());
		return map;
	}
}
