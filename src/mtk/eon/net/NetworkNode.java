package mtk.eon.net;

import java.util.HashMap;
import java.util.Map;

import mtk.eon.graph.positioned.PositionedNode;
import mtk.eon.io.YamlSerializable;

/**
 * Network Node with information about the regenerators
 * 
 * @author Michal
 *
 */
public class NetworkNode extends PositionedNode implements YamlSerializable {

	String name;
	int regeneratorsCount, occupiedRegenerators;
	int cpu = 30, occupiedCPU;
	int memory = 1600, occupiedMemory;
	int storage = 10000, occupiedStorage;

	public NetworkNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
		} else if ((occupiedRegenerators > regeneratorsCount || occupiedRegenerators < 0) && !deallocate) {
			throw new NetworkException(
					"Regenerators occupation exception! (" + occupiedRegenerators + "/" + regeneratorsCount + ")");
		} else {
			occupiedRegenerators += count;
		}
	}

	public void occupyMemory(int count, boolean deallocate) {
		if (deallocate) {
			occupiedMemory += count;
		} else if ((occupiedMemory > memory || occupiedMemory < 0) && !deallocate) {
			throw new MemoryException("Memory occupation exception! (" + occupiedMemory + "/" + memory + ")");
		}
		occupiedMemory += count;
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
		} else if ((occupiedCPU > cpu || occupiedCPU < 0) && !deallocate) {
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
		} else if ((occupiedStorage > storage || occupiedStorage < 0) && !deallocate) {
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("regenerators", regeneratorsCount);
		return map;
	}
}
