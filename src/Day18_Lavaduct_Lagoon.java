import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

enum DigDirection {
    UP, DOWN, RIGHT, LEFT
}

// Represents a vertex coordinate on the polygon resulting from the dig plan.
record DigVertex(long x, long y) { }

// Represents a step in the dig plan.
class DigStep {
    private final DigDirection direction;
    private final long meters;
    private final String color;

    public DigStep(DigDirection d, long meters, String color) {
        this.direction = d;
        this.meters = meters;
        this.color = color;
    }

    public DigStep(DigDirection d, long meters) {
        this.direction = d;
        this.meters = meters;
        this.color = "";
    }

    public DigDirection getDirection() { return this.direction; }
    public long getMeters() { return this.meters; }
    public String getColor() { return this.color; }

    public String toString() { return "(" + this.direction + ", " + this.meters + ", " + this.color + ")"; }
}

public class Day18_Lavaduct_Lagoon {
    public static void main(String[] args) {
        File file = new File("./inputs/day18/day18.txt");
        List<DigStep> digPlan = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");

                DigDirection dd = null;
                switch (tokens[0]) {
                    case "U" -> dd = DigDirection.UP;
                    case "D" -> dd = DigDirection.DOWN;
                    case "R" -> dd = DigDirection.RIGHT;
                    case "L" -> dd = DigDirection.LEFT;
                }

                int meters = Integer.parseInt(tokens[1]);

                String color = tokens[2].substring(2, tokens[2].length()-1);

                digPlan.add(new DigStep(dd, meters, color));
            }

            long part1 = part1(digPlan);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(digPlan);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // The shoelace formula gives the area of a polygon whose vertices are described by their
    // Cartesian coordinates in the plane
    private static long calculateArea(List<DigVertex> vertices) {
        long shoelaceSum = 0;
        long shoelaceDiff = 0;
        for (int i = 0; i < vertices.size(); i++) {
            DigVertex first = vertices.get(i);
            DigVertex second = vertices.get((i + 1) % vertices.size());
            shoelaceSum +=  first.x() * second.y();
            shoelaceDiff += first.y() * second.x();
        }

        return Math.abs(shoelaceSum - shoelaceDiff) / 2;
    }

    // Use the shoelace formula and Pick's theorem to calculate the total area of the
    // polygon traced by the dig plan.
    private static long calculateTotalArea(List<DigStep> digPlan) {
        List<DigVertex> vertices = new ArrayList<>(); // Stores the coordinates of the vertices of the polygon.
        DigVertex start = new DigVertex(0, 0);
        vertices.add(start);

        int perimeter = 0;
        DigVertex currentPoint = start;

        // Simulate the dig plan and calculate the coordinates of each vertex of the polygon as well
        // as its total perimeter.
        for (DigStep step : digPlan) {
            if (step.getDirection().equals(DigDirection.UP)) {
                currentPoint = new DigVertex(currentPoint.x() + step.getMeters(), currentPoint.y());
            } else if (step.getDirection().equals(DigDirection.DOWN)) {
                currentPoint = new DigVertex(currentPoint.x() - step.getMeters(), currentPoint.y());
            } else if (step.getDirection().equals(DigDirection.LEFT)) {
                currentPoint = new DigVertex(currentPoint.x(), currentPoint.y() - step.getMeters());
            } else if (step.getDirection().equals(DigDirection.RIGHT)) {
                currentPoint = new DigVertex(currentPoint.x(), currentPoint.y() + step.getMeters());
            }

            vertices.add(currentPoint);

            perimeter += step.getMeters();
        }

        // Use shoelace formula to calculate the area of the polygon given its vertices.
        long area = calculateArea(vertices);

        // Use Pick's theorem to find the interior integer points of the polygon given its area and perimeter.
        long interiorPoints = area + 1 - (perimeter / 2);

        // The total area of the dig is the perimeter and the interior points.
        return interiorPoints + perimeter;
    }

    // Part 1: Use the shoelace formula and Pick's theorem to calculate the total area of the
    // polygon traced by the dig plan.
    private static long part1(List<DigStep> digPlan) {
        return calculateTotalArea(digPlan);
    }

    // Part 2: Calculate the total area of the polygon traced by a new dig plan.
    // This new dig plan involves converting the color field of the original dig plan into a dig direction
    // and meters.
    private static long part2(List<DigStep> digPlan) {
        List<DigStep> newDigPlan = new ArrayList<>();

        // Generate a new dig plan by converting the first five characters of the color field
        // in the original dig plan from hex to decimal. Then, use the last character of the color field
        // in the original dig plan to inform what direction that dig is supposed to go.
        for (DigStep step : digPlan) {
            long newMeter = Long.parseLong(step.getColor().substring(0, 5), 16);
            DigDirection dd = switch (step.getColor().substring(5)) {
                case "0" -> DigDirection.RIGHT;
                case "1" -> DigDirection.DOWN;
                case "2" -> DigDirection.LEFT;
                case "3" -> DigDirection.UP;
                default -> null;
            };

            newDigPlan.add(new DigStep(dd, newMeter));
        }

        return calculateTotalArea(newDigPlan);
    }
}