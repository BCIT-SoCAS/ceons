package ca.bcit.net.modulation;

public class QAM16 implements IModulation{
    @Override
    public String getName() {
        return "QAM16";
    }

    @Override
    public String getKey() {
        return "QAM16";
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps() {
        return new int[]{2120, 1868, 1721, 1616, 1535, 1469, 1413, 1364, 1322, 1283, 1249, 1217, 1188, 1161, 1136, 1112, 1090, 1070, 1050, 1031, 1014, 997, 981, 965, 950, 936, 922, 909, 896, 884, 872, 861, 849, 839, 828, 818, 808, 798, 789, 779};
    }

    @Override
    public int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps() {
        return new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
    }
}
