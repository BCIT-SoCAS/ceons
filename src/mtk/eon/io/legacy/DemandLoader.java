package mtk.eon.io.legacy;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;

import mtk.eon.io.FileFormat;
import mtk.eon.io.InvalidExtensionException;
import mtk.eon.net.Modulation;
import mtk.eon.net.Network;

public class DemandLoader {

	private Network network;
	private ArrayList<DemandFileFormat> demandFiles = new ArrayList<DemandFileFormat>();
	double totalVolume;
	double spectrumBlockedVolume;
	double regeneratorsBlockedVolume;
	double modulationsUsage[];
	
	private static class DemandFileFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			return file.getAbsolutePath().endsWith(DemandFileFormat.EXTENSION);
		}
	}
	
	private static final DemandFileFilter DEMAND_FILE_FILTER = new DemandFileFilter();
	
	public DemandLoader(String demandDirectoryPath, Network network) throws NoSuchFileException, NotDirectoryException {
		File demandDirectory = new File(demandDirectoryPath);
		if (!demandDirectory.exists()) throw new NoSuchFileException(demandDirectoryPath, null, "Demand directory could not have been found!");
		if (!demandDirectory.isDirectory()) throw new NotDirectoryException("Given demand directory path: \"" + demandDirectoryPath + "\" does not lead to a directory!");
		
		try {
			for (File demandFile : demandDirectory.listFiles(DEMAND_FILE_FILTER)) demandFiles.add(FileFormat.constructor(DemandFileFormat.class, demandFile));
		} catch (InvalidExtensionException e) { throw new RuntimeException("If you see this message it means that God left this place a long time ago..."); }
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public boolean loadAndAllocateDemands() {
		ArrayList<String> results = new ArrayList<String>();
		String columns = "ERLANG;TOTAL VOLUME;BPR;BPS";
		for (Modulation modulation : Modulation.values()) columns += ";" + modulation.name();
		results.add(columns);
		for (DemandFileFormat fileFormat : demandFiles) {
			totalVolume = 0;
			spectrumBlockedVolume = 0;
			regeneratorsBlockedVolume = 0;
			modulationsUsage = new double[6];
			if (!fileFormat.loadWithData(this)) return false;
			int erlang = ((fileFormat.getFile().getName().charAt(0) - '0') + 3) * 100;
			String row = String.format("%d;%0.5f;%0.5f;%0.5f", erlang, totalVolume, regeneratorsBlockedVolume / totalVolume, spectrumBlockedVolume / totalVolume);
			for (int i = 0; i < 6; i++)
				row += String.format(";%0.5f" + modulationsUsage[i]);
			results.add(row);
			network.waitForDemandsDeath();
		}
		
		try {
			RandomAccessFile raf = new RandomAccessFile(new File("net/results.csv"), "rws");
			for (String line : results)
				raf.writeBytes(line + "\n");
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
