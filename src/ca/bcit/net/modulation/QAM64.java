package ca.bcit.net.modulation;

public class QAM64 implements IModulation{
    @Override
    public String getName() {
        return "QAM64";
    }

    @Override
    public String getKey() {
        return "QAM64";
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps() {
        return new int[]{811, 726, 677, 641, 614, 592, 573, 557, 542, 529, 518, 507, 497, 488, 480, 472, 464, 457, 451, 444, 438, 433, 427, 422, 417, 412, 408, 403, 399, 395, 391, 387, 383, 379, 376, 372, 369, 366, 362, 359};
    }

    @Override
    public int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps() {
        return new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
    }
}
