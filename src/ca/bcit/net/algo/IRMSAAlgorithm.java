package ca.bcit.net.algo;

import ca.bcit.net.Network;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

public interface IRMSAAlgorithm {
    String getKey();
    String getName();
    String getDocumentationURL();

    DemandAllocationResult allocateDemand(Demand demand, Network network) throws InstantiationException, ClassNotFoundException, IllegalAccessException;
}
