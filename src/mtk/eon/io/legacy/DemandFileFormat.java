package mtk.eon.io.legacy;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;
import mtk.eon.net.AnycastDemand;
import mtk.eon.net.Demand;
import mtk.eon.net.DemandAllocationResult;
import mtk.eon.net.NetworkNode;
import mtk.eon.net.PartedPath.PathPart;
import mtk.eon.net.UnicastDemand;

public class DemandFileFormat extends FileFormat<DemandLoader> {

	public static final String EXTENSION = "ddem";
	
	@Override
	public String getExtension() {
		return EXTENSION;
	}

	@Override
	public boolean loadWithData(DemandLoader loader) {
		LightScanner scanner = getStream();
		int demandsCount = scanner.nextInt();
		
		for (int i = 0; i < demandsCount; i++) {
			int demandType = scanner.nextInt();
			if (demandType == 1)
				handleDemand(new UnicastDemand(loader.getNetwork().getNode("Node_" + scanner.nextInt()), loader.getNetwork().getNode("Node_" + scanner.nextInt()), roundVolume(scanner.nextInt()), scanner.nextInt()), loader);
			else if (demandType == 2) {
				NetworkNode client = loader.getNetwork().getNode("Node_" + scanner.nextInt());
				if (client.isReplica()) {
					scanner.skipInt();
					scanner.skipInt();
					scanner.skipInt();
					continue;
				}
				int downstreamVolume = roundVolume(scanner.nextInt());
				int upstreamVolume = roundVolume(scanner.nextInt());
				int ttl = scanner.nextInt();
				handleDemand(new AnycastDemand(client, true, upstreamVolume, ttl), loader);
				handleDemand(new AnycastDemand(client, false, downstreamVolume, ttl), loader);
			}
			
			loader.getNetwork().update();
		}
		
		scanner.close();
		return true;
	}
	
	private void handleDemand(Demand demand, DemandLoader loader) {
		DemandAllocationResult result = loader.getNetwork().allocateDemand(demand);
		
		switch (result.type) {
		case NO_REGENERATORS: loader.regeneratorsBlockedVolume += demand.getVolume(); break;
		case NO_SPECTRUM: loader.spectrumBlockedVolume += demand.getVolume(); break;
		case SUCCESS:
			double modulationsUsage[] = new double[6];
			for (PathPart part : result.path) modulationsUsage[part.getModulation().ordinal()]++;
			for (int i = 0; i < 6; i++) {
				modulationsUsage[i] /= result.path.getPartsCount();
				loader.modulationsUsage[i] += modulationsUsage[i];
			}
			break;
		}
		loader.totalVolume += demand.getVolume();
	}
	
	int roundVolume(int volume) {
		return (int) Math.ceil(volume / 10.0) * 10;
	}
}
