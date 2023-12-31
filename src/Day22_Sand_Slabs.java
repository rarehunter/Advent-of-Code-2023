import java.io.File;
import java.io.IOException;
import java.util.*;

// Represents a 1-unit cube of a brick which is denoted by its x, y, z coordinates in 3D space.
class Cube {
    public int x;
    public int y;
    public int z;

    public Cube(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
    public String toString() { return "(" + x + "," + y + "," + z + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cube cube = (Cube) o;
        return x == cube.x && y == cube.y && z == cube.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}

// Represents a brick.
class Brick {
    // When parsing the initial input, the start and end properties are set.
    // Afterwards, they are no longer updated.
    public Cube start;
    public Cube end;

    public List<Cube> cubes;
    public List<Brick> supportedBy; // Stores the list of bricks that this brick is supported by.
    public List<Brick> supports; // Stores the list of bricks that this brick supports.
    public boolean fallen; // Used in part 2. True if the brick is disintegrated/fallen. False otherwise.

    public Brick(Cube start, Cube end) {
        this.start = start;
        this.end = end;
        this.cubes = new ArrayList<>();
        this.supportedBy = new ArrayList<>();
        this.supports = new ArrayList<>();
        this.fallen = false;

        // Use the start and end cubes to construct a list of all the cubes representing this brick.
        if (start.x != end.x) {
            for (int i = start.x; i <= end.x; i++) {
                cubes.add(new Cube(i, start.y, start.z));
            }
        } else if (start.y != end.y) {
            for (int i = start.y; i <= end.y; i++) {
                cubes.add(new Cube(start.x, i, start.z));
            }
        } else if (start.z != end.z) {
            for (int i = start.z; i <= end.z; i++) {
                cubes.add(new Cube(start.x, start.y, i));
            }
        } else {
            cubes.add(new Cube(start.x, start.y, start.z));
        }
    }
    public List<Cube> getCubes() { return this.cubes; }
    public String toString() { return this.cubes.toString() + "\n"; }
    // Drop a brick by one unit in the z direction.
    public void dropBrick() {
        for (Cube c : cubes) {
            if (c.z == 1)
                break;
            c.z -= 1;
        }
    }
    // Raise a brick by one unit in the z direction.
    public void raiseBrick() {
        for (Cube c : cubes) {
            c.z += 1;
        }
    }

    // Two bricks are equivalent if all of their inner cubes are equivalent.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brick brick = (Brick) o;
        return cubes.equals(brick.cubes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cubes);
    }
}

public class Day22_Sand_Slabs {
    // Comparator to sort the bricks by ascending z coordinate order.
    static class BrickComparator implements Comparator<Brick> {
        @Override
        public int compare(Brick a, Brick b) {
            if (a.start.z == b.start.z)
                return a.end.z - b.end.z;
            return a.start.z - b.start.z;
        }
    }

    public static void main(String[] args) {
        File file = new File("./inputs/day22/day22.txt");
        List<Brick> bricks = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split("~");
                String[] startTokens = tokens[0].split(",");
                int startX = Integer.parseInt(startTokens[0]);
                int startY = Integer.parseInt(startTokens[1]);
                int startZ = Integer.parseInt(startTokens[2]);
                Cube start = new Cube(startX, startY, startZ);

                String[] endTokens = tokens[1].split(",");
                int endX = Integer.parseInt(endTokens[0]);
                int endY = Integer.parseInt(endTokens[1]);
                int endZ = Integer.parseInt(endTokens[2]);
                Cube end = new Cube(endX, endY, endZ);

                bricks.add(new Brick(start, end));
            }

            // Sort the bricks by their z values.
            bricks.sort(new BrickComparator());

            // Fall all the bricks down until they stably rest on one another.
            List<Brick> brickStructure = constructBrickStructure(bricks);

            int part1 = part1(brickStructure);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(brickStructure);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given two bricks, returns true if they collide
    // (if at least one of their respective cubes are at the same location).
    // Returns false otherwise.
    private static boolean doBricksCollide(Brick a, Brick b) {
        for (Cube c1 : a.getCubes()) {
            for (Cube c2 : b.getCubes()) {
                if (c1.equals(c2)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Given a brick, returns true if it is on the ground (if it's at least one of its cubes has a z value is 1).
    // Returns false otherwise.
    private static boolean isBrickOnGround(Brick brick) {
        for (Cube c : brick.getCubes()) {
            if (c.z == 1)
                return true;
        }
        return false;
    }

    // Given a brick and a structure of stable bricks, drop that brick down as far as it can go before resting
    // upon another brick.
    // Note: This function is incredibly inefficient as we most likely don't need to compare a brick
    // with all other stable bricks. Future improvements will be to optimize this function
    // by way of a HashMap or storing other data that we can repeatedly look up or update instead.
    private static Brick brickFall(List<Brick> stableBricks, Brick brick) {
        Brick finalBrick = new Brick(brick.start, brick.end);

        // Keep going if the brick is still not on the ground.
        while (!isBrickOnGround(finalBrick)) {
            finalBrick.dropBrick(); // Drop the brick down one level.

            boolean brickCollision = false;

            // Compare the brick with all the stable bricks in our structure so far.
            for (int i = stableBricks.size() - 1; i >= 0; i--) {
                Brick stable = stableBricks.get(i);

                // If the two bricks collide, we know that we've dropped too far.
                // But we don't raise the brick just yet. We want to compare against other bricks
                // in the structure to find all the bricks that this brick would support and which support it.
                if (doBricksCollide(stable, finalBrick)) {
                    brickCollision = true;
                    finalBrick.supportedBy.add(stable);
                    stable.supports.add(finalBrick);
                }
            }

            // Once we've compared our brick against all the other stable bricks in the structure so far,
            // Then, knowing that we've collided, we can raise the brick and exit.
            if (brickCollision) {
                finalBrick.raiseBrick();
                break;
            }
        }

        return finalBrick;
    }

    // Given a brick list that is sorted in ascending order by the brick's z value,
    // drop each brick down from its starting height until it rests upon the ground or another brick.
    // Return the resulting brick structure as a list of fallen bricks.
    private static List<Brick> constructBrickStructure(List<Brick> bricks) {
        List<Brick> stableBricks = new ArrayList<>();

        for (Brick brick : bricks) {
            Brick fallenBrick = brickFall(stableBricks, brick);
            stableBricks.add(fallenBrick);
        }

        return stableBricks;
    }

    // Part 1: Given a fallen brick structure, the bricks that are safe to remove are:
    // 1) Bricks whose bricks they support are not solely supported by itself.
    // 2) Bricks that don't support any other bricks.
    // Return the number of such bricks.
    private static int part1(List<Brick> brickStructure) {
        Set<Brick> safeBricksToRemove = new HashSet<>();
        for (Brick b : brickStructure) {
            boolean brickReliesOnThisBrick = false;

            // For each brick, consider all the bricks that it supports.
            // If none of the bricks that it supports is solely supported by itself, then
            // that brick is safe to remove.
            for (Brick above : b.supports) {
                if (above.supportedBy.size() <= 1) {
                    brickReliesOnThisBrick = true;
                    break;
                }
            }

            if (!brickReliesOnThisBrick)
                safeBricksToRemove.add(b);

            // The safe bricks are also ones that don't support any bricks.
            if (b.supports.size() == 0) {
                safeBricksToRemove.add(b);
            }
        }

        return safeBricksToRemove.size();
    }

    // Given a brick, returns true if this brick falls. Returns false otherwise.
    // If all the bricks that support this brick is fallen, then this brick will also fall.
    private static boolean doesBrickFall(Brick brick) {
        boolean allFallen = true;
        for (Brick b : brick.supportedBy) {
            if (!b.fallen) {
                return false;
            }
        }
        return allFallen;
    }

    // Reset each brick's fallen status to be false.
    private static void resetFallenBricks(List<Brick> brickStructure) {
        for (Brick brick : brickStructure) {
            brick.fallen = false;
        }
    }

    // Returns the number of other bricks that fall when the given brick is disintegrated.
    // We achieve this by performing a breadth-first search (BFS) across the bricks
    // that a brick supports.
    private static int numBricksFallenByRemovingBrick(Brick brick) {
        Queue<Brick> queue = new LinkedList<>();
        brick.fallen = true; // Mark the brick as one in which we will disintegrate.
        queue.add(brick); // Add the starting brick (from which we start disintegrating bricks)
        int pathLength = 0;

        while (!queue.isEmpty()) {
            Brick b = queue.poll();

            // Add all the bricks that this brick supports to the queue.
            for (Brick above : b.supports) {
                queue.add(above);

                // If a supported brick falls, we will mark it as fallen if it hasn't already been marked
                // and increase our pathLength counter.
                if (doesBrickFall(above)) {
                    if (!above.fallen) {
                        above.fallen = true;
                        pathLength++;
                    }
                }
            }
        }

        return pathLength;
    }

    // Part 2: For each brick in our brick structure, attempt to remove that brick
    // and run a breadth-first search (BFS) across the bricks that that brick supports
    // to see which bricks will fall or not. Sum up all the ones that fall.
    private static int part2(List<Brick> brickStructure) {
        int sum = 0;

        // Iterate from the bottom of the structure to the top,
        // counting the number of bricks that fall if a given brick is removed/disintegrated.
        // We also reset all the brick fallen states between each iteration.
        for (Brick brick : brickStructure) {
            sum += numBricksFallenByRemovingBrick(brick);
            resetFallenBricks(brickStructure);
        }

        return sum;
    }
}