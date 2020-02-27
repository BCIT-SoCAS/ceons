package ca.bcit.net.modulation;

public class BPSK implements IModulation{
    @Override
    public String getName() {
        return "BPSK";
    }

    @Override
    public String getKey() {
        return "BPSK";
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps() {
        return new int[]{4083, 3581, 3287, 3078, 2917, 2784, 2673, 2576, 2491, 2414, 2345, 2282, 2224, 2170, 2120, 2074, 2030, 1988, 1949, 1912, 1877, 1843, 1811, 1780, 1750, 1722, 1694, 1668, 1643, 1618, 1594, 1571, 1549, 1527, 1506, 1486, 1466, 1447, 1428, 1410};
    }

    @Override
    public int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps() {
        return new int[]{4, 4, 6, 6, 6, 8, 8, 10, 10, 10, 12, 12, 14, 14, 14, 16, 16, 18, 18, 18, 20, 20, 22, 22, 22, 24, 24, 26, 26, 26, 28, 28, 30, 30, 30, 32, 32, 34, 34, 34};
    }
}
