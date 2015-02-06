package mtk.eon;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

import mtk.eon.io.FileFormat;
import mtk.eon.io.InvalidExtensionException;
import mtk.eon.io.LightScanner;
import mtk.eon.io.Logger;
import mtk.eon.io.legacy.CostFileFormat;
import mtk.eon.io.legacy.DemandLoader;
import mtk.eon.io.legacy.EnergyFileFormat;
import mtk.eon.io.legacy.LegacyLoader;
import mtk.eon.io.legacy.ModulationDistancesFileFormat;
import mtk.eon.io.legacy.NetworkFileFormat;
import mtk.eon.io.legacy.PathFileFormat;
import mtk.eon.io.legacy.RegeneratorsFileFormat;
import mtk.eon.io.legacy.ReplicasFileFormat;
import mtk.eon.io.legacy.SlicesConsumptionFileFormat;
import mtk.eon.jfx.tasks.SimulationTask;
import mtk.eon.net.Network;

public class Project {

	File projectDirectory;
	File demandFolder;
	Network network;
	
	public Project(File projectInfo) throws FileNotFoundException, InvalidExtensionException {
		projectDirectory = projectInfo.getParentFile();
		LegacyLoader loader = new LegacyLoader();
		LightScanner scanner = new LightScanner(projectInfo);
		try {
			FileFormat.constructor(NetworkFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Topology loaded...");
			FileFormat.constructor(PathFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Paths loaded...");
			FileFormat.constructor(ModulationDistancesFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Modulation distances loaded...");
			FileFormat.constructor(RegeneratorsFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Regenerators loaded...");
			FileFormat.constructor(SlicesConsumptionFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Slices consumption loaded...");
			FileFormat.constructor(EnergyFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Energy consumption loaded...");
			FileFormat.constructor(CostFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Costs loaded...");
			FileFormat.constructor(ReplicasFileFormat.class, new File(projectDirectory, scanner.nextLine())).loadWithData(loader);
			Logger.debug("Replicas loaded...");
			demandFolder = new File(projectDirectory, scanner.nextLine());
		} catch (FileNotFoundException | InvalidExtensionException e) {
			scanner.close();
			throw e;
		}
		scanner.close();
		network = loader.getNetwork();
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public DemandLoader getDefaultDemandLoader(SimulationTask task) throws NotDirectoryException, FileNotFoundException {
		return new DemandLoader(demandFolder, network, task);
	}
}
