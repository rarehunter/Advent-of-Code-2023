import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

// Class that represents a subset of the number of cubes of each color
// that were revealed from the bag.
class CubeResults {
    private int red;
    private int green;
    private int blue;

    public CubeResults() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public void setRed(int red) { this.red = red; }
    public void setGreen(int green) { this.green = green; }
    public void setBlue(int blue) { this.blue = blue; }
    public int getRed() { return this.red; }
    public int getGreen() { return this.green; }
    public int getBlue() { return this.blue; }

    public String toString() {
        return "(R: " + this.red + ", G: " + this.green + ", B: " + this.blue + ")";
    }
}

public class Day2_Cube_Conundrum {
    public static void main(String[] args) {
        File file = new File("./inputs/day2/day2.txt");
        Map<Integer, List<CubeResults>> games = new HashMap<>();

        try {
            Scanner sc = new Scanner(file);

            // Parse the input.
            // The resulting HashMap will contain a mapping from the game number
            // to a list of cube results, where a cube result is the subsets of cubes
            // that were revealed from the bag (like 3 red, 5 green, 4 blue)
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                List<CubeResults> cubeResult = new ArrayList<>();

                String[] initialTokens = line.split(": ");
                int gameNum = Integer.parseInt(initialTokens[0].split(" ")[1]);
                String[] rightSideTokens = initialTokens[1].split("; ");

                for (String rightSideToken : rightSideTokens) {
                    CubeResults cr = new CubeResults();

                    String[] individualResults = rightSideToken.split(", ");
                    for (String individualResult : individualResults) {
                        String[] cubeParts = individualResult.split(" ");

                        if (cubeParts[1].equals("red")) {
                            cr.setRed(Integer.parseInt(cubeParts[0]));
                        }

                        if (cubeParts[1].equals("green")) {
                            cr.setGreen(Integer.parseInt(cubeParts[0]));
                        }

                        if (cubeParts[1].equals("blue")) {
                            cr.setBlue(Integer.parseInt(cubeParts[0]));
                        }
                    }

                    cubeResult.add(cr);
                }

                games.put(gameNum, cubeResult);
            }

            int part1 = part1(games);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(games);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1: Which games would have been possible if the bag contained only
    // 12 red cubes, 13 green cubes, and 14 blue cubes? For each game,
    // compares the cubes revealed with 12 red, 13 green, and 14 blue to see if that
    // games could be possible. If the number of cubes revealed in the input is greater than
    // that of the limit, then that game is not possible.
    private static int part1(Map<Integer, List<CubeResults>> games) {
        int sum = 0;
        for (Integer gameNum : games.keySet()) {
            sum += gameNum; // Start by assuming the game is possible, so we include it in the sum.

            List<CubeResults> results = games.get(gameNum);
            for (CubeResults cr : results) {
                // If we find that the game is not possible, then we remove it from the sum.
                if (cr.getRed() > 12 || cr.getGreen() > 13 || cr.getBlue() > 14) {
                    sum -= gameNum;
                    break;
                }
            }
        }
        return sum;
    }

    // Part 2: What is the fewest number of cubes of each color that
    // could have been in the bag to make the game possible? For each game,
    // determine the max of each color cube.
    private static int part2(Map<Integer, List<CubeResults>> games) {
        int sum = 0;
        for (Integer gameNum : games.keySet()) {
            int maxR = 0, maxG = 0, maxB = 0;
            List<CubeResults> results = games.get(gameNum);

            for (CubeResults cr : results) {
                maxR = Math.max(maxR, cr.getRed());
                maxG = Math.max(maxG, cr.getGreen());
                maxB = Math.max(maxB, cr.getBlue());
            }

            sum += maxR * maxG * maxB;
        }
        return sum;
    }
}