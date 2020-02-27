package ca.bcit.net.modulation;

public interface IModulation {
    String getName();
    String getKey();
    int getId();
    int[] getMaximumDistanceSupportedByBitrateWithJumpsOfTenGbps();
    int[] getSlicesConsumptionByBitrateWithJumpsOfTenGbps();
}
