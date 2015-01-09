package mtk.eon.io.legacy;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;
import mtk.eon.net.Modulation;

public class ModulationDistancesFileFormat extends FileFormat<LegacyLoader> {

	@Override
	public String getExtension() {
		return "dist";
	}

	@Override
	public boolean loadWithData(LegacyLoader loader) {
		LightScanner scanner = getStream();
		
		while (!scanner.isEOF()) {
			int volume = (int) Math.ceil(scanner.nextInt() / 10.0) - 1;
			if (volume >= 40) throw new RuntimeException("Volume cannot be larger than 400!");
			for (Modulation modulation : Modulation.values())
				loader.getNetwork().setModulationDistance(modulation, volume, scanner.nextInt());
		}
		
		scanner.close();
		return true;
	}
}
