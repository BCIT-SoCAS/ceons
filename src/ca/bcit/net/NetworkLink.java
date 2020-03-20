package ca.bcit.net;

import ca.bcit.Settings;
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
		for (int i = 0; i < Settings.numberOfCores; i++)
			cores.add(new Core(i));
	}

	public ArrayList<Core> getCores() {
		return cores;
	}
}