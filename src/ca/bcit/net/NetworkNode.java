package ca.bcit.net;

import ca.bcit.graph.positioned.PositionedNode;
import ca.bcit.io.YamlSerializable;

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
	int cpu = 40, occupiedCPU;
	int memory = 160, occupiedMemory;
	int storage = 4000, occupiedStorage;

	public NetworkNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void clearOccupied(){
		this.occupiedCPU = 0;
		this.occupiedMemory = 0;
		this.occupiedRegenerators = 0;
		this.occupiedStorage = 0;
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

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public int getFreeMemory() {
		return memory - occupiedMemory;
	}

	public boolean hasFreeMemory() {
		return memory - occupiedMemory > 0;
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

	public void occupyMemory(int count, boolean deallocate) {
		if (deallocate) {
			occupiedMemory += count;
		} else if ((count > memory - occupiedMemory || occupiedMemory < 0) && !deallocate) {
			throw new MemoryException("Memory occupation exception! (" + occupiedMemory + "/" + memory + ")");
		} else {
			occupiedMemory += count;
		}
	}

	public void setCpu(int cpu) {
		this.cpu = cpu;
	}

	public int getFreeCpu() {
		return cpu - occupiedCPU;
	}

	public boolean hasFreeCpu() {
		return cpu - occupiedCPU > 0;
	}

	public void occupyCpu(int count, boolean deallocate) {
		if (deallocate) {
			occupiedCPU += count;
		} else if ((count > cpu - occupiedCPU || occupiedCPU < 0) && !deallocate) {
			throw new CPUException("CPU occupation exception! (" + occupiedCPU + "/" + cpu + ")");
		} else {
			occupiedCPU += count;
		}
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public int getFreeStorage() {
		return storage - occupiedStorage;
	}

	public boolean hasFreeStorage() {
		return storage - occupiedStorage > 0;
	}

	public void occupyStorage(int count, boolean deallocate) {
		if (deallocate) {
			occupiedStorage += count;
		} else if ((count > storage - occupiedStorage || occupiedStorage < 0) && !deallocate) {
			throw new StorageException("Storage occupation exception! (" + occupiedStorage + "/" + storage + ")");
		} else {
			occupiedStorage += count;
		}
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
