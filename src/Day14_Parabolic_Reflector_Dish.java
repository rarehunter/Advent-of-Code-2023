import java.io.File;
import java.io.IOException;
import java.util.*;

public class Day14_Parabolic_Reflector_Dish {
    public static void main(String[] args) {
        File file = new File("./inputs/day14/day14.txt");

        try {
            Scanner sc = new Scanner(file);
            Scanner sc2 = new Scanner(file);

            int height = 0;
            int width = 0;

            // We will parse the input in two passes.
            // The first pass will determine the height and width of the grid.
            // The second pass will create the 2d char array and populate it with characters.
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                width = line.length();
                height++;
            }

            char[][] grid = new char[height][width];
            int row = 0;
            while (sc2.hasNextLine()) {
                String line = sc2.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    grid[row][i] = line.charAt(i);
                }
                row++;
            }

            int part1 = part1(grid);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(grid);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void printGrid(char[][] grid) {
        for (char[] chars : grid) {
            for (char aChar : chars) {
                System.out.print(aChar);
            }
            System.out.println();
        }
    }

    // Given a grid and the position of a round rock, simulate it rolling in the northward direction.
    // The rock will keep rolling into it encounters the north edge or another rock (both rounded and cubed).
    private static void rockfallNorth(char[][] grid, int i, int j) {
        int row = i;

        while (true) {
            // If we can keep falling, keep iterating
            if (row > 0 && grid[row-1][j] == '.') {
                row--;
            } else {
                // We've reached the edge of the grid, or hit another rock, so the rock stops.
                break;
            }
        }

        // If we've changed the location of the rock, set its new location
        // and mark its original location as empty.
        if (row != i) {
            grid[i][j] = '.';
            grid[row][j] = 'O';
        }
    }

    // Given a grid and the position of a round rock, simulate it rolling in the southward direction.
    // The rock will keep rolling into it encounters the south edge or another rock (both rounded and cubed).
    private static void rockfallSouth(char[][] grid, int i, int j) {
        int row = i;

        while (true) {
            // If we can keep falling, keep iterating
            if (row < grid.length - 1 && grid[row+1][j] == '.') {
                row++;
            } else {
                // We've reached the edge of the grid, or hit another rock, so the rock stops.
                break;
            }
        }

        // If we've changed the location of the rock, set its new location
        // and mark its original location as empty.
        if (row != i) {
            grid[i][j] = '.';
            grid[row][j] = 'O';
        }
    }

    // Given a grid and the position of a round rock, simulate it rolling in the westward direction.
    // The rock will keep rolling into it encounters the west edge or another rock (both rounded and cubed).
    private static void rockfallWest(char[][] grid, int i, int j) {
        int column = j;

        while (true) {
            // If we can keep falling, keep iterating
            if (column > 0 && grid[i][column-1] == '.') {
                column--;
            } else {
                // We've reached the edge of the grid, or hit another rock, so the rock stops.
                break;
            }
        }

        // If we've changed the location of the rock, set its new location
        // and mark its original location as empty.
        if (column != j) {
            grid[i][j] = '.';
            grid[i][column] = 'O';
        }
    }

    // Given a grid and the position of a round rock, simulate it rolling in the eastward direction.
    // The rock will keep rolling into it encounters the east edge or another rock (both rounded and cubed).
    private static void rockfallEast(char[][] grid, int i, int j) {
        int column = j;

        while (true) {
            // If we can keep falling, keep iterating
            if (column < grid[0].length - 1 && grid[i][column + 1] == '.') {
                column++;
            } else {
                // We've reached the edge of the grid, or hit another rock, so the rock stops.
                break;
            }
        }

        // If we've changed the location of the rock, set its new location
        // and mark its original location as empty.
        if (column != j) {
            grid[i][j] = '.';
            grid[i][column] = 'O';
        }
    }

    // Iterate through each cell in the grid (except the cells in the 0th row).
    // If a round rock is encountered, simulate it rolling in the northward direction.
    private static void tiltNorth(char[][] grid) {
        // The 0th row does not move, so we'll start with row index 1.
        for (int i = 1; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'O') {
                    rockfallNorth(grid, i, j);
                }
            }
        }
    }

    // Iterate backwards through each cell in the grid (except the cells in the bottom-most row).
    // If a round rock is encountered, simulate it rolling in the southward direction.
    private static void tiltSouth(char[][] grid) {
        // The bottom-most row does not move, so we'll start with second row from the bottom.
        for (int i = grid.length - 2; i >= 0; i--) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'O') {
                    rockfallSouth(grid, i, j);
                }
            }
        }
    }

    // Iterate through each cell in the grid column-wise from left to right
    // (except the cells in the left-most column).
    // If a round rock is encountered, simulate it rolling in the westward direction.
    private static void tiltWest(char[][] grid) {
        // The left-most row does not move, so we'll start with second row from the left.
        for (int i = 0; i < grid.length; i++) {
            for (int j = 1; j < grid[0].length; j++) {
                if (grid[i][j] == 'O') {
                    rockfallWest(grid, i, j);
                }
            }
        }
    }

    // Iterate through each cell in the grid column-wise from right to left
    // (except the cells in the right-most column).
    // If a round rock is encountered, simulate it rolling in the eastward direction.
    private static void tiltEast(char[][] grid) {
        // The right-most row does not move, so we'll start with second row from the right.
        for (int i = 0; i < grid.length; i++) {
            for (int j = grid[0].length - 2; j >= 0; j--) {
                if (grid[i][j] == 'O') {
                    rockfallEast(grid, i, j);
                }
            }
        }
    }

    // Returns the total load on the north edge.
    // The total load is the sum of the loads of each round rock (O).
    // The load of each round rock is the distance to the southern edge including the row the rock is on.
    private static int calculateTotalLoadNorth(char[][] grid) {
        int sum = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'O') {
                    sum += (grid.length - i);
                }
            }
        }

        return sum;
    }

    // Part 1: Tilt the grid to the north and simulate the round rocks rolling in that direction.
    // Calculate the total load of the resulting positions of the round rocks.
    private static int part1(char[][] grid) {
        tiltNorth(grid);
        return calculateTotalLoadNorth(grid);
    }

    // Runs one cycle of stone tilts.
    // Each cycle will tilt the stones north, west, south, then east in that order.
    private static void cycleStonesOnce(char[][] grid) {
        tiltNorth(grid);
        tiltWest(grid);
        tiltSouth(grid);
        tiltEast(grid);
    }

    // Flattens the 2d char array into a single string.
    private static String gridToString(char[][] grid) {
        StringBuilder sb = new StringBuilder();

        for (char[] chars : grid) {
            for (int j = 0; j < grid[0].length; j++) {
                sb.append(chars[j]);
            }
        }

        return sb.toString();
    }

    // Part 2: It would be impossible to manually simulate one billion cycles.
    // Therefore, we need to find when a grid pattern starts repeating itself (i.e. cycle detection),
    // fast-forward through those cycles (as the pattern would be the same) and perform
    // the last remaining cycles until we get to one billion cycles.
    private static int part2(char[][] grid) {
        // Store a string representation of the grid and the cycle number when that grid is seen.
        Map<String, Integer> store = new HashMap<>();

        int cycles = 0;

        // If we haven't already seen this grid before, keep looping until we find a duplicate.
        while(!store.containsKey(gridToString(grid))) {
            store.put(gridToString(grid), cycles);
            cycleStonesOnce(grid);
            cycles++;
        }

        // At this point, we've seen a grid twice.
        // The cycle length is the cycle number we're on right now
        // minus the cycle number that we first saw this grid.
        int cycleLength = cycles - store.get(gridToString(grid));

        // Therefore, the remaining cycles to perform would be the remaining cycles left
        // (one billion - the number of cycles performed so far) modulo the cycle length
        int remainingCycles = (1000000000 - cycles) % cycleLength;

        // Perform those remaining cycles.
        for (int i = 0; i < remainingCycles; i++) {
            cycleStonesOnce(grid);
        }

        return calculateTotalLoadNorth(grid);
    }
}