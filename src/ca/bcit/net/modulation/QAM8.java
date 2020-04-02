package ca.bcit.net.modulation;

public class QAM8 implements IModulation{
    @Override
    public String getName() {
        return "QAM8";
    }

    @Override
    public String getKey() {
        return "QAM8";
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps() {
        return new int[]{2774, 2439, 2243, 2104, 1996, 1907, 1833, 1768, 1711, 1660, 1614, 1572, 1533, 1497, 1464, 1433, 1403, 1376, 1350, 1325, 1301, 1279, 1257, 1237, 1217, 1198, 1180, 1162, 1145, 1129, 1113, 1097, 1083, 1068, 1054, 1040, 1027, 1014, 1002, 989};
    }

    @Override
    public int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps() {
        return new int[]{4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14};
    }
}
