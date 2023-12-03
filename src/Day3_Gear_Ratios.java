import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Day3_Gear_Ratios {
    private final static List<Character> symbols = Arrays.asList('@','#','$','%','&','*','-','+','=','/');

    public static void main(String[] args) {
        File file = new File("./inputs/day3/day3.txt");
        List<String> grid = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                grid.add(line);
            }

            int part1 = part1(grid);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(grid);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given the indices of the beginning of a number, checks the surrounding grid cells of that number
    // (cells that are exactly one away, including diagonals) for a symbol. If a symbol is found, returns true.
    // Otherwise, returns false.
    private static boolean isSymbolNearby(List<String> grid, int i, int j, int numLength) {
        // Check the row above.
        if (i > 0) {
            String rowAbove = grid.get(i-1);
            for (int col = j; col < j + numLength; col++) {
                char c = rowAbove.charAt(col);
                if (symbols.contains(c)) {
                    return true;
                }
            }
        }

        // Check the row below.
        if (i < grid.size() - 1) {
            String rowBelow = grid.get(i+1);
            for (int col = j; col < j + numLength; col++) {
                char c = rowBelow.charAt(col);
                if (symbols.contains(c)) {
                    return true;
                }
            }
        }

        String row = grid.get(i);

        // Check the cell to the left.
        if (j > 0) {
            char c = row.charAt(j-1);
            if (symbols.contains(c)) {
                return true;
            }
        }

        // Check the cell to the right.
        if (j+numLength-1 < row.length() - 1) {
            char c = row.charAt(j+numLength);
            if (symbols.contains(c)) {
                return true;
            }
        }

        if (i > 0) {
            String rowAbove = grid.get(i-1);

            if (j > 0) {
                // Check the cell at the top left.
                char c = rowAbove.charAt(j-1);
                if (symbols.contains(c)) {
                    return true;
                }
            }

            if (j+numLength-1 < row.length() - 1) {
                // Check the cell at the top right.
                char c = rowAbove.charAt(j+numLength);
                if (symbols.contains(c)) {
                    return true;
                }
            }
        }

        if (i < grid.size() - 1) {
            String rowBelow = grid.get(i+1);

            if (j > 0) {
                // Check the cell at the bottom left.
                char c = rowBelow.charAt(j-1);
                if (symbols.contains(c)) {
                    return true;
                }
            }

            if (j+numLength-1 < row.length() - 1) {
                // Check the cell at the bottom right.
                char c = rowBelow.charAt(j+numLength);
                if (symbols.contains(c)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Part 1:Iterate through the grid and build up numbers as we visit them. When a number
    // is finished building (when we see a non-digit character, or we reach the right edge), check the
    // surrounding grid cells of that number to see if there is a symbol nearby. If so, that number is a part number
    // and accumulate it into the total sum. Otherwise, move on.
    private static int part1(List<String> grid) {
        int sum = 0;
        for (int i = 0; i < grid.size(); i++) {
            StringBuilder sb = new StringBuilder();
            String row = grid.get(i);
            int startColumnOfNumber = 0;

            for (int j = 0; j < grid.get(0).length(); j++) {
                char c = row.charAt(j);
                // If we found the first digit of a number
                if (Character.isDigit(c)) {
                    if (sb.length() == 0) {
                        startColumnOfNumber = j;
                    }

                    sb.append(c);
                } else if (!Character.isDigit(c) && sb.length() > 0) {
                    // If we see a non-digit and we have a number we've been building up,
                    // check to see if there's a symbol nearby.
                    if (isSymbolNearby(grid, i, startColumnOfNumber, sb.length())) {
                        int partNum = Integer.parseInt(sb.toString());
                        sum += partNum;
                    }
                    sb.setLength(0); // reset the string builder
                }
            }

            // We could reach the end of the row with the last digit in the rightmost column,
            // so we still have to process a number if we have it.
            if (sb.length() > 0) {
                if (isSymbolNearby(grid, i, startColumnOfNumber, sb.length())) {
                    int partNum = Integer.parseInt(sb.toString());
                    sum += partNum;
                }
                sb.setLength(0); // reset the string builder
            }
        }

        return sum;
    }

    // Given the indices of the beginning of a number, checks the surrounding grid cells of that number
    // (cells that are exactly one away, including diagonals) for a gear.
    // If a gear is found, return the coordinates of that gear.
    private static Point getNearbyGearCoordinates(List<String> grid, int i, int j, int numLength) {
        // Check the row above.
        if (i > 0) {
            String rowAbove = grid.get(i-1);
            for (int col = j; col < j + numLength; col++) {
                char c = rowAbove.charAt(col);
                if (c == '*') {
                    return new Point(i-1, col);
                }
            }
        }

        // Check the row below.
        if (i < grid.size() - 1) {
            String rowBelow = grid.get(i+1);
            for (int col = j; col < j + numLength; col++) {
                char c = rowBelow.charAt(col);
                if (c == '*') {
                    return new Point(i+1, col);
                }
            }
        }

        String row = grid.get(i);

        // Check the cell to the left.
        if (j > 0) {
            char c = row.charAt(j-1);
            if (c == '*') {
                return new Point(i, j-1);
            }
        }

        // Check the cell to the right.
        if (j+numLength-1 < row.length() - 1) {
            char c = row.charAt(j+numLength);
            if (c == '*') {
                return new Point(i, j+numLength);
            }
        }

        if (i > 0) {
            String rowAbove = grid.get(i-1);

            if (j > 0) {
                // Check the cell at the top left.
                char c = rowAbove.charAt(j-1);
                if (c == '*') {
                    return new Point(i-1, j-1);
                }
            }

            if (j+numLength-1 < row.length() - 1) {
                // Check the cell at the top right.
                char c = rowAbove.charAt(j+numLength);
                if (c == '*') {
                    return new Point(i-1, j+numLength);
                }
            }
        }

        if (i < grid.size() - 1) {
            String rowBelow = grid.get(i+1);

            if (j > 0) {
                // Check the cell at the bottom left.
                char c = rowBelow.charAt(j-1);
                if (c == '*') {
                    return new Point(i+1, j-1);
                }
            }

            if (j+numLength-1 < row.length() - 1) {
                // Check the cell at the bottom right.
                char c = rowBelow.charAt(j+numLength);
                if (c == '*') {
                    return new Point(i+1, j+numLength);
                }
            }
        }

        return null;
    }

    // Part 2: Iterate through the grid and build up numbers as we visit them. When a number
    // is finished building (when we see a non-digit character, or we reach the right edge), check the
    // surrounding grid cells of that number to see if there is a gear symbol nearby.
    // If so, determine the gear's coordinate and store the part number that is adjacent to that gear in a dictionary.
    // After the entire grid has been looked at, find the gears in which there is exactly two part numbers it is
    // adjacent to and multiply them together and sum it all up.
    private static int part2(List<String> grid) {
        // First, run through the grid and find all the gear symbols ('*').
        // Create a dictionary mapping the indices of each gear symbol
        // to the list of numbers that it is adjacent to numbers that it is adjacent to (incl. diagonally adjacent).
        Map<Point, List<Integer>> gears = new HashMap<>();
        for (int i = 0; i < grid.size(); i++) {
            String row = grid.get(i);
            for (int j = 0; j < grid.get(i).length(); j++) {
                if (row.charAt(j) == '*') {
                    gears.put(new Point(i, j), new ArrayList<>());
                }
            }
        }

        int sum = 0;
        for (int i = 0; i < grid.size(); i++) {
            StringBuilder sb = new StringBuilder();
            String row = grid.get(i);
            int startColumnOfNumber = 0;

            for (int j = 0; j < grid.get(0).length(); j++) {
                char c = row.charAt(j);
                // If we found the first digit of a number
                if (Character.isDigit(c)) {
                    if (sb.length() == 0) {
                        startColumnOfNumber = j;
                    }

                    sb.append(c);
                } else if (!Character.isDigit(c) && sb.length() > 0) {
                    // If we see a non-digit and we have a number we've been building up,
                    // check to see if there's a gear symbol nearby.
                    Point nearbyGear = getNearbyGearCoordinates(grid, i, startColumnOfNumber, sb.length());
                    //System.out.println(nearbyGear);
                    if (nearbyGear != null) {
                        int partNum = Integer.parseInt(sb.toString());
                        gears.get(nearbyGear).add(partNum);
                    }
                    sb.setLength(0); // reset the string builder
                }
            }

            // We could reach the end of the row with the last digit in the rightmost column,
            // so we still have to process a number if we have it.
            if (sb.length() > 0) {
                Point nearbyGear = getNearbyGearCoordinates(grid, i, startColumnOfNumber, sb.length());
                if (nearbyGear != null) {
                    int partNum = Integer.parseInt(sb.toString());
                    gears.get(nearbyGear).add(partNum);
                }
                sb.setLength(0); // reset the string builder
            }
        }

        // Run through the entries in the gears dictionary and find the gears that have exactly two numbers
        // Multiply those two numbers and sum them all up.
        for (Point p : gears.keySet()) {
            List<Integer> partNums = gears.get(p);
            if (partNums.size() == 2) {
                sum += partNums.get(0) * partNums.get(1);
            }
        }

        return sum;
    }
}