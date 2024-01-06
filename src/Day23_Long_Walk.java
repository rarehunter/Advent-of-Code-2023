import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Day23_Long_Walk {
    public static void main(String[] args) {
        File file = new File("./inputs/day23/day23.txt");
        char[][] grid;

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

    // Returns a list of neighbors of the current point.
    private static List<Point> getNeighbors(char[][] grid, Point current) {
        List<Point> neighbors = new ArrayList<>();
        int x = current.x;
        int y = current.y;

        // If we encounter a 'v', then the point directly below us is the only available point.
        if (grid[x][y] == 'v') {
            neighbors.add(new Point(x+1, y));
        } else if (grid[x][y] == '>') {
            // If we encounter a '>', then the point directly to the right of us is the only available point.
            neighbors.add(new Point(x, y+1));
        } else {
            // Others, we check all four cardinal directions, accounting for grid boundaries and forests.
            if (x - 1 >= 0 && grid[x-1][y] != '#') {
                neighbors.add(new Point(x-1, y));
            }

            if (x + 1 < grid.length && grid[x+1][y] != '#') {
                neighbors.add(new Point(x+1, y));
            }

            if (y - 1 >= 0 && grid[x][y-1] != '#') {
                neighbors.add(new Point(x, y-1));
            }

            if (y + 1 < grid[0].length && grid[x][y+1] != '#') {
                neighbors.add(new Point(x, y+1));
            }
        }

        return neighbors;
    }

    // Backtracking algorithm to recursively explore all paths in the grid.
    // At the end, the pathLengths list will store the lengths of all the valid paths
    // that are found.
    private static void explorePaths(char[][] grid,
                                     List<Integer> pathLengths,
                                     Set<Point> visited,
                                     Integer pathLength,
                                     Point current,
                                     Point end) {
        // If this is a valid solution, store the solution and return.
        if (current.equals(end)) {
            pathLengths.add(pathLength);
            return;
        }

        List<Point> neighbors = getNeighbors(grid, current);

        // For all valid neighbors, if we haven't visited that neighbor before,
        // consider it as part of the valid solution (i.e. increment path length and put it on our visited set)
        // and recurse. If we don't properly find a solution using this neighbor, we backtrack
        // (i.e. decrement path length and remove the neighbor from our visited set).
        for (Point neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                pathLength++;
                visited.add(neighbor);
                explorePaths(grid, pathLengths, visited, pathLength, neighbor, end);
                pathLength--;
                visited.remove(neighbor);
            }
        }
    }

    // Returns the max integer in a list.
    private static int findMax(List<Integer> pathLengths) {
        int maxLength = 0;
        for (Integer i : pathLengths) {
            maxLength = Math.max(i, maxLength);
        }

        return maxLength;
    }

    // Part 1: Use a vanilla backtracking technique to recursively search the grid, store
    // the lengths of all the valid paths that are found, and then return the max length.
    private static int part1(char[][] grid) {
        Point start = new Point(0,1);
        Point end = new Point(grid.length - 1, grid[0].length - 2);
        List<Integer> pathLengths = new ArrayList<>();
        Set<Point> visited = new HashSet<>();

        // Recursively explore all paths and store the lengths of all the valid paths
        // in the pathLengths list.
        explorePaths(grid, pathLengths, visited, 0, start, end);

        // Find the length of the longest path.
        return findMax(pathLengths);
    }

    // Given a grid, return a new grid with all the slopes ('>' or 'v' characters) removed
    // and replaced with empty spaces ('.').
    private static char[][] deslopifyGrid(char[][] grid) {
        char[][] deslopedGrid = new char[grid.length][grid[0].length];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'v' || grid[i][j] == '>') {
                    deslopedGrid[i][j] = '.';
                } else {
                    deslopedGrid[i][j] = grid[i][j];
                }
            }
        }

        return deslopedGrid;
    }

    // Returns a list of points which have at least three valid neighbors.
    // In other words, these are the points in which an actual decision of which direction to go
    // has to be made.
    private static List<Point> getJunctions(char[][] grid) {
        List<Point> junctions = new ArrayList<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '#') continue;

                Point current = new Point(i, j);
                List<Point> neighbors = getNeighbors(grid, current);
                if (neighbors.size() >= 3) {
                    junctions.add(current);
                }
            }
        }

        return junctions;
    }

    // Given a current junction point, walks down all the neighboring corridors (width of one space)
    // until it hits another junction point. Stores the result of a neighboring junction point
    // and the distance to it in the pathLengths hashmap.
    private static void walkCorridor(char[][] grid,
                                     Map<Point, Integer> pathLengths,
                                     Point current,
                                     List<Point> ends) {
        List<Point> neighbors = getNeighbors(grid, current);

        for (Point neighbor : neighbors) {
            Set<Point> visited = new HashSet<>();
            visited.add(neighbor);
            visited.add(current);
            int pathLength = 1; // Start the path by counting the neighbor
            Point ptr = neighbor;

            while (!ends.contains(ptr)) {
                List<Point> ptrNeighbors = getNeighbors(grid, ptr);

                for (Point ptrNeighbor : ptrNeighbors) {
                    if (!visited.contains(ptrNeighbor)) {
                        visited.add(ptrNeighbor);
                        ptr = ptrNeighbor;
                        pathLength++;
                        break;
                    }
                }
            }

            pathLengths.put(ptr, pathLength);
        }
    }

    // Given a list of junction points in the original grid, return an adjacency list representing
    // a contracted graph where the junction points are the nodes of the graph and the edges are distances to
    // neighboring junction points.
    private static Map<Point, Map<Point, Integer>> contractGrid(char[][] grid, List<Point> junctions) {
        Map<Point, Map<Point, Integer>> contractedGraph = new HashMap<>();
        Point start = new Point(0, 1);
        Point end = new Point(grid.length - 1, grid[0].length - 2);

        for (Point junction : junctions) {
            Map<Point, Integer> pathLengths = new HashMap<>();
            List<Point> ends = new ArrayList<>(junctions);
            ends.add(start);
            ends.add(end);
            ends.remove(junction);

            walkCorridor(grid, pathLengths, junction, ends);
            contractedGraph.put(junction, pathLengths);
        }

        return contractedGraph;
    }

    // Recursive function that runs a backtracking algorithm on the contracted graph.
    private static void exploreContractedPaths(Map<Point, Map<Point, Integer>> graph,
                                               List<Integer> pathLengths,
                                               Set<Point> visited,
                                               Integer pathLength,
                                               Point current,
                                               Point start,
                                               Point end) {
        // If we've reached a junction that is a neighbor of the end point,
        // we can and should directly proceed to the end point from here so let's add on
        // the distance from this current junction to the end and store it.
        Map<Point, Integer> neighborsToCurrent = graph.get(current);
        if (neighborsToCurrent.containsKey(end)) {
            pathLengths.add(pathLength + neighborsToCurrent.get(end));
            return;
        }

        // For all valid neighbors, if we haven't visited that neighbor before,
        // consider it as part of the valid solution (i.e. increase path length and put it on our visited set)
        // and recurse. If we don't properly find a solution using this neighbor, we backtrack
        // (i.e. decrease path length and remove the neighbor from our visited set).
        for (Point neighbor : neighborsToCurrent.keySet()) {
            // For the starting junction, we don't want to go back to the start point
            // so don't consider the start point a valid neighbor.
            if (neighbor.equals(start))
                continue;

            if (!visited.contains(neighbor)) {
                pathLength += neighborsToCurrent.get(neighbor);
                visited.add(neighbor);

                exploreContractedPaths(graph, pathLengths, visited, pathLength, neighbor, start, end);

                pathLength -= neighborsToCurrent.get(neighbor);
                visited.remove(neighbor);
            }
        }
    }

    // Finds the closest junction point to the start point. This will be the junction point that has
    // the start point in its neighbors.
    private static Point findStartJunction(Map<Point, Map<Point, Integer>> graph, Point start) {
        for (Point junction : graph.keySet()) {
            Map<Point, Integer> junctionNeighbors = graph.get(junction);
            for (Point neighbor : junctionNeighbors.keySet()) {
                if (neighbor.equals(start)) {
                    return junction;
                }
            }
        }

        return null;
    }

    // Part 2: The problem of finding the longest path (without cycles) is that it is NP-complete.
    // Therefore, we are left with essentially evaluating every path and choosing the longest one.
    // In part 1, we are able to brute force our way through the grid. However, in this case (part 2), such a
    // solution would not work.
    // But, we observe a key point about our input. Even though there are many tiles in the grid,
    // the number of paths is fairly low. In other words, the maze itself has long stretches of corridors
    // in which there is only one legal neighbor every time. Therefore, we can treat these corridors
    // as a single node in order to speed up runtime. This involves implementing edge contraction.
    // We find junctions/points in which there are at least three valid neighbors and contract the graph
    // to be these junctions instead.
    private static int part2(char[][] grid) {
        // Start by replacing all 'v' and '>' characters with '.' for simplicity sake.
        char[][] deslopedGrid = deslopifyGrid(grid);

        // We start by finding the points in our grid in which there is at least three valid neighbors.
        // These are the points in which a junction or decision has to be made in which direction to go.
        // We will contract our graph to simply be these junctions instead.
        List<Point> junctions = getJunctions(deslopedGrid);

        // Contract the graph. The resulting map consists of junction points that each
        // contain the distance to adjacent junction points.
        Map<Point, Map<Point, Integer>> smallerGraph = contractGrid(deslopedGrid, junctions);

        Point start = new Point(0, 1);
        Point end = new Point(grid.length - 1, grid[0].length - 2);
        List<Integer> pathLengths = new ArrayList<>();
        Set<Point> visited = new HashSet<>();

        // The start junction is the junction that contains the start point as its neighbor.
        // We'll start our graph search from this junction rather than the start point.
        Point startJunction = findStartJunction(smallerGraph, start);
        int pathLength = smallerGraph.get(startJunction).get(start);

        visited.add(startJunction);

        // Recursively explore all paths and store the lengths of all the valid paths
        // in the pathLengths list.
        exploreContractedPaths(smallerGraph, pathLengths, visited, pathLength, startJunction, start, end);

        // Find the length of the longest path.
        return findMax(pathLengths);
    }
}