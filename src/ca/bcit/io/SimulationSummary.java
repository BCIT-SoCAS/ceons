package ca.bcit.io;

public class SimulationSummary {
    private String trafficGeneratorName;
    private int erlangValue;
    private long seedValue;
    private double alphaValue;
    private int demandsCountValue;
    private double totalVolume;
    private double spectrumBlockedVolume;
    private double regeneratorsBlockedVolume;
    private double linkFailureBlockedVolume;
    private double regsPerAllocation;
    private double allocations;
    private double unhandledVolume;
    private double noSpectrumBlockedVolumePercentage;
    private double noRegeneratorsBlockedVolumePercentage;
    private double linkFailureBlockedVolumePercentage;
    private double unhandledVolumePercentage;
    private double totalBlockedVolumePercentage;
    private double averageRegeneratiorsPerAllocation;
    private String algorithm;

    public SimulationSummary(String trafficGeneratorName, int erlangValue, long seedValue, double alphaValue, int demandsCountValue,
                             double totalVolume, double spectrumBlockedVolume, double regeneratorsBlockedVolume, double linkFailureBlockedVolume,
                             double unhandledVolume, double regsPerAllocation, double allocations, String algorithm) {
        this.trafficGeneratorName = trafficGeneratorName;
        this.erlangValue = erlangValue;
        this.seedValue = seedValue;
        this.alphaValue = alphaValue;
        this.demandsCountValue = demandsCountValue;
        this.totalVolume = totalVolume;
        this.spectrumBlockedVolume = spectrumBlockedVolume;
        this.regeneratorsBlockedVolume = regeneratorsBlockedVolume;
        this.linkFailureBlockedVolume = linkFailureBlockedVolume;
        this.regsPerAllocation = regsPerAllocation;
        this.allocations = allocations;
        this.unhandledVolume = unhandledVolume;
        this.algorithm = algorithm;
        noSpectrumBlockedVolumePercentage = spectrumBlockedVolume / totalVolume * 100;
        noRegeneratorsBlockedVolumePercentage = regeneratorsBlockedVolume / totalVolume * 100;
        linkFailureBlockedVolumePercentage = linkFailureBlockedVolume / totalVolume * 100;
        unhandledVolumePercentage = unhandledVolume / totalVolume * 100;
        totalBlockedVolumePercentage = ((spectrumBlockedVolume / totalVolume)
                                        + (regeneratorsBlockedVolume / totalVolume)
                                        + (linkFailureBlockedVolume / totalVolume)
                                        + (unhandledVolume / totalVolume)) * 100;
        averageRegeneratiorsPerAllocation = regsPerAllocation / allocations;
    }
}