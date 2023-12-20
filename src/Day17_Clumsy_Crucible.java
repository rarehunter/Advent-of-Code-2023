import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

enum CrucibleDirection{
    UP, DOWN, LEFT, RIGHT;
}

// State of the crucible that we will be running Dijkstra's algorithm on.
// The state consists of the cost (heat loss) of that state, the coordinate of the grid,
// the direction the crucible is traveling, and the number of steps taken by the crucible in that direction so far.
record CrucibleState(int heatLoss, Point point, CrucibleDirection direction, int steps) {
    public int getHeatLoss() {
        return this.heatLoss;
    }

    public Point getPoint() {
        return this.point;
    }

    public CrucibleDirection getDirection() {
        return this.direction;
    }

    public int getSteps() {
        return this.steps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrucibleState that = (CrucibleState) o;
        return steps == that.steps && point.equals(that.point) && direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, direction, steps);
    }
}

public class Day17_Clumsy_Crucible {
    static int[][] grid;

    static class NodeComparator implements Comparator<CrucibleState> {
        @Override
        public int compare(CrucibleState a, CrucibleState b) {
            return a.getHeatLoss() - b.getHeatLoss();
        }
    }

    public static void main(String[] args) {
        File file = new File("./inputs/day17/day17.txt");

        try {
            Scanner sc = new Scanner(file);
            Scanner sc2 = new Scanner(file);

            int height = 0;
            int width = 0;
            // Parse the input through two passes.
            // The first pass finds the height and width of the grid.
            // The second pass initializes the 2d int array and populates it with numbers.
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                width = line.length();
                height++;
            }

            grid = new int[height][width];
            int row = 0;
            while (sc2.hasNextLine()) {
                String line = sc2.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    grid[row][i] = Integer.parseInt(String.valueOf(line.charAt(i)));
                }
                row++;
            }

            int part1 = part1();
            System.out.println("Part 1 is: " + part1);

            int part2 = part2();
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns true if the given x and y are within the bounds of the grid. Returns false otherwise.
    private static boolean isInBounds(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
    }

    // Run Dijkstra's algorithm, not in the original grid itself, but on a graph of CrucibleStates.
    // Each node represents a state that the crucible can be in and incorporates the direction and number
    // of steps that the crucible has taken in that direction.
    private static int findCrucibleHeatLoss(int minBeforeTurning, int maxStraight) {
        Set<CrucibleState> visited = new HashSet<>();
        PriorityQueue<CrucibleState> pq = new PriorityQueue<>(new NodeComparator());

        // initial state
        pq.add(new CrucibleState(0, new Point(0, 0), null, 0));

        // terminal point
        Point terminal = new Point(grid.length - 1, grid[0].length - 1);

        while (!pq.isEmpty()) {
            CrucibleState node = pq.poll();
            Point nodePoint = node.getPoint();
            int numSteps = node.getSteps();
            int heatLoss = node.getHeatLoss();
            CrucibleDirection direction = node.getDirection();

            if (visited.contains(node))
                continue;

            // Terminate once we've reached the bottom right node.
            if (nodePoint.equals(terminal) && numSteps >= minBeforeTurning) {
                return node.getHeatLoss();
            }

            visited.add(node);

            // If we haven't taken at most 'maxStraight' steps in a single direction yet,
            // then we can consider the same direction.
            // If we're just standing still right now (initial state of algorithm),
            // then we'll which direction to turn in the code block below this if statement.
            if (numSteps < maxStraight && direction != null) {
                Point sameDirection = null;

                if (direction == CrucibleDirection.UP) {
                    sameDirection = new Point(nodePoint.x - 1, nodePoint.y);
                } else if (direction == CrucibleDirection.DOWN){
                    sameDirection = new Point(nodePoint.x + 1, nodePoint.y);
                } else if (direction == CrucibleDirection.RIGHT){
                    sameDirection = new Point(nodePoint.x, nodePoint.y + 1);
                } else if (direction == CrucibleDirection.LEFT){
                    sameDirection = new Point(nodePoint.x, nodePoint.y - 1);
                }

                if (isInBounds(sameDirection.x, sameDirection.y)) {
                    int newLoss = heatLoss + grid[sameDirection.x][sameDirection.y];
                    CrucibleState state = new CrucibleState(newLoss, sameDirection, direction, numSteps + 1);
                    pq.add(state);
                }
            }

            // If we're in the initial state of the algorithm (standing still),
            // Since we start at the top right, we'll only consider moving right or down.
            if (direction == null) {
                int currX = nodePoint.x;
                int currY = nodePoint.y;

                Point right = new Point(currX, currY + 1);
                Point down = new Point(currX + 1, currY);

                if (isInBounds(right.x, right.y)) {
                    int newLoss = heatLoss + grid[right.x][right.y];
                    pq.add(new CrucibleState(newLoss, right, CrucibleDirection.RIGHT, 1));
                }

                if (isInBounds(down.x, down.y)) {
                    int newLoss = heatLoss + grid[down.x][down.y];
                    pq.add(new CrucibleState(newLoss, down, CrucibleDirection.DOWN, 1));
                }
            }

            // Check if we're allowed to turn yet. If so, we consider turning left/right by 90 degrees.
            if (numSteps >= minBeforeTurning || direction == null) {
                int currX = nodePoint.x;
                int currY = nodePoint.y;

                // If we're going up or down, then turning 90 degrees results in left and right
                // If we're going left or right, then turning 90 degrees results in up and down.
                if (direction == CrucibleDirection.UP || direction == CrucibleDirection.DOWN) {
                    Point left = new Point(currX, currY - 1);
                    Point right = new Point(currX, currY + 1);

                    if (isInBounds(left.x, left.y)) {
                        int newLoss = heatLoss + grid[left.x][left.y];
                        pq.add(new CrucibleState(newLoss, left, CrucibleDirection.LEFT, 1));
                    }

                    if (isInBounds(right.x, right.y)) {
                        int newLoss = heatLoss + grid[right.x][right.y];
                        pq.add(new CrucibleState(newLoss, right, CrucibleDirection.RIGHT, 1));
                    }
                } else if (direction == CrucibleDirection.LEFT || direction == CrucibleDirection.RIGHT) {
                    Point up = new Point(currX - 1, currY);
                    Point down = new Point(currX + 1, currY);

                    if (isInBounds(up.x, up.y)) {
                        int newLoss = heatLoss + grid[up.x][up.y];
                        pq.add(new CrucibleState(newLoss, up, CrucibleDirection.UP,1));
                    }

                    if (isInBounds(down.x, down.y)) {
                        int newLoss = heatLoss + grid[down.x][down.y];
                        pq.add(new CrucibleState(newLoss, down, CrucibleDirection.DOWN, 1));
                    }
                }
            }
        }

        return 0;
    }

    // Part 1: Finds the minimum heat loss of the crucible if we can move at most 3 spaces before turning.
    private static int part1() {
        return findCrucibleHeatLoss(0, 3);
    }

    // Part 2: Find the minimum heat loss of the crucible if we can move at most 10 spaces before turning,
    // and have to move at least 4 spaces before turning.
    private static int part2() {
        return findCrucibleHeatLoss(4, 10);
    }
}