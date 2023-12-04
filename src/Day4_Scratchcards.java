import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

// Represents a scratch card. Each scratch card has a card number,
// a list of numbers that represent the winning numbers, and a list
// of numbers that represent that numbers that you have.
record ScratchCard(int cardNumber, List<Integer> winningNumbers, List<Integer> numbersYouHave) {
    public Integer getCardNumber() {
        return this.cardNumber;
    }

    public List<Integer> getWinningNumbers() {
        return this.winningNumbers;
    }

    public List<Integer> getNumbersYouHave() {
        return this.numbersYouHave;
    }
}

public class Day4_Scratchcards {
    public static void main(String[] args) {
        File file = new File("./inputs/day4/day4.txt");
        List<ScratchCard> scratchCards = new ArrayList<>();
        // Maps a card number to the number of instances of each card. Used in part 2.
        Map<Integer, Integer> totalScratchcards = new HashMap<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(":\\s+");
                int cardNumber = Integer.parseInt(tokens[0].split("\\s+")[1]);
                List<Integer> winningNumbers = new ArrayList<>();
                List<Integer> numbersYouHave = new ArrayList<>();

                String[] numberTokens = tokens[1].trim().split("\\s+\\|\\s+");

                String[] leftNumbers = numberTokens[0].trim().split("\\s+");
                String[] rightNumbers = numberTokens[1].trim().split("\\s+");
                for (String leftNum : leftNumbers) {
                    winningNumbers.add(Integer.parseInt(leftNum));
                }
                for (String rightNum : rightNumbers) {
                    numbersYouHave.add(Integer.parseInt(rightNum));
                }

                scratchCards.add(new ScratchCard(cardNumber, winningNumbers, numbersYouHave));
                totalScratchcards.put(cardNumber, 1); // Only one instance of each card right now.
            }

            int part1 = part1(scratchCards);
            System.out.println("Part 1 is: " + part1);

            int part2 = part2(scratchCards, totalScratchcards);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Returns the number of items that occur in both lists.
    private static int intersection(List<Integer> list1, List<Integer> list2) {
        int count = 0;
        for (Integer item : list1) {
            if (list2.contains(item)) {
                count++;
            }
        }

        return count;
    }

    // Part 1: Iterate through all the scratchcards, finding the number of items that occur in both lists.
    // Perform some exponentiation and sum it all up.
    private static int part1(List<ScratchCard> scratchCards) {
        int sum = 0;

        for (ScratchCard card : scratchCards) {
            int numCommon = intersection(card.getNumbersYouHave(), card.getWinningNumbers());
            sum += Math.pow(2, numCommon - 1);
        }

        return sum;
    }

    // Part 2: Store the instances of each card in a dictionary, initialized to 1 to start.
    // When we process each card, we look at how many instances of the card there are and update
    // the count of the next n cards by that many number of instances. Finally, sum up all the counts.
    private static int part2(List<ScratchCard> scratchCards, Map<Integer, Integer> totalScratchcards) {
        for (ScratchCard card : scratchCards) {
            int cardNumber = card.getCardNumber();
            int numCommon = intersection(card.getNumbersYouHave(), card.getWinningNumbers());
            int numInstances = totalScratchcards.get(cardNumber);

            for (int i = 0; i < numCommon; i++) {
                int instancesOfNextCard = totalScratchcards.get(cardNumber + i + 1);
                totalScratchcards.put(cardNumber + i + 1, instancesOfNextCard + numInstances);
            }
        }

        int sum = 0;
        for (Integer i : totalScratchcards.keySet()) {
            sum += totalScratchcards.get(i);
        }
        return sum;
    }
}