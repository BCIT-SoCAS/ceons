package mtk.eon.net.demand;

import java.util.ArrayList;

import mtk.eon.net.Network;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.NetworkPath;
import mtk.eon.net.PartedPath;

public abstract class AnycastDemand extends Demand {
	
	public static class Upstream extends AnycastDemand {

		public Upstream(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, int squeezedVolume, int ttl, boolean replicaPreservation) {
			super(client, reallocate, allocateBackup, volume, squeezedVolume, ttl, replicaPreservation);
		}
		
		public Upstream(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, float squeezeRatio, int ttl, boolean replicaPreservation) {
			super(client, reallocate, allocateBackup, volume, squeezeRatio, ttl, replicaPreservation);
		}
		
		@Override
		public ArrayList<PartedPath> getCandidatePaths(boolean backup, Network network) {
			ArrayList<PartedPath> paths = new ArrayList<PartedPath>();
			
			if (backup)
				if (replicaPreservation) {
					NetworkNode replica = workingPath.getPath().get(0) == client ? workingPath.getPath().get(workingPath.getPath().size() - 1) : workingPath.getPath().get(0);
					int acceptedPaths = 0;
					for (NetworkPath path : network.getPaths(client , replica))
						if (!network.isInactive(path))
							if (acceptedPaths >= network.getBestPathsCount()) break;
							else if (path.isDisjoint(workingPath)) {
								paths.add(new PartedPath(network, path, path.get(0) == client));
								acceptedPaths++;
							}
				} else
					for (NetworkNode replica : network.getGroup("replicas")) {
						int acceptedPaths = 0;
						for (NetworkPath path : network.getPaths(client , replica))
							if (!network.isInactive(path))
								if (acceptedPaths >= network.getBestPathsCount()) break;
								else if (path.isDisjoint(workingPath)) {
									paths.add(new PartedPath(network, path, path.get(0) == client));
									acceptedPaths++;
								}
					}
			else
				for (NetworkNode replica : network.getGroup("replicas"))
					for (NetworkPath path : network.getPaths(client , replica).subList(0, network.getBestPathsCount()))
						if (!network.isInactive(path))
							paths.add(new PartedPath(network, path, path.get(0) == client));
			
			paths.sort(PartedPath.LENGTH_COMPARATOR);
			if (paths.size() > network.getBestPathsCount())
				paths.subList(network.getBestPathsCount(), paths.size()).clear();
			
			return paths;
		}
	}

	public static class Downstream extends AnycastDemand {

		public Downstream(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, int squeezedVolume, int ttl, boolean replicaPreservation) {
			super(client, reallocate, allocateBackup, volume, squeezedVolume, ttl, replicaPreservation);
		}
		
		public Downstream(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, float squeezeRatio, int ttl, boolean replicaPreservation) {
			super(client, reallocate, allocateBackup, volume, squeezeRatio, ttl, replicaPreservation);
		}
		
		@Override
		public ArrayList<PartedPath> getCandidatePaths(boolean backup, Network network) {
			ArrayList<PartedPath> paths = new ArrayList<PartedPath>();

			if (backup)
				if (replicaPreservation) {
					NetworkNode replica = workingPath.getPath().get(0) == client ? workingPath.getPath().get(workingPath.getPath().size() - 1) : workingPath.getPath().get(0);
					int acceptedPaths = 0;
					for (NetworkPath path : network.getPaths(client , replica))
						if (!network.isInactive(path))
							if (acceptedPaths >= network.getBestPathsCount()) break;
							else if (path.isDisjoint(workingPath)) {
								paths.add(new PartedPath(network, path, path.get(0) == replica));
								acceptedPaths++;
							}
				} else
					for (NetworkNode replica : network.getGroup("replicas")) {
						int acceptedPaths = 0;
						for (NetworkPath path : network.getPaths(client , replica))
							if (!network.isInactive(path))
								if (acceptedPaths >= network.getBestPathsCount()) break;
								else if (path.isDisjoint(workingPath)) {
									paths.add(new PartedPath(network, path, path.get(0) == replica));
									acceptedPaths++;
								}
					}
			else
				for (NetworkNode replica : network.getGroup("replicas"))
					for (NetworkPath path : network.getPaths(replica, client).subList(0, network.getBestPathsCount()))
						if (!network.isInactive(path))
							paths.add(new PartedPath(network, path, path.get(0) == replica));
			
			paths.sort(PartedPath.LENGTH_COMPARATOR);
			if (paths.size() > network.getBestPathsCount())
				paths.subList(network.getBestPathsCount(), paths.size()).clear();
			
			return paths;
		}
	}
	
	NetworkNode client;
	boolean replicaPreservation;
	
	public AnycastDemand(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, int squeezedVolume, int ttl, boolean replicaPreservation) {
		super(reallocate, allocateBackup, volume, squeezedVolume, ttl);
		this.client = client;
		this.replicaPreservation = replicaPreservation;
	}
	
	public AnycastDemand(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, float squeezeRatio, int ttl, boolean replicaPreservation) {
		super(reallocate, allocateBackup, volume, squeezeRatio, ttl);
		this.client = client;
		this.replicaPreservation = replicaPreservation;
	}
	
	public NetworkNode getClient() {
		return client;
	}
	
	@Override
	public String toString() {
		return "AnycastDemand {client: " + client + ", volume: " + getVolume() + ", ttl: " + getTTL() + "}";
	}
}
