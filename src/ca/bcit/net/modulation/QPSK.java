package ca.bcit.net.modulation;

public class QPSK implements IModulation{
    @Override
    public String getName() {
        return "QPSK";
    }

    @Override
    public String getKey() {
        return "QPSK";
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps() {
        return new int[]{3429, 3010, 2765, 2591, 2456, 2346, 2253, 2172, 2101, 2037, 1980, 1927, 1879, 1834, 1792, 1753, 1717, 1682, 1649, 1618, 1589, 1561, 1534, 1508, 1484, 1460, 1437, 1415, 1394, 1373, 1354, 1334, 1316, 1298, 1280, 1263, 1247, 1231, 1215, 1200};
    }

    @Override
    public int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps() {
        return new int[]{4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 14, 14, 14, 14, 14, 16, 16, 16, 16, 16, 18, 18, 18, 18, 18};
    }
}
