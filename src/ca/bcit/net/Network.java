package ca.bcit.net;

import ca.bcit.graph.Graph;
import ca.bcit.graph.Relation;
import ca.bcit.io.YamlSerializable;
import ca.bcit.net.algo.IRMSAAlgorithm;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.demand.generator.TrafficGenerator;
import ca.bcit.net.spectrum.BackupSpectrumSegment;
import ca.bcit.net.spectrum.Spectrum;
import ca.bcit.net.spectrum.SpectrumSegment;
import ca.bcit.net.spectrum.WorkingSpectrumSegment;

import java.util.*;
import java.util.Map.Entry;

/**
 * Class Network has main data about the network 
 */
public class Network extends Graph<NetworkNode, NetworkLink, NetworkPath, Network> implements YamlSerializable {
	final Map<String, NetworkNode> nodes = new HashMap<>();
	private final Map<String, List<NetworkNode>> nodesGroups = new HashMap<>();

	private final Set<Relation<NetworkNode, NetworkLink, NetworkPath>> inactiveLinks = new HashSet<>();
	private final Set<NetworkPath> inactivePaths = new HashSet<>();
	
	private final List<Modulation> modulations = new ArrayList<>();
	private MetricType modulationMetricType;
	private final int[][] modulationMetrics = new int[6][6];
	
	private MetricType regeneratorMetricType;
	private int regeneratorMetricValue = 5;
	
	private IRMSAAlgorithm demandAllocationAlgorithm;

	private TrafficGenerator trafficGenerator;
	private int bestPathsCount;
	private boolean canSwitchModulation;
	private ArrayList<Demand> allocatedDemands = new ArrayList<>();

	private int maxPathsCount;

	public Network() {
		super(new NetworkPathBuilder());
	}

	public int getMaxPathsCount() {
		return maxPathsCount;
	}

	public void setMaxPathsCount(int maxPathsCount) {
		if (maxPathsCount >= 0)
			this.maxPathsCount = maxPathsCount;
		else
			throw new IllegalArgumentException("max_path_count_must_be_positive");
	}

	public void setTrafficGenerator(TrafficGenerator trafficGenerator) {
		this.trafficGenerator = trafficGenerator;
	}
	
	// DEMANDS
	
	public void setDemandAllocationAlgorithm(IRMSAAlgorithm algorithm) {
		this.demandAllocationAlgorithm = algorithm;
	}

	public IRMSAAlgorithm getDemandAllocationAlgorithm(){
		return demandAllocationAlgorithm;
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
		if (result.type == DemandAllocationResult.Type.SUCCESS)
			allocatedDemands.add(demand);
		return result;
	}
	
	public void update() {
		for (int i = 0; i < allocatedDemands.size(); i++)
			if (allocatedDemands.get(i).isDead()) {
				allocatedDemands.get(i).deallocate();
				allocatedDemands.remove(i);
				i--;
			}
			else
				allocatedDemands.get(i).tick();
	}
	
	public void waitForDemandsDeath() {
		while (!allocatedDemands.isEmpty()) update();
		allocatedDemands = new ArrayList<>();
		for (Map.Entry<String, NetworkNode> entry: nodes.entrySet())
			entry.getValue().clearOccupied();

		inactiveLinks.clear();
		inactivePaths.clear();
	}
	
	// NODES GROUPS
	
	private boolean addNodeToGroup(String groupName, NetworkNode node) {
		if (!contains(node))
			return false;
		List<NetworkNode> group = nodesGroups.computeIfAbsent(groupName, k -> new ArrayList<>());
		if (group.contains(node))
			return false;
		node.setNodeGroup(groupName, true);
		group.add(node);
		return true;
	}
	
	public boolean removeNodeFromGroup(String groupName, NetworkNode node) {
		if (!contains(node))
			return false;
		List<NetworkNode> group = nodesGroups.get(groupName);
		if (group == null)
			return false;
		if (!group.contains(node))
			return false;
		node.setNodeGroup(groupName, false);
		group.remove(node);
		if (group.isEmpty())
			nodesGroups.remove(groupName);
		return true;
	}

	public List<NetworkNode> getGroup(String name) {
		return nodesGroups.get(name);
	}
	
	// NODES
	
	private NetworkNode getNode(String name) {
		return nodes.get(name);
	}
	
	@Override
	protected boolean addNode(NetworkNode node) {
		boolean result = super.addNode(node);
		if (result)
			nodes.put(node.getName(), node);
		return result;
	}
	
	@Override
	public boolean removeNode(NetworkNode node) {
		boolean result = super.removeNode(node);
		if (result)
			nodes.remove(node.getName());
		return result;
	}
	
	// LINKS
	
	private Random linkDestroyer;
	
	public void setSeed(long seed) {
		linkDestroyer = new Random(seed);
	}
	
	public Set<Demand> cutLink() {
		List<Relation<NetworkNode, NetworkLink, NetworkPath>> links = new ArrayList<>();
		for (Relation<NetworkNode, NetworkLink, NetworkPath> relation : relations)
			if (relation.hasLink() && !inactiveLinks.contains(relation))
				links.add(relation);
		Relation<NetworkNode, NetworkLink, NetworkPath> link = links.get(linkDestroyer.nextInt(links.size()));
		inactiveLinks.add(link);
		for (Relation<NetworkNode, NetworkLink, NetworkPath> relation : relations)
			for (NetworkPath path : relation.getPaths())
				if (Math.abs(path.indexOf(relation.nodeA) - path.indexOf(relation.nodeB)) == 1)
					inactivePaths.add(path);
		
		Set<Demand> working = new HashSet<>();
		Set<Demand> backup = new HashSet<>();
		Set<Demand> result = new HashSet<>();
		for (SpectrumSegment segment : link.getLink().slicesDown.getSegments())
			if (segment instanceof WorkingSpectrumSegment)
				working.add(((WorkingSpectrumSegment) segment).getOwner());
			else if (segment instanceof BackupSpectrumSegment)
				backup.addAll(((BackupSpectrumSegment) segment).getDemands());

		for (SpectrumSegment segment : link.getLink().slicesUp.getSegments())
			if (segment instanceof WorkingSpectrumSegment)
				working.add(((WorkingSpectrumSegment) segment).getOwner());
			else if (segment instanceof BackupSpectrumSegment)
				backup.addAll(((BackupSpectrumSegment) segment).getDemands());

		for (Demand demand : working)
			if (!demand.onWorkingFailure()) {
				result.add(demand);
				allocatedDemands.remove(demand);
			}

		for (Demand demand : backup)
			demand.onBackupFailure();
		
		if (links.size() == 1)
			throw new NetworkException("all_links_in_the_network_failed");
		
		return result;
	}
	
	public boolean isInactive(NetworkPath path) {
		return inactivePaths.contains(path);
	}
	
	public Spectrum getLinkSlices(NetworkNode source, NetworkNode destination) {
		NetworkLink link = getLink(source, destination);
		return source.getID() < destination.getID() ? link.slicesUp : link.slicesDown;
	}
	
	// MODULATION
	
	public MetricType getModualtionMetricType() {
		return modulationMetricType;
	}
	
	public int getDynamicModulationMetric(Modulation modulation, int slicesOccupationMetric) {
		if (modulationMetricType != MetricType.DYNAMIC)
			throw new NetworkException("trying_to_obtain_dynamic_modulation_when_the_network_is_in_static_modulation_metric_mode");
		if (slicesOccupationMetric < 0 || slicesOccupationMetric > 5)
			throw new NetworkException("slices_occupation_metric_must_be_between_0_and_5");
		return modulationMetrics[slicesOccupationMetric][modulation.ordinal()];
	}
	
	public int getStaticModulationMetric(Modulation modulation) {
		if (modulationMetricType != MetricType.STATIC)
			throw new NetworkException("trying_to_obtain_dynamic_modulation_when_the_network_is_in_static_modulation_metric_mode");
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
		return new ArrayList<>(modulations);
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
	
	// DESERIALIZATION
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Network(Map map) {
		super(new NetworkPathBuilder());
		List<NetworkNode> nodes = (List<NetworkNode>) map.get("nodes");
		if (nodes != null)
			for (NetworkNode node : nodes)
				addNode(node);

		Map<List<String>, NetworkLink> links = (Map<List<String>, NetworkLink>) map.get("links");
		if (links != null)
			for (Entry<List<String>, NetworkLink> link : links.entrySet())
				putLink(getNode(link.getKey().get(0)), getNode(link.getKey().get(1)), link.getValue());

		Map<String, List<String>> groups = (Map<String, List<String>>) map.get("groups");
		if (groups != null)
			for (Entry<String, List<String>> group : groups.entrySet())
				for (String node : group.getValue())
					addNodeToGroup(group.getKey(), getNode(node));
	}

	// SERIALIZATION
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		List<NetworkNode> nodes = getNodes();
		map.put("nodes", nodes);
		Map<List<String>, NetworkLink> links = new HashMap<>();
		for (int i = 0; i < nodes.size(); i++)
			for (int j = i + 1; j < nodes.size(); j++)
				if (containsLink(nodes.get(i), nodes.get(j)))
					links.put(Arrays.asList(nodes.get(i).getName(), nodes.get(j).getName()),
							getLink(nodes.get(i), nodes.get(j)));
		map.put("links", links);
		Map<String, List<String>> groups = new HashMap<>();
		for (Entry<String, List<NetworkNode>> group : nodesGroups.entrySet()) {
			groups.put(group.getKey(), new ArrayList<>());
			for (NetworkNode node : group.getValue())
				groups.get(group.getKey()).add(node.getName());
		}
		map.put("groups", groups);
		return map;
	}
}
