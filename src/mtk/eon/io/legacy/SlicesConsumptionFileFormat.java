package mtk.eon.io.legacy;

import java.util.List;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;
import mtk.eon.net.NetworkPath;

public class SlicesConsumptionFileFormat extends FileFormat<LegacyLoader> {

	@Override
	public String getExtension() {
		return "spec";
	}

	@Override
	public boolean loadWithData(LegacyLoader loader) {
		LightScanner scanner = getStream();
		List<NetworkPath> paths;
		
		for (int idA = 0; idA < loader.getNodesCount(); idA++)
			for (int idB = 0; idB < loader.getNodesCount(); idB++)
				if (idA != idB){
					if (idA > idB) {
						for (int i = 0; i < loader.candidatePathsCount * 6 * 40; i++) scanner.skipInt();
						continue;
					}
					paths = loader.getPaths(idA, idB);
					for (NetworkPath path : paths)
						for (int modNr = 0; modNr < 6; modNr++)
							for (int birNr = 0; birNr < 40; birNr++)
								path.slicesConsumption[modNr][birNr] = scanner.nextInt();
				}
		
		scanner.close();
		return true;
	}
}
