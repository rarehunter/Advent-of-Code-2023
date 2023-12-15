import java.io.File;
import java.io.IOException;
import java.util.*;

// Represents a row of springs which includes the row itself and a list of sizes of contiguous
// groups of damaged springs.
record SpringRow(String row, List<Integer> groupSizes) {
    public String getRow() {
        return this.row;
    }

    public List<Integer> getGroupSizes() {
        return this.groupSizes;
    }

    public String toString() {
        return this.row + " " + this.groupSizes;
    }
}

// Represents a row-sizes tuple which is used as key in a memoization dictionary.
class State {
    private final String row;
    private final List<Integer> groupSizes;

    public State(String row, List<Integer> groupSizes) {
        this.row = row;
        this.groupSizes = groupSizes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return row.equals(state.row) && groupSizes.equals(state.groupSizes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, groupSizes);
    }
}

public class Day12_Hot_Springs {
    public static void main(String[] args) {
        File file = new File("./inputs/day12/day12.txt");
        List<SpringRow> rows = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");

                List<Integer> groupSizes = new ArrayList<>();
                String[] sizes = tokens[1].split(",");
                for (String size : sizes)
                    groupSizes.add(Integer.parseInt(size));

                rows.add(new SpringRow(tokens[0], groupSizes));
            }

            long part1 = part1(rows);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(rows);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Recursive function that returns the number of arrangements of operational
    // and damaged springs given the row and a list sizes of contiguous blocks of damaged springs.
    // In part 2, we added the dictionary store in order to memoize previous calculations of known state.
    private static long count(String row, List<Integer> sizes, Map<State, Long> store)  {
        // If we've seen this row-sizes state before, we just look in our dictionary for the previously
        // calculated value.
        State state = new State(row, sizes);
        if (store.containsKey(state)) {
            return store.get(state);
        }

        // If we've reached the end of the row and we still have sizes to go through,
        // then this configuration is invalid. Otherwise, it is valid!
        if (row.isBlank()) {
            if (sizes.isEmpty()) return 1;
            else return 0;
        }

        long arrangements = 0;
        char firstCharacter = row.charAt(0);

        if (firstCharacter == '.') {
            // If we encounter a '.', then ignore it and keep checking.
            arrangements += count(row.substring(1), sizes, store);
        } else if (firstCharacter == '?') {
            // If we encounter a '?', then try both a '.' and a '#' for that character.
            String usingDot = "." + row.substring(1);
            String usingHash = "#" + row.substring(1);
            arrangements += count(usingDot, sizes, store) + count(usingHash, sizes, store);
        } else if (firstCharacter == '#') {
            // If we encounter a '#', check the first group size (say it is n)
            // and check if the next n characters can be that many #'s in a row.

            // If no more sizes left, but we clearly have another '#' in the row, then invalid.
            if (sizes.isEmpty())
                return 0;

            int n = sizes.get(0);

            // If we're trying to grab more off of the row than exists, then invalid.
            if (n > row.length()) {
                return 0;
            }

            // Pop the first element off the list.
            List<Integer> newSizes = new ArrayList<>();
            for (int i = 1; i < sizes.size(); i++) {
                newSizes.add(sizes.get(i));
            }

            // If the next n elements has a '.', then that's an invalid configuration.
            if (row.substring(0, n).contains(".")) {
                return 0;
            } else if (row.length() == n) {
                // If we've reached the end of the row string,
                // check to see if we have any more items left in the list.
                // (This will automatically be taken care of with one more recursion.
                arrangements += count("", newSizes, store);
            } else if (row.charAt(n) == '#') {
                // We also have to check if the nth character is not a # because otherwise the size of that block
                // would be n+1 and not n.
                return 0;
            } else {
                // If all the above checks pass, then we've successfully matched a contiguous block of #s so
                // we keep moving on.
                arrangements += count(row.substring(n + 1), newSizes, store);
            }
        }

        store.put(state, arrangements); // Memoize the arrangements we've calculated.
        return arrangements;
    }

    // Part 1: Calculate the arrangements for each row and sum them all up. Makes use
    // of a recursive function along with a dictionary for memoization (which was added as a result of part 2).
    private static long part1(List<SpringRow> rows) {
        long sum = 0;

        for (SpringRow row : rows) {
            sum += count(row.getRow(), row.getGroupSizes(), new HashMap<>());
        }

        return sum;
    }

    // Makes a new row consisting of five copies of itself separated by "?"s
    private static String unfoldRow(String row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(row);
            sb.append("?");
        }

        sb.append(row);
        return sb.toString();
    }

    // Make a new list of sizes consisting of five copies of itself.
    private static List<Integer> unfoldSizes(List<Integer> sizes) {
        List<Integer> newSizes = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            newSizes.addAll(sizes);
        }

        return newSizes;
    }

    // Part 2: Calculate the arrangements for a five-fold row and sizes list.
    // Makes use of a recursive function along with a dictionary for memoization.
    private static long part2(List<SpringRow> rows) {
        long sum = 0;

        for (SpringRow row : rows) {
            String newRow = unfoldRow(row.getRow());
            List<Integer> newSizes = unfoldSizes(row.getGroupSizes());

            sum += count(newRow, newSizes, new HashMap<>());
        }

        return sum;
    }
}