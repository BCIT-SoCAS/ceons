package ca.bcit.net.algo;

import ca.bcit.net.*;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;
import ca.bcit.net.spectrum.NoSpectrumAvailableException;

import java.util.List;

public class SPF extends BaseRMSAAlgorithm implements IRMSAAlgorithm {
    public String getKey(){
        return "SPF";
    };

    public String getName(){
        return "SPF";
    };

    public String getDocumentationURL(){
        return "https://www.researchgate.net/publication/277329671_Adaptive_Modulation_and_Regenerator-Aware_Dynamic_Routing_Algorithm_in_Elastic_Optical_Networks";
    };

    protected void applyMetricsToCandidatePaths(Network network, int volume, List<PartedPath> candidatePaths) {
        pathLoop: for (PartedPath path : candidatePaths) {
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
    }

    protected void filterCandidatePaths(List<PartedPath> candidatePaths) {
        for (int i = 0; i < candidatePaths.size(); i++)
            for (PathPart spec: candidatePaths.get(i).getParts())
                if (spec.getOccupiedSlicesPercentage() > 80.0) {
                    candidatePaths.remove(i);
                    i--;
                }
    }
}
