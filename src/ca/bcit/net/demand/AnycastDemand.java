package ca.bcit.net.demand;

import ca.bcit.net.Network;
import ca.bcit.net.NetworkNode;
import ca.bcit.net.NetworkPath;
import ca.bcit.net.PartedPath;

import java.util.ArrayList;

public abstract class AnycastDemand extends Demand {

	public static class Upstream extends AnycastDemand {

		public Upstream(NetworkNode client, boolean reallocate, boolean allocateBackup, int volume, float squeezeRatio, int ttl, boolean replicaPreservation) {
			super(client, reallocate, allocateBackup, volume, squeezeRatio, ttl, replicaPreservation);
		}

		@Override
		public ArrayList<PartedPath> getCandidatePaths(boolean backup, Network network) {
			ArrayList<PartedPath> paths = new ArrayList<>();

			if (backup && workingPath != null)
				if (replicaPreservation) {
					NetworkPath networkPath = workingPath.getPath();
					NetworkNode replica = networkPath.get(0) == client ? networkPath.get(networkPath.size() - 1) : networkPath.get(0);
					int acceptedPaths = 0;
					for (NetworkPath path : network.getPaths(client, replica))
						if (!network.isInactive(path))
							if (acceptedPaths >= network.getBestPathsCount()) break;
							else if (path.isDisjoint(workingPath)) {
								paths.add(new PartedPath(network, path, path.get(0) == client));
								acceptedPaths++;
							}
				}
				else
					for (NetworkNode replica : network.getGroup("replicas")) {
						int acceptedPaths = 0;
						if (client == replica)
							continue;

						for (NetworkPath path : network.getPaths(client, replica))
							if (!network.isInactive(path))
								if (acceptedPaths >= network.getBestPathsCount())
									break;
								else if (path.isDisjoint(workingPath)) {
									paths.add(new PartedPath(network, path, path.get(0) == client));
									acceptedPaths++;
								}
					}
			else
				for (NetworkNode replica : network.getGroup("replicas")) {
					if (client == replica)
						continue;

					for (NetworkPath path : network.getPaths(client, replica).subList(0, network.getBestPathsCount()))
						if (!network.isInactive(path))
							paths.add(new PartedPath(network, path, path.get(0) == client));
				}
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
			ArrayList<PartedPath> paths = new ArrayList<>();

			if (backup)
				if (replicaPreservation && workingPath != null) {
					NetworkPath networkPath = workingPath.getPath();
					NetworkNode replica = networkPath.get(0) == client ? networkPath.get(networkPath.size() - 1) : networkPath.get(0);
					int acceptedPaths = 0;
					for (NetworkPath path : network.getPaths(client, replica))
						if (!network.isInactive(path))
							if (acceptedPaths >= network.getBestPathsCount())
								break;
							else if (path.isDisjoint(workingPath)) {
								paths.add(new PartedPath(network, path, path.get(0) == replica));
								acceptedPaths++;
							}
				}
				else
					for (NetworkNode replica : network.getGroup("replicas")) {
						int acceptedPaths = 0;
						if (client == replica)
							continue;

						for (NetworkPath path : network.getPaths(client, replica))
							if (!network.isInactive(path))
								if (acceptedPaths >= network.getBestPathsCount())
									break;
								else if (path.isDisjoint(workingPath)) {
									paths.add(new PartedPath(network, path, path.get(0) == replica));
									acceptedPaths++;
								}
					}
			for (NetworkNode replica : network.getGroup("replicas")) {
				if (client == replica)
					continue;

				for (NetworkPath path : network.getPaths(client, replica).subList(0, network.getBestPathsCount()))
					if (!network.isInactive(path))
						paths.add(new PartedPath(network, path, path.get(0) == client));
			}

			paths.sort(PartedPath.LENGTH_COMPARATOR);
			if (paths.size() > network.getBestPathsCount())
				paths.subList(network.getBestPathsCount(), paths.size()).clear();

			return paths;
		}
	}

	final NetworkNode client;
	final boolean replicaPreservation;

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

	@Override
	public String toString() {
		return "AnycastDemand {client: " + client + ", volume: " + getVolume() + ", ttl: " + getTTL() + "}";
	}
}