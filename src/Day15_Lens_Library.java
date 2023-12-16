import java.io.File;
import java.io.IOException;
import java.util.*;

// Represents a lens with a label and a focal length.
class Lens {
    private final String label;
    private int focalLength;

    public Lens(String label) {
        this.label = label;
    }

    public Lens(String label, int focalLength) {
        this.label = label;
        this.focalLength = focalLength;
    }

    public String getLabel() {
        return this.label;
    }

    public int getFocalLength() {
        return this.focalLength;
    }

    public void setFocalLength(int focalLength) { this.focalLength = focalLength; }

    public String toString() {
        return "[" + this.label + " " + this.focalLength + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lens lens = (Lens) o;
        return label.equals(lens.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}

public class Day15_Lens_Library {
    public static void main(String[] args) {
        File file = new File("./inputs/day15/day15.txt");
        List<String> steps = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(",");
                steps.addAll(Arrays.asList(tokens));
            }

            int part1 = part1(steps);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(steps);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Turns a string into a single number in the range of 0 to 255.
    private static int hash(String step) {
        long currentValue = 0L;

        for (int i = 0; i < step.length(); i++) {
            currentValue += (int)(step.charAt(i));
            currentValue *= 17;
            currentValue %= 256;
        }

        return (int)currentValue;
    }

    // Part 1: For each string step, hash it to a number and sum them all up.
    private static int part1(List<String> steps) {
        int sum = 0;

        for (String step : steps) {
            sum += hash(step);
        }

        return sum;
    }

    // Calculates the focusing power for all the lenses in all the boxes.
    // For each box, iterate through the LinkedList containing the lenses and calculate
    // each lens' power level and sum them all up.
    private static long focusingPower(LinkedList<Lens>[] boxes) {
        long sum = 0;
        for (int i = 0; i < boxes.length; i++) {
            LinkedList<Lens> box = boxes[i];

            if (box == null) continue;

            for (int l = 0; l < box.size(); l++) {
                long power = 1;
                power *= (i + 1); // One plus the box number
                power *= (l + 1); // slot number of the lens within the box
                power *= box.get(l).getFocalLength();
                sum += power;
            }
        }

        return sum;
    }

    // Part 2: For each string step, hash the step label and either insert, replace, or delete it from the list.
    // Calculate the power level for all the lenses and sum them all up.
    private static long part2(List<String> steps) {
        LinkedList<Lens>[] boxes = new LinkedList[256];

        for (String step : steps) {
            // If a step has a "=", create a new lens, hash the step label, and find the box
            // that it sends us to.
            if (step.contains("=")) {
                String[] tokens = step.split("=");
                int focalLength = Integer.parseInt(tokens[1]);
                Lens newLens = new Lens(tokens[0], focalLength);
                int boxIndex = hash(tokens[0]);
                LinkedList<Lens> ll = boxes[boxIndex];

                // If the box already has a LinkedList,
                // then we check to see if it already has a lens of the same label.
                // If it does, we replace it with the new focal length.
                // If it doesn't, then add the new lens to the end of the list.
                if (ll != null) {
                    if (ll.contains(newLens)) {
                        ll.get(ll.indexOf(newLens)).setFocalLength(focalLength);
                    } else {
                        ll.addLast(newLens);
                    }
                } else {
                    // If the box does not have a LinkedList,
                    // we initialize it and insert the new lens into it.
                    LinkedList<Lens> newLL = new LinkedList<>();
                    newLL.add(newLens);
                    boxes[boxIndex] = newLL;
                }
            } else if (step.contains("-")) {
                // If a step has a "-", hash the step label, find the box
                // that it sends us to and remove the lens with that step label from the box.
                String[] tokens = step.split("-");
                int boxIndex = hash(tokens[0]);
                LinkedList<Lens> ll = boxes[boxIndex];
                if (ll != null) { // A box may not have been initialized with a LinkedList yet.
                    ll.remove(new Lens(tokens[0]));
                }
            }
        }

        return focusingPower(boxes);
    }
}