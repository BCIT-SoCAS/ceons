package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

import java.util.List;

public class SPF implements IRMSAAlgorithm {
    public String getKey(){
        return "SPF";
    };

    public String getName(){
        return "SPF";
    };

    public String getDocumentationURL(){
        return "https://www.researchgate.net/publication/277329671_Adaptive_Modulation_and_Regenerator-Aware_Dynamic_Routing_Algorithm_in_Elastic_Optical_Networks";
    };

    @Override
    public DemandAllocationResult allocateDemand(Demand demand, Network network) {
        int volume = (int) Math.ceil(demand.getVolume() / 10) - 1;
        List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);

        sortByLength(network, volume, candidatePaths);

        if (candidatePaths.isEmpty())
            return DemandAllocationResult.NO_SPECTRUM;

        boolean workingPathSuccess = false;

        try {
            for (PartedPath path : candidatePaths)
                if (demand.allocate(network, path)) {
                    workingPathSuccess = true;
                    break;
                }
        }
        catch (NetworkException storage) {
            workingPathSuccess = false;
            return DemandAllocationResult.NO_REGENERATORS;
        }

        if (!workingPathSuccess)
            return DemandAllocationResult.NO_SPECTRUM;

        try {
            if (demand.allocateBackup()) {
                volume = (int) Math.ceil(demand.getSqueezedVolume() / 10) - 1;

                if (candidatePaths.isEmpty())
                    return new DemandAllocationResult(demand.getWorkingPath());

                for (PartedPath path : candidatePaths)
                    if (demand.allocate(network, path))
                        return new DemandAllocationResult(demand.getWorkingPath(), demand.getBackupPath());

                return new DemandAllocationResult(demand.getWorkingPath());
            }
        }
        catch (NetworkException e) {
            workingPathSuccess = false;
            return DemandAllocationResult.NO_REGENERATORS;
        }

        return new DemandAllocationResult(demand.getWorkingPath());
    }

    private static List<PartedPath> sortByLength(Network network, int volume, List<PartedPath> candidatePaths) {
        pathLoop:
        for (PartedPath path : candidatePaths) {
            path.setMetric(path.getPath().getLength());

            // choosing modulations for parts
            for (PathPart part : path) {
                for (Modulation modulation : network.getAllowedModulations())
                    if (modulation.modulationDistances[volume] >= part.getLength()) {
                        part.setModulation(modulation, 1);
                        break;
                    }

                if (part.getModulation() == null)
                    continue pathLoop;
            }
        }

        for (int i = 0; i < candidatePaths.size(); i++)
            for (PathPart spec: candidatePaths.get(i).getParts())
                if (spec.getOccupiedSlicesPercentage() > 80.0) {
                    candidatePaths.remove(i);
                    i--;
                }

        candidatePaths.sort(PartedPath::compareTo);

        return candidatePaths;
    }
}
