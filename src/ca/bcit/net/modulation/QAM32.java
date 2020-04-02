package ca.bcit.net.modulation;

public class QAM32 implements IModulation{
    @Override
    public String getName() {
        return "QAM32";
    }

    @Override
    public String getKey() {
        return "QAM32";
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps() {
        return new int[]{1466, 1297, 1199, 1129, 1075, 1030, 993, 960, 932, 906, 883, 862, 842, 824, 808, 792, 777, 763, 750, 738, 726, 715, 704, 694, 684, 674, 665, 656, 648, 639, 631, 624, 616, 609, 602, 595, 588, 582, 576, 569};
    }

    @Override
    public int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps() {
        return new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 10, 10, 10};
    }
}
