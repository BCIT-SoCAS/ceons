package ca.bcit.net;

import ca.bcit.graph.positioned.FixedLengthLink;

import java.util.ArrayList;
import java.util.Map;

public class NetworkLink extends FixedLengthLink<NetworkLink> {
	private ArrayList<Core> cores = new ArrayList<>();

	public NetworkLink(int length) {
		super(length);

		initializeCores();
	}

	@SuppressWarnings("rawtypes")
	public NetworkLink(Map map) {
		super(map);

		initializeCores();
	}

	private void initializeCores() {
		for (int i = 0; i < 1; i++)
			cores.add(new Core());
	}

	public ArrayList<Core> getCores() {
		return cores;
	}

	public void setCores(ArrayList<Core> cores) {
		this.cores = cores;
	}
}