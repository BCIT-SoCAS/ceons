package ca.bcit.net.algo;

import ca.bcit.net.Network;
import ca.bcit.net.PartedPath;

import java.util.List;

public class TemporaryDemandResult {
    Double score;
    Network network;
    List<PartedPath> paths;

    TemporaryDemandResult(Double score, Network network, List<PartedPath> paths) {
        this.score = score;
        this.network = network;
        this.paths = paths;
    }
}
