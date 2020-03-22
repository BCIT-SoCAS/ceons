package ca.bcit.net.demand.generator;

import ca.bcit.io.YamlSerializable;
import ca.bcit.net.demand.AnycastDemand;
import ca.bcit.net.demand.Demand;
import ca.bcit.net.demand.DemandStream;
import ca.bcit.utils.random.MappedRandomVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TrafficGenerator implements DemandStream<Demand>, YamlSerializable {

    private int generatedDemandsCount;
    private final String name;
    private final MappedRandomVariable<DemandGenerator<?>> generators;
    private DemandGenerator<?> lastAnycast;

    public TrafficGenerator(String name, MappedRandomVariable<DemandGenerator<?>> generators) {
        this.name = name;
        this.generators = generators;
    }

    public String getName() {
        return name;
    }

    public MappedRandomVariable<DemandGenerator<?>> getGenerators() {
        return generators;
    }

    public void setReplicaPreservation(boolean replicaPreservation) {
        for (DemandGenerator<?> generator : generators.values())
            if (generator instanceof AnycastDemandGenerator)
                ((AnycastDemandGenerator) generator).setReplicaPreservation(replicaPreservation);
    }

    public void setSeed(long seed) {
        Random seedGenerator = new Random(seed);
        generators.setSeed(seedGenerator.nextLong());
        for (DemandGenerator<?> generator : generators.values())
            generator.setSeed(seedGenerator.nextLong());
        generatedDemandsCount = 0;
    }

    public void setErlang(int erlang) {
        for (DemandGenerator<?> generator : generators.values())
            generator.setErlang(erlang);
    }

    @Override
    public Demand next() {
        Demand demand;
        if (lastAnycast == null) {
            DemandGenerator<?> generator = generators.next();
            demand = generator.next();
            if (demand instanceof AnycastDemand)
                lastAnycast = generator;
            generatedDemandsCount++;
        }
        else {
            demand = lastAnycast.next();
            lastAnycast = null;
        }
        return demand;
    }

    @Override
    public int getGeneratedDemandsCount() {
        return generatedDemandsCount;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public TrafficGenerator(Map map) {
        name = (String) map.get("name");
        generators = (MappedRandomVariable<DemandGenerator<?>>) map.get("generators");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("generators", generators);

        return map;
    }

    @Override
    public String toString() {
        return name;
    }
}