package mtk.eon.net;

import java.util.HashMap;
import java.util.Map;

import mtk.eon.graph.positioned.PositionedNode;
import mtk.eon.io.YamlSerializable;

/**
 * Network Node with information about the regenerators
 * @author Michal
 *
 */
public class NetworkNode extends PositionedNode implements YamlSerializable {

	String name;
	int regeneratorsCount, occupiedRegenerators;
	int cpu, occupiedCPU;
	int memory, occupiedMemory;
	int storage, occupiedStorage;
	
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
	
	public void occupyRegenerators(int count) {
		occupiedRegenerators += count;
		if (occupiedRegenerators > regeneratorsCount || occupiedRegenerators < 0)
			throw new NetworkException("Regenerators occupation exception! (" + occupiedRegenerators + "/" +
					regeneratorsCount + ")");
	}
	
	public void occupyMemory(int count) {
		occupiedMemory += count;
		if (occupiedMemory > memory || occupiedMemory < 0)
			throw new NetworkException("Memory occupation exception! (" + occupiedMemory + "/" +
					memory + ")");
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
	
	public void occupyCpu(int count) {
		occupiedCPU += count;
		if (occupiedCPU > cpu || occupiedCPU < 0)
			throw new NetworkException("CPU occupation exception! (" + occupiedCPU + "/" +
					cpu + ")");
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
	
	public void occupyStorage(int count) {
		occupiedStorage += count;
		if (occupiedStorage > storage || occupiedStorage < 0)
			throw new NetworkException("Storage occupation exception! (" + occupiedStorage + "/" +
					storage + ")");
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
