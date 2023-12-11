import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Day10_Pipe_Maze {
    public static void main(String[] args) {
        File file = new File("./inputs/day10/day10.txt");

        try {
            Scanner sc = new Scanner(file);
            Scanner sc2 = new Scanner(file);
            Point start = new Point();

            // First pass through the input finds the height and width of the grid.
            int height = 0, width = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                width = line.length();
                height++;
            }

            // Second pass through the input initializes a 2d char array and populates it.
            char[][] grid = new char[height][width];
            int row = 0;
            while (sc2.hasNextLine()) {
                String line = sc2.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    grid[row][i] = line.charAt(i);

                    if (line.charAt(i) == 'S') {
                        start.x = row;
                        start.y = i;
                    }
                }

                row++;
            }

            int part1 = part1(grid, start);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(grid, start);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void printGrid(char[][] grid) {
        for (char[] chars : grid) {
            for (int i = 0; i < chars.length; i++) {
                System.out.print(chars[i]);
            }
            System.out.println();
        }
    }

    // If the cell above our current cell has a valid pipe, return its coordinate.
    // Otherwise, return null.
    private static Point checkTop(char[][] grid, Point current) {
        if (current.x > 0) {
            char charCurrent = grid[current.x][current.y];
            char charAbove = grid[current.x - 1][current.y];

            if (charCurrent == 'L' || charCurrent == 'J' || charCurrent == '|') {
                if (charAbove == '7' || charAbove == 'F' || charAbove == '|') {
                    return new Point(current.x - 1, current.y);
                }
            }
        }

        return null;
    }

    // If the cell below our current cell has a valid pipe, return its coordinate.
    // Otherwise, return null.
    private static Point checkBottom(char[][] grid, Point current) {
        if (current.x < grid.length - 1) {
            char charCurrent = grid[current.x][current.y];
            char charBelow = grid[current.x + 1][current.y];

            if (charCurrent == '7' || charCurrent == 'F' || charCurrent == '|') {
                if (charBelow == 'J' || charBelow == 'L' || charBelow == '|') {
                    return new Point(current.x + 1, current.y);
                }
            }
        }

        return null;
    }

    // If the cell to the left of our current cell has a valid pipe, return its coordinate.
    // Otherwise, return null.
    private static Point checkLeft(char[][] grid, Point current) {
        if (current.y > 0) {
            char charCurrent = grid[current.x][current.y];
            char charLeft = grid[current.x][current.y - 1];

            if (charCurrent == 'J' || charCurrent == '7' || charCurrent == '-') {
                if (charLeft == 'F' || charLeft == 'L' || charLeft == '-') {
                    return new Point(current.x, current.y - 1);
                }
            }
        }

        return null;
    }

    // If the cell to the right of our current cell has a valid pipe, return its coordinate.
    // Otherwise, return null.
    private static Point checkRight(char[][] grid, Point current) {
        if (current.y < grid[0].length - 1) {
            char charCurrent = grid[current.x][current.y];
            char charRight = grid[current.x][current.y + 1];

            if (charCurrent == 'F' || charCurrent == 'L' || charCurrent == '-') {
                if (charRight == 'J' || charRight == '7' || charRight == '-') {
                    return new Point(current.x, current.y + 1);
                }
            }
        }

        return null;
    }

    // Try all possible pipes for the start pipe. A pipe is a valid pipe if it has two valid directions
    // that neighbor pipes could be.
    private static void inferStartJunction(char[][] grid, Point start) {
        char[] pipes = { '-', '|', 'L', '7', 'J', 'F' };

        for (char pipe : pipes) {
            grid[start.x][start.y] = pipe;
            Point top = checkTop(grid, start);
            Point left = checkLeft(grid, start);
            Point right = checkRight(grid, start);
            Point bottom = checkBottom(grid, start);

            if ((top != null && bottom != null) ||
                    (top != null && left != null) ||
                    (top != null && right != null) ||
                    (left != null && bottom != null) ||
                    (left != null && right != null) ||
                    (right != null && bottom != null))
                break;
        }
    }

    // Part 1: From the start pipe, traverse the full loop by checking all four directions and moving to a cell
    // that we haven't visited yet.
    private static List<Point> getPipePath(char[][] grid, Point start) {
        // Start by filling in the start pipe.
        inferStartJunction(grid, start);

        List<Point> currentPath = new ArrayList<>();
        currentPath.add(start);
        Point current = start;

        // Keep looping until we've returned to our start pipe.
        while (true) {
            // Check all four directions for valid pipes.
            Point top = checkTop(grid, current);
            Point left = checkLeft(grid, current);
            Point right = checkRight(grid, current);
            Point bottom = checkBottom(grid, current);

            // Seet the new current pipe and add it to our list.
            if (top != null && !currentPath.contains(top)) {
                current = top;
                currentPath.add(top);
            } else if (left != null && !currentPath.contains(left)) {
                current = left;
                currentPath.add(left);
            } else if (right != null && !currentPath.contains(right)) {
                current = right;
                currentPath.add(right);
            } else if (bottom != null && !currentPath.contains(bottom)) {
                current = bottom;
                currentPath.add(bottom);
            } else {
                // There are no more paths found which must means that we are back at the start so break.
                break;
            }
        }

        return currentPath;
    }

    // Part 1: Get the path that makes up the loop.
    // The point farthest from the starting point is half of the length of the loop.
    private static int part1(char[][] grid, Point start) {
        List<Point> currentPath = getPipePath(grid, start);
        return currentPath.size() / 2;
    }

    // Part 2: If we consider the loop as an integral polygon then Pick's theorem relates the area of the closed loop
    // (call it A) (which can be calculated using the shoelace formula),
    // the number of integer points on the boundary of the closed loop (which is the length of the close loop,
    // our answer for part 1, call it b) and the number of integer points in the interior of the loop
    // (which is the answer, call it i).
    private static int part2(char[][] grid, Point start) {
        List<Point> currentPath = getPipePath(grid, start);

        // The shoelace formula gives the area of a polygon whose vertices are described by their
        // Cartesian coordinates in the plane
        int shoelaceSum = 0;
        int shoelaceDiff = 0;
        for (int i = 0; i < currentPath.size(); i++) {
            Point first = currentPath.get(i);
            Point second = currentPath.get((i + 1) % currentPath.size());
            shoelaceSum +=  first.x * second.y;
            shoelaceDiff += first.y * second.x;
        }

        int area = Math.abs(shoelaceSum - shoelaceDiff) / 2;

        // Pick's theorem: A = i + (b/2) - 1
        return area + 1 - (currentPath.size() / 2);
    }
}