import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Day9_Mirage_Maintenance {
    public static void main(String[] args) {
        File file = new File("./inputs/day9/day9.txt");
        List<List<Integer>> inputs = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                List<Integer> starting = new ArrayList<>();
                String[] tokens = line.split(" ");
                for (String item : tokens ){
                    starting.add(Integer.parseInt(item));
                }
                inputs.add(starting);
            }

            int part1 = part1(inputs);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(inputs);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns true if all numbers in the input are 0s. Returns false otherwise.
    private static boolean allZeros(List<Integer> input) {
        for (Integer i : input) {
            if (i != 0) return false;
        }
        return true;
    }

    // Part 1: Simulate the repeated iteration of finding differences between numbers in the lists and find
    // the next number in each base sequence. Repeat this for all the inputs and return the total sum.
    private static int part1(List<List<Integer>> inputs) {
        int nextValueSums = 0; // This will store the sums of all the next values in the inputs.

        for (List<Integer> input : inputs) {
            // Create a working area for each input in which we will add iterations of the differences.
            List<List<Integer>> workingArea = new ArrayList<>();
            workingArea.add(input); // Start with the base list.

            // Keep running until we get to a list that has all zeros.
            while (!allZeros(workingArea.get(workingArea.size() - 1))) {
                // Grab the last item in the last and create a new list
                // that is the differences between all the numbers.
                List<Integer> current = workingArea.get(workingArea.size() - 1);
                List<Integer> difference = new ArrayList<>();
                for (int i = 1; i < current.size(); i++) {
                    difference.add(current.get(i) - current.get(i-1));
                }

                // Add that differences list to our working area and repeat.
                workingArea.add(difference);
            }

            // Finally, sum up the last numbers in each iteration and that is the next value for our input.
            for (int i = workingArea.size() -1; i >= 0; i--) {
                List<Integer> current = workingArea.get(i);
                nextValueSums += current.get(current.size() - 1);
            }
        }

        return nextValueSums;
    }

    // Part 2: Simulate the repeated iteration of finding differences between numbers in the lists and find
    // the previous number in each base sequence. Repeat this for all the inputs and return the total sum.
    private static int part2(List<List<Integer>> inputs) {
        int previousValueSums = 0; // This will store the sums of all the previous values in the inputs.

        for (List<Integer> input : inputs) {
            // Create a working area for each input in which we will add iterations of the differences.
            List<List<Integer>> workingArea = new ArrayList<>();
            workingArea.add(input); // Start with the base list.

            // Keep running until we get to a list that has all zeros.
            while (!allZeros(workingArea.get(workingArea.size() - 1))) {
                // Grab the last item in the last and create a new list
                // that is the differences between all the numbers.
                List<Integer> current = workingArea.get(workingArea.size() - 1);
                List<Integer> difference = new ArrayList<>();
                for (int i = 1; i < current.size(); i++) {
                    difference.add(current.get(i) - current.get(i-1));
                }

                // Add that differences list to our working area and repeat.
                workingArea.add(difference);
            }

            int previousValue = 0;
            for (int i = workingArea.size() - 2; i >= 0; i--) {
                List<Integer> current = workingArea.get(i);
                previousValue = (current.get(0) - previousValue);
            }

            previousValueSums += previousValue;
        }

        return previousValueSums;
    }
}