package mtk.eon.io.legacy;

import mtk.eon.io.FileFormat;
import mtk.eon.io.LightScanner;

public class RegeneratorsFileFormat extends FileFormat<LegacyLoader> {

	@Override
	public String getExtension() {
		return "regs";
	}

	@Override
	public boolean loadWithData(LegacyLoader loader) {
		LightScanner scanner = getStream();
		
		while (!scanner.isEOF()) loader.setRegeneratorsCount(scanner.nextInt(), scanner.nextInt());
		
		scanner.close();
		return true;
	}
}
