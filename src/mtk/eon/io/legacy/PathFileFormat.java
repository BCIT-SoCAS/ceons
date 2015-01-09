package mtk.eon.io.legacy;

import java.util.ArrayList;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;

public class PathFileFormat extends FileFormat<LegacyLoader> {

	@Override
	public String getExtension() {
		return "pat";
	}

	@Override
	public boolean loadWithData(LegacyLoader loader) {
		LightScanner scanner = getStream();
		loader.candidatePathsCount = Integer.parseInt(getFile().getName().substring(1, getFile().getName().length() - 4));
		scanner.skipInt(); // all paths count
		
		for (int idA = 0; idA < loader.getNodesCount(); idA++)
			for (int idB = 0; idB < loader.getNodesCount(); idB++)
				if (idA != idB) {
					if (idA > idB) {
						for (int i = 0; i < loader.candidatePathsCount * loader.getLinksCount(); i++) scanner.skipInt();
						continue;
					}
					for (int k = 0; k < loader.candidatePathsCount; k++) {
						ArrayList<LegacyLoader.Pair> links = new ArrayList<LegacyLoader.Pair>();
						for (int i = 0; i < loader.getLinksCount(); i++)
							if (scanner.nextInt() > 0)
								links.add(loader.getLink(i));
						loader.addPath(idA, idB, links);
					}
				}
		
		scanner.close();
		return true;
	}

}
