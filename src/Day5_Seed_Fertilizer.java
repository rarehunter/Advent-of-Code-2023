import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

record AToBMap(long destinationRangeStart, long sourceRangeStart, long rangeLength) {
    public String toString() {
        return "(" + this.destinationRangeStart + ", " + this.sourceRangeStart + ", " + this.rangeLength + ")";
    }
}

public class Day5_Seed_Fertilizer {
    private static final List<Long> seeds = new ArrayList<>();
    private static final List<AToBMap> seedToSoil = new ArrayList<>();
    private static final List<AToBMap> soilToFertilizer = new ArrayList<>();
    private static final List<AToBMap> fertilizerToWater = new ArrayList<>();
    private static final List<AToBMap> waterToLight = new ArrayList<>();
    private static final List<AToBMap> lightToTemp = new ArrayList<>();
    private static final List<AToBMap> tempToHumidity = new ArrayList<>();
    private static final List<AToBMap> humidityToLocation = new ArrayList<>();

    public static void main(String[] args) {
        File file = new File("./inputs/day5/day5.txt");

        try {
            Scanner sc = new Scanner(file);

            boolean seedToSoilFlag = false;
            boolean soilToFertilizerFlag = false;
            boolean fertilizerToWaterFlag = false;
            boolean waterToLightFlag = false;
            boolean lightToTempFlag = false;
            boolean tempToHumidityFlag = false;
            boolean humidityToLocationFlag = false;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                // Parse the seed numbers.
                if (line.startsWith("seeds:")) {
                    String[] seedTokens = line.split(" ");
                    for (int i = 1; i < seedTokens.length; i++) {
                        seeds.add(Long.parseLong(seedTokens[i]));
                    }
                }

                // Set flags for when to start building each object.
                if (line.startsWith("seed-to-soil map")) { seedToSoilFlag = true; continue; }
                else if (line.startsWith("soil-to-fertilizer")) { soilToFertilizerFlag = true; continue; }
                else if (line.startsWith("fertilizer-to-water")) { fertilizerToWaterFlag = true; continue; }
                else if (line.startsWith("water-to-light")) { waterToLightFlag = true; continue; }
                else if (line.startsWith("light-to-temperature")) { lightToTempFlag = true; continue; }
                else if (line.startsWith("temperature-to-humidity")) { tempToHumidityFlag = true; continue; }
                else if (line.startsWith("humidity-to-location")) { humidityToLocationFlag = true; continue; }

                if (seedToSoilFlag) {
                    if (line.equals("")) {
                        seedToSoilFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    seedToSoil.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }

                if (soilToFertilizerFlag) {
                    if (line.equals("")) {
                        soilToFertilizerFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    soilToFertilizer.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }

                if (fertilizerToWaterFlag) {
                    if (line.equals("")) {
                        fertilizerToWaterFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    fertilizerToWater.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }

                if (waterToLightFlag) {
                    if (line.equals("")) {
                        waterToLightFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    waterToLight.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }

                if (lightToTempFlag) {
                    if (line.equals("")) {
                        lightToTempFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    lightToTemp.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }

                if (tempToHumidityFlag) {
                    if (line.equals("")) {
                        tempToHumidityFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    tempToHumidity.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }

                if (humidityToLocationFlag) {
                    if (line.equals("")) {
                        humidityToLocationFlag = false;
                        continue;
                    }
                    String[] values = line.split(" ");
                    humidityToLocation.add(new AToBMap(
                            Long.parseLong(values[0]),
                            Long.parseLong(values[1]),
                            Long.parseLong(values[2]))
                    );
                }
            }

            long part1 = part1();
            System.out.println("Part 1 is: " + part1);

            long part2 = part2();
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given a value, returns the mapped value using the given map.
    private static long getMappedValue(List<AToBMap> map, long value) {
        for (AToBMap entry : map) {
            if (value >= entry.sourceRangeStart() && value < entry.sourceRangeStart() + entry.rangeLength()) {
                return entry.destinationRangeStart() + (value - entry.sourceRangeStart());
            }
        }

        return value;
    }

    // Part 1: For each seed value, convert it to its soil value, then to its fertilizer value, and so on.
    // Use the mapping logic located in each AToBMap object.
    // Finally, keep a running minimum output value (location) and return it.
    private static long part1() {
        long minLocation = Long.MAX_VALUE;
        for (Long seed : seeds) {
            long soil = getMappedValue(seedToSoil, seed);
            long fertilizer = getMappedValue(soilToFertilizer, soil);
            long water = getMappedValue(fertilizerToWater, fertilizer);
            long light = getMappedValue(waterToLight, water);
            long temp = getMappedValue(lightToTemp, light);
            long humidity = getMappedValue(tempToHumidity, temp);
            long location = getMappedValue(humidityToLocation, humidity);

            minLocation = Math.min(location, minLocation);
        }
        return minLocation;
    }

    // Part 2: Brute force implementation. For each value in the seed ranges,
    // run through the conversion process again (same as part 1). This takes about 5-6 minutes to run.
    private static long part2() {
        long minLocation = Long.MAX_VALUE;
        for (int i = 0; i < seeds.size() - 1; i += 2) {
            for (long seed = seeds.get(i); seed < seeds.get(i) + seeds.get(i+1); seed++) {
                long soil = getMappedValue(seedToSoil, seed);
                long fertilizer = getMappedValue(soilToFertilizer, soil);
                long water = getMappedValue(fertilizerToWater, fertilizer);
                long light = getMappedValue(waterToLight, water);
                long temp = getMappedValue(lightToTemp, light);
                long humidity = getMappedValue(tempToHumidity, temp);
                long location = getMappedValue(humidityToLocation, humidity);
                minLocation = Math.min(location, minLocation);
            }
        }
        return minLocation;
    }
}