import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.Point;

enum BeamDirection {
    UP, DOWN, LEFT, RIGHT
}

record PointDirection(Point p, BeamDirection dir) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointDirection that = (PointDirection) o;
        return p.equals(that.p) && dir == that.dir;
    }

    @Override
    public int hashCode() {
        return Objects.hash(p, dir);
    }
}
public class Day16_Floor_Will_Be_Lava {
    public static void main(String[] args) {
        File file = new File("./inputs/day16/day16.txt");
        char[][] grid;

        try {
            Scanner sc = new Scanner(file);
            Scanner sc2 = new Scanner(file);

            int height = 0;
            int width = 0;

            // Parse the input over two passes.
            // The first pass stores the height and width of the input grid.
            // The second pass creates the 2d char array and populates it with characters.
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

    // Given the direction that a beam is traveling and the current coordinate of the beam,
    // returns the next coordinate of the beam if it is still on the grid. Returns null otherwise.
    private static Point getNextPoint(BeamDirection direction, Point current, int width, int height) {
        if (direction == BeamDirection.UP) {
            if (current.x-1 < 0) // If the next point is off the grid, return null.
                return null;
            return new Point(current.x-1, current.y);
        } else if (direction == BeamDirection.DOWN) {
            if (current.x+1 >= height) // If the next point is off the grid, return null.
                return null;
            return new Point(current.x+1, current.y);
        } else if (direction == BeamDirection.LEFT) {
            if (current.y-1 < 0) // If the next point is off the grid, return null.
                return null;
            return  new Point(current.x, current.y-1);
        } else if (direction == BeamDirection.RIGHT) {
            if (current.y+1 >= width) // If the next point is off the grid, return null.
                return null;
            return  new Point(current.x, current.y+1);
        }

        return null;
    }

    // Shoots a beam from the given point in the given direction.
    private static void shootBeam(char[][] grid, Point point, BeamDirection direction, Set<PointDirection> visited) {
        // Stop if the current point we're given is off the edge of the grid.
        if (point.x < 0 || point.x > grid.length - 1 || point.y < 0 || point.y > grid[0].length - 1)
            return;

        // Stop if we've already visited this point from this direction. In other words,
        // we're allowed to revisit a point so long as it's from a different direction.
        if (visited.contains(new PointDirection(point, direction)))
            return;

        Point currentPoint = point;
        Point nextPoint;

        while (true) {
            // Mark that we've already seen this point from this direction.
            visited.add(new PointDirection(currentPoint, direction));

            // If we encounter a '/', then if we're coming at it from the left, then shoot a beam up.
            // If we're coming at it from the right, then shoot a beam down.
            // If we're coming at it from the top, then shoot a beam left.
            // If we're coming at it from the bottom, then shoot a beam right.
            if (grid[currentPoint.x][currentPoint.y] == '/') {
                if (direction == BeamDirection.RIGHT)
                    shootBeam(grid, new Point(currentPoint.x-1, currentPoint.y), BeamDirection.UP, visited);
                else if (direction == BeamDirection.LEFT)
                    shootBeam(grid, new Point(currentPoint.x+1, currentPoint.y), BeamDirection.DOWN, visited);
                else if (direction == BeamDirection.UP)
                    shootBeam(grid, new Point(currentPoint.x, currentPoint.y+1), BeamDirection.RIGHT, visited);
                else if (direction == BeamDirection.DOWN)
                    shootBeam(grid, new Point(currentPoint.x, currentPoint.y-1), BeamDirection.LEFT, visited);

                // Stop shooting our current beam.
                break;
            } else if (grid[currentPoint.x][currentPoint.y] == '\\') {
                // If we encounter a '\', then if we're coming at it from the left, then shoot a beam down.
                // If we're coming at it from the right, then shoot a beam up.
                // If we're coming at it from the top, then shoot a beam right.
                // If we're coming at it from the bottom, then shoot a beam left.
                if (direction == BeamDirection.RIGHT)
                    shootBeam(grid, new Point(currentPoint.x+1, currentPoint.y), BeamDirection.DOWN, visited);
                else if (direction == BeamDirection.LEFT)
                    shootBeam(grid, new Point(currentPoint.x-1, currentPoint.y), BeamDirection.UP, visited);
                else if (direction == BeamDirection.UP)
                    shootBeam(grid, new Point(currentPoint.x, currentPoint.y-1), BeamDirection.LEFT, visited);
                else if (direction == BeamDirection.DOWN)
                    shootBeam(grid, new Point(currentPoint.x, currentPoint.y+1), BeamDirection.RIGHT, visited);

                // Stop shooting our current beam.
                break;
            } else if (grid[currentPoint.x][currentPoint.y] == '|') {
                // If we encounter a '|' and we're going up and down, we don't have to do anything.
                // Otherwise, we split into two beams, shooting up and down.
                if (direction == BeamDirection.LEFT || direction == BeamDirection.RIGHT) {
                    shootBeam(grid, new Point(currentPoint.x-1, currentPoint.y), BeamDirection.UP, visited);
                    shootBeam(grid, new Point(currentPoint.x+1, currentPoint.y), BeamDirection.DOWN, visited);
                    break; // Stop shooting our current beam.
                }
            } else if (grid[currentPoint.x][currentPoint.y] == '-') {
                // If we encounter a '-' and we're going left and right, we don't have to do anything.
                // Otherwise, we split into two beams, shooting left and right.
                if (direction == BeamDirection.UP || direction == BeamDirection.DOWN) {
                    shootBeam(grid, new Point(currentPoint.x, currentPoint.y-1), BeamDirection.LEFT, visited);
                    shootBeam(grid, new Point(currentPoint.x, currentPoint.y+1), BeamDirection.RIGHT, visited);
                    break; // Stop shooting our current beam.
                }
            }

            // If we're here, that means we haven't encountered any mirrors OR we're going in the same
            // direction as a mirror, so we effectively ignore it. Given a direction, find the next point in
            // that direction to move to.
            nextPoint = getNextPoint(direction, currentPoint, grid[0].length, grid.length);

            // We've travelled off the grid, so stop this beam.
            if (nextPoint == null) {
                break;
            }

            currentPoint = nextPoint;
        }
    }

    // Given a starting point and a direction, shoot a beam starting from that point,
    // going in that direction and count how many points are visited.
    private static int shootBeamAndCountVisited(char[][] grid, int i, int j, BeamDirection direction) {
        // We allow for a point to be visited twice as long as the beam is going in a different direction.
        Set<PointDirection> visited = new HashSet<>();
        shootBeam(grid, new Point(i, j), direction, visited);

        // Take the set of visited PointDirection objects and filter them further by unique points.
        Set<Point> visitedPoints = new HashSet<>();

        for (PointDirection pd : visited) {
            visitedPoints.add(pd.p());
        }
        return visitedPoints.size();
    }

    // Part 1: Shoot a beam from the top left corner moving right and return how many unique points
    // we've visited.
    private static int part1(char[][] grid) {
        return shootBeamAndCountVisited(grid, 0, 0, BeamDirection.RIGHT);
    }

    // Part 2: For every point in the top, bottom, left, and right edges of the grid,
    // shoot a beam from each of those points inward into the grid and return the max of how many unique points
    // we've visited.
    private static int part2(char[][] grid) {
        int max = 0;

        // From the top edge, shoot beam down.
        for (int j = 0; j < grid[0].length; j++) {
            int numStartingTop = shootBeamAndCountVisited(grid, 0, j, BeamDirection.DOWN);
            max = Math.max(max, numStartingTop);

            int numStartingBottom = shootBeamAndCountVisited(grid, grid.length - 1, j, BeamDirection.UP);
            max = Math.max(max, numStartingBottom);

        }

        // From the left edge, shoot beam to the right.
        // From the right edge, shoot beam to the left.
        for (int i = 0; i < grid.length; i++) {
            int numStartingLeft = shootBeamAndCountVisited(grid, i, 0, BeamDirection.RIGHT);
            max = Math.max(max, numStartingLeft);

            int numStartingRight = shootBeamAndCountVisited(grid, i, grid[0].length - 1, BeamDirection.LEFT);
            max = Math.max(max, numStartingRight);
        }

        return max;
    }
}