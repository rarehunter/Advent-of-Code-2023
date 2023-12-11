import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Day11_Cosmic_Expansion {
    public static void main(String[] args) {
        File file = new File("./inputs/day11/day11.txt");
        List<Point> galaxies = new ArrayList<>();
        List<Point> galaxiesCopy = new ArrayList<>(); // A second instance of the galaxies for use in part 2.

        try {
            Scanner sc = new Scanner(file);

            int numRows = 0;
            int numCols = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (numCols == 0) numCols = line.length();

                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '#') {
                        galaxies.add(new Point(numRows, i));
                        galaxiesCopy.add(new Point(numRows, i));
                    }
                }
                numRows++;
            }

            long part1 = part1(galaxies, numRows, numCols);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(galaxiesCopy, numRows, numCols);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns a list of indices in which there is no galaxy in that row.
    private static List<Integer> findRowsWithNoGalaxies(List<Point> galaxies, int numRows) {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            boolean galaxyFound = false;
            for (Point p : galaxies) {
                if (p.x == i) {
                    galaxyFound = true;
                    break;
                }
            }

            if (!galaxyFound) {
                indices.add(i);
            }
        }

        return indices;
    }

    // Returns a list of indices in which there is no galaxy in the column.
    private static List<Integer> findColumnsWithNoGalaxies(List<Point> galaxies, int numCols) {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < numCols; i++) {
            boolean galaxyFound = false;
            for (Point p : galaxies) {
                if (p.y == i) {
                    galaxyFound = true;
                    break;
                }
            }

            if (!galaxyFound) {
                indices.add(i);
            }
        }

        return indices;
    }

    // Given the list of row indices that are empty (don't have galaxies in that row),
    // modifies the galaxy coordinates to reflect a double expansion for that row.
    // Part 2 passes in a parameter to reflect a one million times expansion for that row.
    private static void expandRows(List<Point> galaxies, List<Integer> rowIndices, int part) {
        for (Point p : galaxies) {
            int numIndicesLessThan = 0;
            for (Integer index : rowIndices) {
                if (index < p.x) {
                    numIndicesLessThan++;
                }
            }

            if (part == 1)
                p.x = p.x + numIndicesLessThan;
            else if (part == 2)
                p.x = p.x + numIndicesLessThan * 999999;
        }
    }

    // Given the list of column indices that are empty (don't have galaxies in that column),
    // modifies the galaxy coordinates to reflect a double expansion for that column.
    // Part 2 passes in a parameter to reflect a one million times expansion for that column.
    private static void expandColumns(List<Point> galaxies, List<Integer> columnIndices, int part) {
        for (Point p : galaxies) {
            int numIndicesLessThan = 0;
            for (Integer index : columnIndices) {
                if (index < p.y) {
                    numIndicesLessThan++;
                }
            }

            if (part == 1)
                p.y = p.y + numIndicesLessThan;
            else if (part == 2)
                p.y = p.y + numIndicesLessThan * 999999;
        }
    }

    // For each pair of galaxies, calculate the sum of its manhattan distances.
    private static long calculateManhattanDistanceForGalaxyPairs(List<Point> galaxies) {
        long sum = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            for (int j = i+1; j < galaxies.size(); j++) {
                Point p1 = galaxies.get(i);
                Point p2 = galaxies.get(j);
                sum += Math.abs(p1.x-p2.x) + Math.abs(p1.y-p2.y);
            }
        }

        return sum;
    }

    // Part 1: Identify the row and column indices that have no galaxies present. Then, expand the
    // coordinates of the galaxies to reflect a double-expansion for empty rows and columns.
    // Finally, calculate the sum of the manhattan distances between each pair of galaxies.
    private static long part1(List<Point> galaxies, int numRows, int numCols) {
        List<Integer> rowIndices = findRowsWithNoGalaxies(galaxies, numRows);
        List<Integer> columnIndices = findColumnsWithNoGalaxies(galaxies, numCols);
        expandRows(galaxies, rowIndices, 1);
        expandColumns(galaxies, columnIndices, 1);
        return calculateManhattanDistanceForGalaxyPairs(galaxies);
    }

    // Part 2: Identify the row and column indices that have no galaxies present. Then, expand the
    // coordinates of the galaxies to reflect a one-million-times-expansion for empty rows and columns.
    // Finally, calculate the sum of the manhattan distances between each pair of galaxies.
    private static long part2(List<Point> galaxies, int numRows, int numCols) {
        List<Integer> rowIndices = findRowsWithNoGalaxies(galaxies, numRows);
        List<Integer> columnIndices = findColumnsWithNoGalaxies(galaxies, numCols);
        expandRows(galaxies, rowIndices, 2);
        expandColumns(galaxies, columnIndices, 2);
        return calculateManhattanDistanceForGalaxyPairs(galaxies);
    }
}