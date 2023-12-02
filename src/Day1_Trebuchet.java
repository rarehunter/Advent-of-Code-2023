import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Day1_Trebuchet {
    public static void main(String[] args) {
        File file = new File("./inputs/day1/day1.txt");

        try {
            Scanner sc = new Scanner(file);
            List<String> calibrationDocument = new ArrayList<>();

            while (sc.hasNextLine()) {
                calibrationDocument.add(sc.nextLine());
            }

            int part1 = part1(calibrationDocument);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(calibrationDocument);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns the integer represented by concatenating the leftmost and rightmost number in the string.
    private static int concatenateLeftmostAndRightmostNumbers(String input) {
        int leftNum = 0, rightNum = 0;
        // Scan from left to right for the leftmost number.
        for (int i = 0; i < input.length(); i++) {
            char candidate = input.charAt(i);
            if (Character.isDigit(candidate)) {
                leftNum = candidate - '0'; // Convert a single character to the integer it represents.
                break;
            }
        }
        // Scan from right to left for the rightmost number.
        for (int j = input.length() - 1; j >= 0; j--) {
            char candidate = input.charAt(j);
            if (Character.isDigit(candidate)) {
                rightNum = candidate - '0'; // Convert a single character to the integer it represents.
                break;
            }
        }

        return leftNum * 10 + rightNum;
    }

    // Part 1: Scan from left to right to find the leftmost digit.
    // Scan from right to left to find the rightmost digit.
    // Then, create the full integer and sum them all up.
    private static int part1(List<String> calibrationDocument) {
        int sum = 0;
        for (String calibrationInput : calibrationDocument) {
            sum += concatenateLeftmostAndRightmostNumbers(calibrationInput);
        }
        return sum;
    }

    // Check if the given input starts with the numbers one through nine spelled out.
    // If so, return the integer for that number. Otherwise, return -1.
    // This helps us find the leftmost spelled-out number.
    private static int substringToNumber(String input, int index) {
        if (input.startsWith("one", index)) {
            return 1;
        }
        if (input.startsWith("two", index)) {
            return 2;
        }
        if (input.startsWith("three", index)) {
            return 3;
        }
        if (input.startsWith("four", index)) {
            return 4;
        }
        if (input.startsWith("five", index)) {
            return 5;
        }
        if (input.startsWith("six", index)) {
            return 6;
        }
        if (input.startsWith("seven", index)) {
            return 7;
        }
        if (input.startsWith("eight", index)) {
            return 8;
        }
        if (input.startsWith("nine", index)) {
            return 9;
        }

        return -1;
    }

    // Check if the given input ends with the numbers one through nine spelled out.
    // If so, return the integer for that number. Otherwise, return -1.
    // This helps us find the rightmost spelled-out number.
    private static int substringToNumberReversed(String input, int index) {
        // Only search from the beginning of the string to where the index has us end.
        String shortened = input.substring(0, index + 1);
        if (shortened.endsWith("one")) {
            return 1;
        }
        if (shortened.endsWith("two")) {
            return 2;
        }
        if (shortened.endsWith("three")) {
            return 3;
        }
        if (shortened.endsWith("four")) {
            return 4;
        }
        if (shortened.endsWith("five")) {
            return 5;
        }
        if (shortened.endsWith("six")) {
            return 6;
        }
        if (shortened.endsWith("seven")) {
            return 7;
        }
        if (shortened.endsWith("eight")) {
            return 8;
        }
        if (shortened.endsWith("nine")) {
            return 9;
        }

        return -1;
    }

    // Part 2: Scan from left to right to find the leftmost digit or spelled-out numbers.
    // Scan from right to left to find the rightmost digit or spelled-out numbers.
    // Then, create the full integer and sum them all up.
    private static int part2(List<String> calibrationDocument) {
        int sum = 0;

        for (String input : calibrationDocument) {
            int leftNum = 0, rightNum = 0;

            // Scan from left to right for the leftmost number, including spelled-out numbers.
            for (int i = 0; i < input.length(); i++) {
                char candidate = input.charAt(i);
                if (Character.isDigit(candidate)) {
                    leftNum = candidate - '0'; // Convert a single character to the integer it represents.
                    break;
                }

                // Starting from index i, does this substring start with any of
                // the numbers "one" through "nine" spelled out?
                int num = substringToNumber(input, i);
                if (num > 0) {
                    leftNum = num;
                    break;
                }
            }

            // Scan from right to left for the rightmost number.
            for (int j = input.length() - 1; j >= 0; j--) {
                char candidate = input.charAt(j);
                if (Character.isDigit(candidate)) {
                    rightNum = candidate - '0'; // Convert a single character to the integer it represents.
                    break;
                }

                // Does the substring from 0 to j, end with any of
                // the numbers "one" through "nine" spelled out?
                int num = substringToNumberReversed(input, j);
                if (num > 0) {
                    rightNum = num;
                    break;
                }
            }

            sum += (leftNum * 10 + rightNum);
        }

        return sum;
    }
}