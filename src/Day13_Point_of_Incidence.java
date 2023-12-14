import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

record IntTuple(int i1, int i2) {
    public int getI1() {
        return i1;
    }

    public int getI2() {
        return i2;
    }

    public String toString() {
        return "(" + this.i1 + ", " + this.i2 + ")";
    }
}

public class Day13_Point_of_Incidence {
    public static void main(String[] args) {
        File file = new File("./inputs/day13/day13.txt");
        List<char[][]> patterns = new ArrayList<>();
        List<IntTuple> whTuples = new ArrayList<>(); // Stores a width and height tuple for each grid

        try {
            Scanner sc = new Scanner(file);
            Scanner sc2 = new Scanner(file);

            int row = 0;
            int width = 0;

            // Iterate through the file two times.
            // For the first time, note down the widths and heights of each pattern.
            // For the second time, create 2d char arrays to store the patterns.
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (line.isBlank()) {
                    whTuples.add(new IntTuple(width, row));
                    row = 0;
                    width = 0;
                    continue;
                }

                width = line.length();
                row++;
            }

            whTuples.add(new IntTuple(width, row));

            int pattern = 0; // Keeps track of which pattern we're processing right now.
            char[][] grid = new char[whTuples.get(pattern).getI2()][whTuples.get(pattern).getI1()];
            int currRow = 0;
            while (sc2.hasNextLine()) {
                String line = sc2.nextLine();

                if (line.isBlank()) {
                    patterns.add(grid);
                    pattern++;
                    currRow = 0;
                    grid = new char[whTuples.get(pattern).getI2()][whTuples.get(pattern).getI1()];
                    continue;
                }

                for (int i = 0; i < line.length(); i++) {
                    grid[currRow][i] = line.charAt(i);
                }

                currRow++;
            }

            patterns.add(grid);

            int part1 = part1(patterns);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(patterns);
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

    // Return the number of rows above where the horizontal line of reflection is.
    private static int findHorizontalReflection(char[][] pattern) {
        int numRows = pattern.length;
        int numCols = pattern[0].length;

        // Try all possible lines of reflection.
        for (int i = 0; i < numRows - 1; i++) {
            int j = i + 1;

            int searchUntil;
            // If i is closer to 0 than j is to the end of the rows,
            // then we search until i hits 0. Otherwise, if j is closer to the end of the rows,
            // we search until j hits the end of the rows.
            if (i < numRows - 1 - j) {
                searchUntil = 0;
            } else {
                searchUntil = numRows - 1;
            }

            boolean noMatch = false;
            int top = i;
            int bottom = j;

            // Continuously check if rows that reflect across the proposed line of reflection match.
            // If so, move our pointers one space in either direction and check again.
            while(top >= searchUntil || bottom <= searchUntil) {
                for (int x = 0; x < numCols; x++) {
                    if (pattern[top][x] != pattern[bottom][x]) {
                        // If we're here, at least one character of the two rows we're comparing did not match.
                        // Therefore, this is not the line of reflection.
                        noMatch = true;
                        break;
                    }
                }

                top--;
                bottom++;

                // Break out of while loop if no match found.
                if (noMatch) {
                    break;
                }
            }

            // No match found using this line of reflection, so let's try another line of reflection.
            if (noMatch) {
                continue;
            }

            // If it's all matches, then we found a reflection
            // so return the number of rows above the reflection.
            return i + 1;
        }

        // If we're here, then we've tried all possible horizontal reflections and no match has been found.
        return -1;
    }

    // Return the number of columns to the left of where the vertical line of reflection is.
    private static int findVerticalReflection(char[][] pattern) {
        int numRows = pattern.length;
        int numCols = pattern[0].length;

        // Try all possible lines of reflection.
        for (int i = 0; i < numCols - 1; i++) {
            int j = i + 1;

            int searchUntil;
            // If i is closer to 0 than j is to the end of the columns,
            // then we search until i hits 0. Otherwise, if j is closer to the end of the columns,
            // we search until j hits the end of the columns.
            if (i < numCols - 1 - j) {
                searchUntil = 0;
            } else {
                searchUntil = numCols - 1;
            }

            boolean noMatch = false;
            int left = i;
            int right = j;

            // Continuously check if columns that reflect across the proposed line of reflection match.
            // If so, move our pointers one space in either direction and check again.
            while(left >= searchUntil || right <= searchUntil) {
                for (char[] chars : pattern) {
                    if (chars[left] != chars[right]) {
                        // If we're here, at least one character of the two columns we're comparing did not match.
                        // Therefore, this is not the line of reflection.
                        noMatch = true;
                        break;
                    }
                }

                left--;
                right++;

                // Break out of while loop if no match found.
                if (noMatch) {
                    break;
                }
            }

            // No match found using this line of reflection, so let's try another line of reflection.
            if (noMatch) {
                continue;
            }

            // If it's all matches, then we found a reflection
            // so return the number of columns to the left of the reflection.
            return i + 1;
        }

        // If we're here, then we've tried all possible horizontal reflections and no match has been found.
        return -1;
    }

    // Part 1: Try all possible lines of reflection in either direction and calculate the final
    // sum using the number of rows above a horizontal line of reflection or the number of columns
    // to the left of a vertical line of reflection.
    private static int part1(List<char[][]> patterns) {
        int sum = 0;

        for (char[][] pattern : patterns) {

            // Each pattern would only have one line of reflection either horizontally or vertically.
            // If we found a horizontal line of reflection, then we don't have to search for a vertical one.
            int rowsAbove = findHorizontalReflection(pattern);

            if (rowsAbove > 0) {
                sum += rowsAbove * 100;
                continue;
            }

            int columnsLeft = findVerticalReflection(pattern);
            if (columnsLeft > 0) {
                sum += columnsLeft;
            }
        }
        return sum;
    }

    // Given a 2d char array and a cell location, flip a '.' to a '#' and a '#' to a '.'
    private static void flipCharacter(char[][] pattern, int i, int j) {
        if (pattern[i][j] == '.')
            pattern[i][j] = '#';
        else if (pattern[i][j] == '#')
            pattern[i][j] = '.';
    }

    // Returns a list of the number of rows above all possible vertical reflection lines.
    private static List<Integer> findAllHorizontalReflections(char[][] pattern) {
        List<Integer> output = new ArrayList<>();
        int numRows = pattern.length;
        int numCols = pattern[0].length;

        // Try all possible lines of reflection.
        for (int i = 0; i < numRows - 1; i++) {
            int j = i + 1;

            int searchUntil;
            // If i is closer to 0 than j is to the end of the rows,
            // then we search until i hits 0. Otherwise, if j is closer to the end of the rows,
            // we search until j hits the end of the rows.
            if (i < numRows - 1 - j) {
                searchUntil = 0;
            } else {
                searchUntil = numRows - 1;
            }

            boolean noMatch = false;
            int top = i;
            int bottom = j;

            // Continuously check if rows that reflect across the proposed line of reflection match.
            // If so, move our pointers one space in either direction and check again.
            while(top >= searchUntil || bottom <= searchUntil) {
                for (int x = 0; x < numCols; x++) {
                    if (pattern[top][x] != pattern[bottom][x]) {
                        // If we're here, at least one character of the two rows we're comparing did not match.
                        // Therefore, this is not the line of reflection.
                        noMatch = true;
                        break;
                    }
                }

                top--;
                bottom++;

                // Break out of while loop if no match found.
                if (noMatch) {
                    break;
                }
            }

            // No match found using this line of reflection, so let's try another line of reflection.
            if (noMatch) {
                continue;
            }

            // If it's all matches, then we found a reflection
            // so add the number of rows above that reflection line to our output list.
            output.add(i + 1);
        }

        return output;
    }

    // Returns a list of the number of columns to the left of all possible vertical reflection lines.
    private static List<Integer> findAllVerticalReflections(char[][] pattern) {
        List<Integer> output = new ArrayList<>();
        int numCols = pattern[0].length;

        // Try all possible lines of reflection.
        for (int i = 0; i < numCols - 1; i++) {
            int j = i + 1;

            int searchUntil;
            // If i is closer to 0 than j is to the end of the columns,
            // then we search until i hits 0. Otherwise, if j is closer to the end of the columns,
            // we search until j hits the end of the columns.
            if (i < numCols - 1 - j) {
                searchUntil = 0;
            } else {
                searchUntil = numCols - 1;
            }

            boolean noMatch = false;
            int left = i;
            int right = j;

            // Continuously check if columns that reflect across the proposed line of reflection match.
            // If so, move our pointers one space in either direction and check again.
            while(left >= searchUntil || right <= searchUntil) {
                for (char[] chars : pattern) {
                    if (chars[left] != chars[right]) {
                        // If we're here, at least one character of the two columns we're comparing did not match.
                        // Therefore, this is not the line of reflection.
                        noMatch = true;
                        break;
                    }
                }

                left--;
                right++;

                // Break out of while loop if no match found.
                if (noMatch) {
                    break;
                }
            }

            // No match found using this line of reflection, so let's try another line of reflection.
            if (noMatch) {
                continue;
            }

            // If it's all matches, then we found a reflection
            // so add the number of columns to the left of that reflection line to our output list.
            output.add(i + 1);
        }

        return output;
    }

    // Part 2: To find the location of the smudge, iterate through all characters in the grid
    // and try flipping each one. First, start by computing where the original vertical and horizontal
    // lines of reflection are. Then, we recognize that when we flip a character, it's entirely possible
    // that there are multiple valid reflections present and we are concerned about a new one.
    // Therefore, we generate two helper functions that are similar to the two used in part 1 but instead
    // of returning a single line of reflection, we return a list of valid lines of reflection.
    // Remove the original reflection line from the returned lists. If any reflections remain,
    // it is guaranteed to be new. Calculate the final sum according to the same rules as in part 1.
    private static int part2(List<char[][]> patterns) {
        int sum = 0;

        for (char[][] pattern : patterns) {
            int originalRowsAbove = findHorizontalReflection(pattern);
            int originalColumnsLeft = findVerticalReflection(pattern);

            boolean newReflectionFound = false;
            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[0].length; j++) {
                    flipCharacter(pattern, i, j);

                    // Return a list of valid lines of reflection
                    List<Integer> newRowsAbove = findAllHorizontalReflections(pattern);
                    List<Integer> newColumnsLeft = findAllVerticalReflections(pattern);

                    // The new line of reflection must be different than the original one so remove
                    // the original one from the list.
                    newRowsAbove.remove(Integer.valueOf(originalRowsAbove));
                    newColumnsLeft.remove(Integer.valueOf(originalColumnsLeft));

                    if (!newRowsAbove.isEmpty()) {
                        sum += (100 * newRowsAbove.get(0));
                        newReflectionFound = true;
                        break;
                    } else if (!newColumnsLeft.isEmpty()) {
                        sum += newColumnsLeft.get(0);
                        newReflectionFound = true;
                        break;
                    }

                    // Reset the pattern to what it was and try another character.
                    flipCharacter(pattern, i, j);
                }

                if (newReflectionFound)
                    break;
            }
        }

        return sum;
    }
}