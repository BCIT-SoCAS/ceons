package ca.bcit.net.algo;

import ca.bcit.net.Network;
import ca.bcit.net.NetworkException;
import ca.bcit.net.PartedPath;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandAllocationResult;

import java.util.List;

public class SPF extends RMSAAlgorithm {

    @Override
    protected String getName() {
        return "SPF";
    }

    @Override
    public DemandAllocationResult allocateDemand(Demand demand, Network network) {
        int volume = (int) Math.ceil(demand.getVolume() / 10) - 1;
        List<PartedPath> candidatePaths = demand.getCandidatePaths(false, network);

        sortByLength(candidatePaths);

        if (candidatePaths.isEmpty())
            return DemandAllocationResult.NO_SPECTRUM;

        if (candidatePaths.isEmpty())
            return DemandAllocationResult.NO_REGENERATORS;

        boolean workingPathSuccess = false;

        try {
            for (PartedPath path : candidatePaths)
                if (demand.allocate(network, path)) {
                    workingPathSuccess = true;
                    break;
                }

        } catch (NetworkException storage) {
            workingPathSuccess = false;
            return DemandAllocationResult.NO_REGENERATORS;
        }
        if (!workingPathSuccess)
            return DemandAllocationResult.NO_SPECTRUM;
        if (demand.allocateBackup()) {
            volume = (int) Math.ceil(demand.getSqueezedVolume() / 10) - 1;

            if (candidatePaths.isEmpty())
                return new DemandAllocationResult(
                        demand.getWorkingPath());
            for (PartedPath path : candidatePaths)
                if (demand.allocate(network, path))
                    return new DemandAllocationResult(demand.getWorkingPath(), demand.getBackupPath());

            return new DemandAllocationResult(demand.getWorkingPath());
        }

        return new DemandAllocationResult(demand.getWorkingPath());
    }

    private List<PartedPath> sortByLength(List<PartedPath> candidatePaths) {
        pathLoop: for (PartedPath path : candidatePaths) {
            path.setMetric(path.getPath().getLength());
        }
        candidatePaths.sort(PartedPath::compareTo);
        return candidatePaths;
    }
}
