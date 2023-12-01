import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day1_Trebuchet {
    public static void main(String[] args) {
        File file = new File("./inputs/day1/day1.example.txt");

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
            }

            int part1 = part1();
            System.out.println("Part 1 is: " + part1);

            int part2 = part2();
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Part 1:
    private static int part1() {
        return 0;
    }

    // Part 2:
    private static int part2() {
        return 0;
    }
}