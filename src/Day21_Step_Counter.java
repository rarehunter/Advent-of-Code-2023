import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Point;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

record StepState(Point point, int stepsRemaining) { }

public class Day21_Step_Counter {
    public static void main(String[] args) {
        File file = new File("./inputs/day21/day21.txt");
        char[][] grid;
        Point start = null;

        try {
            Scanner sc = new Scanner(file);
            Scanner sc2 = new Scanner(file);

            int height = 0;
            int width = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                width = line.length();
                height++;
            }

            grid = new char[height][width];

            int row = 0;
            while (sc2.hasNextLine()) {
                String line = sc2.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    grid[row][i] = line.charAt(i);
                    if (line.charAt(i) == 'S') {
                        start = new Point(row, i);
                    }
                }
                row++;
            }

            // Set the start point to an empty cell instead of an 'S'
            grid[start.x][start.y] = '.';

            int part1 = part1(grid, start);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2();
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

    // Returns the empty neighbors of a current point on a grid, checking for boundary limits.
    private static List<Point> getNeighbors(char[][] grid, Point current) {
        List<Point> neighbors = new ArrayList<>();
        int x = current.x;
        int y = current.y;

        if (x - 1 >= 0 && grid[x-1][y] != '#') {
            neighbors.add(new Point(x - 1, y));
        }

        if (x + 1 < grid.length && grid[x+1][y] != '#') {
            neighbors.add(new Point(x + 1, y));
        }

        if (y - 1 >= 0 && grid[x][y-1] != '#') {
            neighbors.add(new Point(x, y - 1));
        }

        if (y + 1 < grid[0].length && grid[x][y+1] != '#') {
            neighbors.add(new Point(x, y + 1));
        }

        return neighbors;
    }

    // Returns the empty neighbors of a point on an infinitely tiled grid.
    private static List<Point> getNeighborsTiled(char[][] grid, Point current) {
        List<Point> neighbors = new ArrayList<>();
        int x = current.x;
        int y = current.y;
        int width = grid[0].length;
        int height = grid.length;


        if (grid[Math.floorMod(x-1, height)][Math.floorMod(y, width)] != '#') {
            neighbors.add(new Point(x-1, y));
        }

        if (grid[Math.floorMod(x+1, height)][Math.floorMod(y, width)] != '#') {
            neighbors.add(new Point(x+1, y));
        }

        if (grid[Math.floorMod(x, height)][Math.floorMod(y-1, width)] != '#') {
            neighbors.add(new Point(x, y-1));
        }

        if (grid[Math.floorMod(x, height)][Math.floorMod(y+1, width)] != '#') {
            neighbors.add(new Point(x, y+1));
        }

        return neighbors;
    }

    // Returns a set of points representing
    // the neighbor's of all the unique points you could be at.
    private static Set<Point> step(char[][] grid, Set<Point> uniquePointsYouCouldBeAt) {
        Set<Point> nextPoints = new HashSet<>();

        for (Point p : uniquePointsYouCouldBeAt) {
            List<Point> neighbors = getNeighbors(grid, p);
            nextPoints.addAll(neighbors);
        }

        return nextPoints;
    }

    // Part 1: Simulate 64 iterations of steps. At each step, all the neighbors of the points you could be at
    // are returned. Returns how many points you could reach in exactly 64 points (incl. back to the points
    // you've already visited).
    private static int part1(char[][] grid, Point start) {
        Set<Point> uniquePointsYouCouldBeAt = new HashSet<>();
        uniquePointsYouCouldBeAt.add(start);

        for (int i = 0; i < 64; i++) {
            uniquePointsYouCouldBeAt = step(grid, uniquePointsYouCouldBeAt);
        }
        return uniquePointsYouCouldBeAt.size();
    }

    // This is a re-implementation of part 1 of the problem but using a proper BFS and storing
    // a state of the steps (incl how many steps are remaining in the walk). The original part 1
    // implementation stored all points seen in a set instead.
    private static int part1BFS(char[][] grid, Point start) {
        Set<Point> uniquePointsYouCouldBeAt = new HashSet<>();
        Set<Point> visited = new HashSet<>();
        visited.add(start);

        Queue<StepState> queue = new LinkedList<>();
        queue.add(new StepState(start, 589));

        while (!queue.isEmpty()) {
            StepState state = queue.poll();
            Point current = state.point();
            int stepsRemaining = state.stepsRemaining();

            // Because we're asked to walk an even number of steps,
            // we can always return to any point that we've seen with an even number of
            // steps remaining since we can just alternate back and forth.
            if (stepsRemaining % 2 == 0) {
                uniquePointsYouCouldBeAt.add(current);
            }

            // If we don't have any more steps left to walk in this step,
            // let's just move on to check another state.
            if (stepsRemaining == 0) {
                continue;
            }

            List<Point> neighbors = getNeighborsTiled(grid, current);
            for (Point neighbor : neighbors) {
                if (visited.contains(neighbor))
                    continue;

                visited.add(neighbor);
                queue.add(new StepState(neighbor, stepsRemaining - 1));
            }
        }

        return uniquePointsYouCouldBeAt.size();
    }

    // Part 2: For this problem, we need to make a few observations/assumptions about our input.
    // We observe that:
    // 1. The entire row containing the starting position is empty.
    // 2. The entire column containing the starting position is empty.
    // 3. All four edges of the grid are empty. (Empty meaning that it does not contain an obstacle '#').
    // 4. The starting position S is in the middle of the grid.
    // 5. Grid dimensions are 131 x 131 and the starting position is (65, 65)
    // Therefore, we could go all the way to the right, up, left, or down in a straight line.
    // The problem is asking us for 26501365 steps. 65 steps gets us to the edge of the first grid.
    // 131 steps gets us to the edge of the next grid. Therefore, we can decompose the total steps into a
    // formula like: 202300 * 131 + 65. In other words, the steps will reach the end of 202300 full grids
    // in all four directions. If we run the above part 1 algorithm for increasing grids, we can notice
    // a pattern forming.
    // n = 65  (131 * 0 + 65)    3835 points
    // n = 196 (131 * 1 + 65)   34125 points
    // n = 327 (131 * 2 + 65)   94603 points
    // n = 458 (131 * 3 + 65)  185269 points
    // n = 589 (131 * 4 + 65)  306123 points
    // We use Wolfram Alpha to plot these points on a coordinate plane and notice that it looks like a
    // quadratic equation. We find the best fit equation exactly to be:
    // 15094x^2 + 15196x + 3835
    // Therefore, in order to find the number of points for 202300 grids, we just insert x = 202300 and
    // we'll have our answer.
    private static long part2() {
        return 15094 * (long)Math.pow(202300, 2) + ((long)15196 * 202300) + 3835;
    }
}