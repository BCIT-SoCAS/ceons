package mtk.eon.io.project;

import java.io.File;
import java.io.IOException;

import javafx.stage.FileChooser.ExtensionFilter;
import mtk.eon.io.FileFormat;
import mtk.eon.io.Logger;
import mtk.eon.io.YamlConfiguration;
import mtk.eon.io.legacy.CostFileFormat;
import mtk.eon.io.legacy.EnergyFileFormat;
import mtk.eon.io.legacy.LegacyLoader;
import mtk.eon.io.legacy.ModulationDistancesFileFormat;
import mtk.eon.io.legacy.NetworkFileFormat;
import mtk.eon.io.legacy.PathFileFormat;
import mtk.eon.io.legacy.RegeneratorsFileFormat;
import mtk.eon.io.legacy.ReplicasFileFormat;
import mtk.eon.io.legacy.SlicesConsumptionFileFormat;

public class LegacyProjectFileFormat extends ProjectFileFormat<Void, Void> {

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("Legacy project descriptor file", "*.lgc");
	}

	private File getProjectFile(File descriptorFile, YamlConfiguration descriptor, String pathName) {
		return new File(descriptorFile.getParentFile(), descriptor.get(String.class, pathName));
	}
	
	@Override
	public Project load(File file, Void parameter) throws IOException {
		YamlConfiguration descriptor = new YamlConfiguration(file);
		LegacyLoader loader = new LegacyLoader();

		FileFormat.constructor(NetworkFileFormat.class, getProjectFile(file, descriptor, "net")).loadWithData(loader);
		Logger.debug("Topology loaded...");
		FileFormat.constructor(PathFileFormat.class, getProjectFile(file, descriptor, "pat")).loadWithData(loader);
		Logger.debug("Paths loaded...");
		FileFormat.constructor(ModulationDistancesFileFormat.class, getProjectFile(file, descriptor, "dist")).loadWithData(loader);
		Logger.debug("Modulation distances loaded...");
		FileFormat.constructor(RegeneratorsFileFormat.class, getProjectFile(file, descriptor, "regs")).loadWithData(loader);
		Logger.debug("Regenerators loaded...");
		FileFormat.constructor(SlicesConsumptionFileFormat.class, getProjectFile(file, descriptor, "spec")).loadWithData(loader);
		Logger.debug("Slices consumption loaded...");
		FileFormat.constructor(EnergyFileFormat.class, getProjectFile(file, descriptor, "ener")).loadWithData(loader);
		Logger.debug("Energy consumption loaded...");
		FileFormat.constructor(CostFileFormat.class, getProjectFile(file, descriptor, "cost")).loadWithData(loader);
		Logger.debug("Costs loaded...");
		FileFormat.constructor(ReplicasFileFormat.class, getProjectFile(file, descriptor, "rep")).loadWithData(loader);
		Logger.debug("Replicas loaded...");
		
		return new LegacyProject(file, loader.getNetwork(), getProjectFile(file, descriptor, "dems"));
	}

	@Override
	public void save(File file, Project data, Void parameter) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasLoadParameter() {
		return false;
	}

	@Override
	public boolean hasSaveParameter() {
		return false;
	}

}
