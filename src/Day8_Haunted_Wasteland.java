import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Day8_Haunted_Wasteland {
    public static void main(String[] args) {
        File file = new File("./inputs/day8/day8.txt");
        String directions = "";
        Map<String, List<String>> connections = new HashMap<>();

        try {
            Scanner sc = new Scanner(file);

            boolean newlineSeen = false;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.equals("")) { newlineSeen = true; continue; }

                if (!newlineSeen) {
                    directions = line;
                    continue;
                }

                String[] tokens = line.split(" = ");
                List<String> destinations = new ArrayList<>();
                destinations.add(tokens[1].substring(1, 4));
                destinations.add(tokens[1].substring(6, 9));
                connections.put(tokens[0], destinations);
            }

            int part1 = part1(connections, directions);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(connections, directions);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1: Starting with node AAA, traverse the network, going left or right depending on
    // which step we're on. Ensure that we loop around the directions string when we get to the end of it.
    // Exit when we arrive at ZZZ and return how many iterations it has been.
    private static int part1(Map<String, List<String>> connections, String directions) {
        int i = 0;
        String currentNode = "AAA";
        while (!currentNode.equals("ZZZ")) {
            char direction = directions.charAt(i % directions.length());

            List<String> children = connections.get(currentNode);
            if (direction == 'L')
                currentNode = children.get(0);
            else if (direction == 'R')
                currentNode = children.get(1);

            i++;
        }

        return i;
    }

    // Part 2: Each ghost has a separate starting point and a unique ending point,
    // and it never visits other ending points. All ghosts loop back around to their starting point,
    // and then to their ending point, and then back around to their starting point, until infinity.
    // By analyzing the input, we see that every last location leads to the second location that ghost ever visited.
    // Every ghost is on their own loop, they go round, and round,
    // and eventually all land on a location that ends in a Z.
    // This means that, for every ghost we need to figure out how long it takes until they reach their ending location.
    // Then find the least common multiple (LCM) of that time which is the first time that all ghosts
    // simultaneously reach their ending position.
    private static long part2(Map<String, List<String>> connections, String directions) {
        List<String> startingNodes = new ArrayList<>();

        // Start by finding all the nodes that end in "A"
        for (String n : connections.keySet()) {
            if (n.endsWith("A")) {
                startingNodes.add(n);
            }
        }

        List<Integer> numSteps = new ArrayList<>();
        String currentNode;

        // Calculate the lengths of each cycle.
        for (String start : startingNodes) {
            int i = 0;

            currentNode = start;
            while (!currentNode.endsWith("Z")) {
                char direction = directions.charAt(i % directions.length());

                List<String> children = connections.get(currentNode);
                if (direction == 'L')
                    currentNode = children.get(0);
                else if (direction == 'R')
                    currentNode = children.get(1);

                i++;
            }

            numSteps.add(i);
        }

        // Find the least common multiple of the cycle lengths. This will be the time that all ghosts
        // simultaneously land on a node that ends with Z.
        return lcm(numSteps);
    }

    // Find the least common multiple (LCM) of a and b.
    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    // Find the least common multiple (LCM) of a list of inputs.
    private static long lcm(List<Integer> input) {
        long result = input.get(0);
        for(int i = 1; i < input.size(); i++) {
            result = lcm(result, input.get(i));
        }
        return result;
    }

    // Find the greatest common divisor (GCD) between a and b.
    // This implements the Euclidean method of repeated modulo.
    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }
}