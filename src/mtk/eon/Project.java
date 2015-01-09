package mtk.eon;

import java.io.File;
import java.nio.file.NoSuchFileException;

import mtk.eon.io.FileFormat;
import mtk.eon.io.InvalidExtensionException;
import mtk.eon.io.LightScanner;
import mtk.eon.io.legacy.CostFileFormat;
import mtk.eon.io.legacy.EnergyFileFormat;
import mtk.eon.io.legacy.LegacyLoader;
import mtk.eon.io.legacy.ModulationDistancesFileFormat;
import mtk.eon.io.legacy.NetworkFileFormat;
import mtk.eon.io.legacy.PathFileFormat;
import mtk.eon.io.legacy.RegeneratorsFileFormat;
import mtk.eon.io.legacy.ReplicasFileFormat;
import mtk.eon.io.legacy.SlicesConsumptionFileFormat;
import mtk.eon.net.Network;

public class Project {

	File projectDirectory;
	Network network;
	
	public Project(File projectInfo) throws NoSuchFileException, InvalidExtensionException {
		projectDirectory = projectInfo.getParentFile();
		LegacyLoader loader = new LegacyLoader();
		LightScanner scanner = new LightScanner(projectInfo);
		try {
			FileFormat.constructor(NetworkFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(PathFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(ModulationDistancesFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(RegeneratorsFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(SlicesConsumptionFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(EnergyFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(CostFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			FileFormat.constructor(ReplicasFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
		} catch (NoSuchFileException | InvalidExtensionException e) {
			scanner.close();
			throw e;
		}
		scanner.close();
		network = loader.getNetwork();
	}
}
