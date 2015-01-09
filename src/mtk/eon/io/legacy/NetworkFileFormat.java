package mtk.eon.io.legacy;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;

public class NetworkFileFormat extends FileFormat<LegacyLoader> {
	
	@Override
	public String getExtension() {
		return "net";
	}

	@Override
	public boolean loadWithData(LegacyLoader loader) {
		LightScanner scanner = getStream();
		
		loader.initNodes(scanner.nextInt()); // Number of nodes
		scanner.skipInt(); // Number of links
		
		for (int idA = 0; idA < loader.getNodesCount(); idA++)
			for (int idB = 0; idB < loader.getNodesCount(); idB++) {
				int distance = scanner.nextInt();
				if (idA != idB && distance > 0) 
					loader.addLink(idA, idB, distance);
			}
		
		scanner.close();
		return true;
	}
}
