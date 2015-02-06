package mtk.eon.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mtk.eon.net.DemandAllocationResult.Type;
import mtk.eon.net.algo.Algorithm;
import mtk.graph.Graph;
import mtk.graph.positioned.NodeCluster;


public class Network extends Graph<NetworkNode, NetworkLink, NetworkPath, Network> {
	
	HashMap<String, NetworkNode> nodes = new HashMap<String, NetworkNode>();
	ArrayList<NetworkNode> replicas = new ArrayList<NetworkNode>();

	List<Modulation> modulations = new ArrayList<Modulation>();
	int[][] modulationDistances = new int[6][40];
	MetricType modulationMetricType;
	int[][] modulationMetrics = new int[6][6];
	
	MetricType regeneratorMetricType;
	int regeneratorMetricValue;
	
	Algorithm demandAllocationAlgorithm;
	int bestPathsCount;
	boolean canSwitchModulation;
	ArrayList<Demand> allocatedDemands = new ArrayList<Demand>();
	
	public Network() {
		super(new NetworkPathBuilder());
	}
	
	// DEMANDS
	
	public void setDemandAllocationAlgorithm(Algorithm algorithm) {
		this.demandAllocationAlgorithm = algorithm;
	}
	
	public void setBestPathsCount(int bestPathsCount) {
		this.bestPathsCount = bestPathsCount;
	}
	
	public int getBestPathsCount() {
		return bestPathsCount;
	}
	
	public void setCanSwitchModulation(boolean canSwitchModulation) {
		this.canSwitchModulation = canSwitchModulation;
	}
	
	public boolean canSwitchModulation() {
		return canSwitchModulation;
	}
	
	public DemandAllocationResult allocateDemand(Demand demand) {
		DemandAllocationResult result = demandAllocationAlgorithm.allocateDemand(demand, this);
		if (result.type == Type.SUCCESS) allocatedDemands.add(demand);
		return result;
	}
	
	public void update() {
		for (int i = 0; i < allocatedDemands.size(); i++)
			if (allocatedDemands.get(i).isDead()) {
				allocatedDemands.get(i).deallocate();
				allocatedDemands.remove(i);
				i--;
			} else
				allocatedDemands.get(i).tick();
	}
	
	public void waitForDemandsDeath() {
		while (!allocatedDemands.isEmpty()) update();
	}
	
	// NODES
	
	public NetworkNode getNode(String name) {
		return nodes.get(name);
	}
	
	public boolean addReplica(NetworkNode node) {
		if (!contains(node) || replicas.contains(node)) return false;
		replicas.add(node);
		node.isReplica = true;
		return true;
	}
	
	public boolean removeReplica(NetworkNode node) {
		if (!contains(node) || !replicas.contains(node)) return false;
		replicas.remove(node);
		node.isReplica = false;
		return true;
	}
	
	public ArrayList<NetworkNode> getReplicas() {
		return replicas;
	}
	
	@Override
	protected boolean addNode(NetworkNode node) {
		boolean result = super.addNode(node);
		if (result) nodes.put(node.getName(), node);
		return result;
	}
	
	@Override
	public boolean removeNode(NetworkNode node) {
		boolean result = super.removeNode(node);
		if (result) nodes.remove(node.getName());
		return result;
	}
	
	// LINKS
	
	public Slices getLinkSlices(NetworkNode source, NetworkNode destination) {
		NetworkLink link = getLink(source, destination);
		return source.getID() < destination.getID() ? link.slicesUp : link.slicesDown;
	}
	
	// MODULATION
	
	public MetricType getModualtionMetricType() {
		return modulationMetricType;
	}
	
	public int getModulationDistance(Modulation modulation, int volume) {
		return modulationDistances[modulation.ordinal()][volume];
	}
	
	public void setModulationDistance(Modulation modulation, int volume, int distance) {
		modulationDistances[modulation.ordinal()][volume] = distance;
	}
	
	public int getDynamicModulationMetric(Modulation modulation, int slicesOccupationMetric) {
		if (modulationMetricType != MetricType.DYNAMIC) throw new NetworkException("Trying to obtain dynamic modulation metric when the network is in static modulation metric mode.");
		if (slicesOccupationMetric < 0 || slicesOccupationMetric > 5) throw new NetworkException("Slices occupation metric must be >= 0 and <= 5!");
		return modulationMetrics[slicesOccupationMetric][modulation.ordinal()];
	}
	
	public int getStaticModulationMetric(Modulation modulation) {
		if (modulationMetricType != MetricType.STATIC) throw new NetworkException("Trying to obtain static modulation metric when the network is in dynamic modulation metric mode.");
		return modulationMetrics[0][modulation.ordinal()];
	}
	
	public void setModualtionMetricType(MetricType modulationMetricType) {
		this.modulationMetricType = modulationMetricType;
		if (modulationMetricType == MetricType.DYNAMIC)
			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 6; j++)
					modulationMetrics[i][j] = j <= i ? i - j : j;
	}
	
	public void setStaticModulationMetric(Modulation modulation, int metric) {
		modulationMetrics[0][modulation.ordinal()] = metric;
	}
	
	public void allowModulation(Modulation modulation) {
		if (!modulations.contains(modulation))
			modulations.add(modulation);
	}
	
	public void disallowModulation(Modulation modulation) {
		if (modulations.contains(modulation))
			modulations.remove(modulation);
	}
	
	public List<Modulation> getAllowedModulations() {
		return new ArrayList<Modulation>(modulations);
	}
	
	// REGENERATORS
	
	public MetricType getRegeneratorMetricType() {
		return regeneratorMetricType;
	}
	
	public void setRegeneratorMetricType(MetricType regeneratorMetricType) {
		this.regeneratorMetricType = regeneratorMetricType;
	}
	
	public int getRegeneratorMetricValue() {
		return regeneratorMetricValue;
	}
	
	public void setRegeneratorMetricValue(int regeneratorMetricValue) {
		this.regeneratorMetricValue = regeneratorMetricValue;
	}
}
