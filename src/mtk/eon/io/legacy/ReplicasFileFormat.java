package mtk.eon.io.legacy;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;

public class ReplicasFileFormat extends FileFormat<LegacyLoader> {

	@Override
	public String getExtension() {
		return "rep";
	}

	@Override
	public boolean loadWithData(LegacyLoader loader) {
		LightScanner scanner = getStream();
		
		int replicsCount = scanner.nextInt();
		for (int i = 0; i < replicsCount; i++)
			loader.addReplica(scanner.nextInt());
		
		scanner.close();
		return true;
	}
}
