import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

// Represents the time allowed for a race and its record distance.
class Race {
    private long time;
    private long distance;

    public Race() {
        time = 0;
        distance = 0;
    }

    public void setTime(long time) { this.time = time; }
    public void setDistance(long distance) { this.distance = distance; }
    public long getTime() { return this.time; }
    public long getDistance() { return this.distance; }
    public String toString() { return "(" + this.time + "," + this.distance + ")"; }
}

public class Day6_Wait_For_It {
    public static void main(String[] args) {
        File file = new File("./inputs/day6/day6.txt");
        List<Race> races = parsePart1(file);
        int part1 = part1(races);
        System.out.println("Part 1 is: " + part1);

        Race actualRace = parsePart2(file);
        long part2 = part2(actualRace);
        System.out.println("Part 2 is: " + part2);
    }

    // Parses the input file for part 1 (accounting for whitespaces as a separate race)
    private static List<Race> parsePart1(File file) {
        List<Race> races = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);

            String line = sc.nextLine();
            String[] tokens = line.split(":");
            String[] nums = tokens[1].trim().split("\\s+");

            for (String num : nums) {
                Race r = new Race();
                r.setTime(Long.parseLong(num));
                races.add(r);
            }

            String line2 = sc.nextLine();
            String[] tokens2 = line2.split(":");
            String[] distances = tokens2[1].trim().split("\\s+");

            for (int i = 0; i < races.size(); i++) {
                races.get(i).setDistance(Long.parseLong(distances[i]));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return races;
    }

    // Part 1: Iterate through each race and try every possible time to see which ones result in distances
    // that beat the race record. Keep track of the number of ways to beat the record distance in an array
    // and multiply them all together at the end.
    private static int part1(List<Race> races) {
        int[] numWaysToBeatRecord = new int[races.size()];
        for (int r = 0; r < races.size(); r++) {
            Race race = races.get(r);
            long time = race.getTime();
            long distance = race.getDistance();

            for (int i = 1; i < time; i++) {
                if (i * (time - i) > distance) {
                    numWaysToBeatRecord[r]++;
                }
            }
        }

        int product = 1;
        for (Integer numWays : numWaysToBeatRecord) {
            product *= numWays;
        }

        return product;
    }

    // Parses the input file for part 2
    // (whitespaces are removed and the numbers are squashed together to be a single number)
    private static Race parsePart2(File file) {
        Race race = new Race();
        try {
            Scanner sc = new Scanner(file);

            String line = sc.nextLine();
            String[] tokens = line.split(":");
            String[] nums = tokens[1].trim().split("\\s+");

            StringBuilder actualTime = new StringBuilder();
            for (String num : nums) {
                actualTime.append(num);
            }

            String line2 = sc.nextLine();
            String[] tokens2 = line2.split(":");
            String[] distances = tokens2[1].trim().split("\\s+");

            StringBuilder actualDistance = new StringBuilder();
            for (String dist : distances) {
                actualDistance.append(dist);
            }

            race.setTime(Long.parseLong(actualTime.toString()));
            race.setDistance(Long.parseLong(actualDistance.toString()));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return race;
    }

    // Part 2: This is a brute force attempt by taking the single race and running the same algorithm
    // as in part 1 on it. Takes less than a second to complete.
    private static long part2(Race actualRace) {
        long numWaysToBeatRecord = 0L;
        long time = actualRace.getTime();
        long distance = actualRace.getDistance();

        for (int i = 1; i < time; i++) {
            if (i * (time - i) > distance) {
                numWaysToBeatRecord++;
            }
        }

        return numWaysToBeatRecord;
    }
}