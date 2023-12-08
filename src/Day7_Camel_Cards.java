import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;

// Represents a hand of cards along with its associated bid amount.
record Hand(String cards, int bidAmount) {
    public String getCards() {
        return this.cards;
    }

    public int getBidAmount() {
        return this.bidAmount;
    }

    public String toString() {
        return "(" + this.cards + ", " + this.bidAmount + ")";
    }
}

// Associate a relative value to a hand type for easier comparison purposes.
enum HAND_TYPE {
    FIVE_OF_KIND(7),
    FOUR_OF_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1);

    private int value;

    HAND_TYPE(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


public class Day7_Camel_Cards {
    private static Map<Character, Integer> cardTypes;
    private static Map<Character, Integer> cardTypesWithJokers;

    // Used in part 1.
    static class HandComparator implements Comparator<Hand> {
        @Override
        public int compare(Hand a, Hand b) {
            int handTypeA = getHandType(a.getCards()).getValue();
            int handTypeB = getHandType(b.getCards()).getValue();

            // First, start by comparing hand types.
            if (handTypeA < handTypeB) {
                return -1;
            } else if (handTypeA > handTypeB) {
                return 1;
            } else {
                // If two hands are the same type, then we start comparing
                // the card values starting from the first card.
                for (int i = 0; i < 5; i++) {
                    char c1 = a.getCards().charAt(i);
                    char c2 = b.getCards().charAt(i);

                    if (cardTypes.get(c1) < cardTypes.get(c2)) {
                        return -1;
                    } else if (cardTypes.get(c1) > cardTypes.get(c2)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
    }

    // Used in part 2.
    static class HandComparatorWithJokers implements Comparator<Hand> {
        @Override
        public int compare(Hand a, Hand b) {
            int handTypeA = getHandTypeWithJokers(a.getCards()).getValue();
            int handTypeB = getHandTypeWithJokers(b.getCards()).getValue();

            // First, start by comparing hand types.
            if (handTypeA < handTypeB) {
                return -1;
            } else if (handTypeA > handTypeB) {
                return 1;
            } else {
                // If two hands are the same type, then we start comparing
                // the card values starting from the first card.
                for (int i = 0; i < 5; i++) {
                    char c1 = a.getCards().charAt(i);
                    char c2 = b.getCards().charAt(i);

                    if (cardTypesWithJokers.get(c1) < cardTypesWithJokers.get(c2)) {
                        return -1;
                    } else if (cardTypesWithJokers.get(c1) > cardTypesWithJokers.get(c2)) {
                        return 1;
                    }
                }
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        File file = new File("./inputs/day7/day7.txt");

        // Maps a character to its numerical value for easier comparison purposes.
        cardTypes = new HashMap<>();
        cardTypes.put('2', 2);
        cardTypes.put('3', 3);
        cardTypes.put('4', 4);
        cardTypes.put('5', 5);
        cardTypes.put('6', 6);
        cardTypes.put('7', 7);
        cardTypes.put('8', 8);
        cardTypes.put('9', 9);
        cardTypes.put('T', 10);
        cardTypes.put('J', 11);
        cardTypes.put('Q', 12);
        cardTypes.put('K', 13);
        cardTypes.put('A', 14);

        // Maps a character to its numerical value for easier comparison purposes.
        cardTypesWithJokers = new HashMap<>();
        cardTypesWithJokers.put('J', 1);
        cardTypesWithJokers.put('2', 2);
        cardTypesWithJokers.put('3', 3);
        cardTypesWithJokers.put('4', 4);
        cardTypesWithJokers.put('5', 5);
        cardTypesWithJokers.put('6', 6);
        cardTypesWithJokers.put('7', 7);
        cardTypesWithJokers.put('8', 8);
        cardTypesWithJokers.put('9', 9);
        cardTypesWithJokers.put('T', 10);
        cardTypesWithJokers.put('Q', 11);
        cardTypesWithJokers.put('K', 12);
        cardTypesWithJokers.put('A', 13);

        List<Hand> hands = new ArrayList<>();

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split(" ");
                hands.add(new Hand(tokens[0], Integer.parseInt(tokens[1])));
            }

            long part1 = part1(hands);
            System.out.println("Part 1 is: " + part1);

            long part2 = part2(hands);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Given a string of cards, return a map of its characters to their frequencies in the string.
    private static Map<Character, Integer> constructFrequencyMap(String cards) {
        Map<Character, Integer> frequency = new HashMap<>();

        for (int i = 0; i < cards.length(); i++) {
            char c = cards.charAt(i);
            if (frequency.containsKey(c)) {
                frequency.put(c, frequency.get(c) + 1);
            } else {
                frequency.put(c, 1);
            }
        }

        return frequency;
    }

    // Given the cards of a hand, return its hand type. Used in part 1.
    private static HAND_TYPE getHandType(String cards) {
        Map<Character, Integer> frequency = constructFrequencyMap(cards);

        if (frequency.size() == 1) {
            return HAND_TYPE.FIVE_OF_KIND;
        } else if (frequency.size() == 2) {
            if (frequency.containsValue(4) && frequency.containsValue(1)) {
                return HAND_TYPE.FOUR_OF_KIND;
            } else if (frequency.containsValue(3) && frequency.containsValue(2)) {
                return HAND_TYPE.FULL_HOUSE;
            }
        } else if (frequency.size() == 3) {
            if (frequency.containsValue(3) && frequency.containsValue(1)) {
                return HAND_TYPE.THREE_OF_KIND;
            } else if (frequency.containsValue(2) && frequency.containsValue(1)) {
                return HAND_TYPE.TWO_PAIR;
            }
        } else if (frequency.size() == 4) {
            return HAND_TYPE.ONE_PAIR;
        }

        return HAND_TYPE.HIGH_CARD; // When the map size is 5
    }

    private static long calculateBidAmount(List<Hand> hands) {
        long sum = 0L;
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = hands.get(i);
            sum += ((long) (i + 1) * hand.getBidAmount());
        }

        return sum;
    }

    // Part 1: Write a comparator that compares hands first by their types, then by the relative values
    // of the card values from left to right.
    // Sort the hands by that criteria and do some math to find the total bid amount.
    private static long part1(List<Hand> hands) {
        hands.sort(new HandComparator());
        return calculateBidAmount(hands);
    }

    // Returns the character that is the most frequently represented in the hand.
    // No jokers are present in the frequency map.
    private static Character getMostFrequentCharacter(Map<Character, Integer> frequency) {
        int maxFrequency = 0;

        // Start by finding the max value in the map.
        for (Character c : frequency.keySet()) {
            maxFrequency = Math.max(maxFrequency, frequency.get(c));
        }

        // Return the first character that matches the max value.
        for (Character c : frequency.keySet()) {
            if (frequency.get(c) == maxFrequency) {
                return c;
            }
        }

        // There could be all Jokers in the hand, meaning that the frequency map is empty.
        return null;
    }

    // Given the cards of a hand (incl. jokers), return its best hand type. Used in part 2.
    private static HAND_TYPE getHandTypeWithJokers(String cards) {
        Map<Character, Integer> frequency = constructFrequencyMap(cards);

        // If our hand has any jokers, find the best cards it should turn into in order to make the strongest hand.
        if (frequency.containsKey('J')) {
            int numJokers = frequency.get('J');
            frequency.remove('J');

            // no jokers in the frequency map at this time.
            Character mostFrequentCharacter = getMostFrequentCharacter(frequency);

            if (mostFrequentCharacter != null) {
                // Turn any jokers into the cards that are the most
                // frequently represented to make the hand even stronger.
                frequency.put(mostFrequentCharacter, frequency.get(mostFrequentCharacter) + numJokers);
            } else {
                // If our hand is all jokers, then the best hand it could be is a five of a kind.
                return HAND_TYPE.FIVE_OF_KIND;
            }
        }

        // After converting the jokers into the best cards they could be,
        // return the hand type that the hand is.
        if (frequency.size() == 1) {
            return HAND_TYPE.FIVE_OF_KIND;
        } else if (frequency.size() == 2) {
            if (frequency.containsValue(4) && frequency.containsValue(1)) {
                return HAND_TYPE.FOUR_OF_KIND;
            } else if (frequency.containsValue(3) && frequency.containsValue(2)) {
                return HAND_TYPE.FULL_HOUSE;
            }
        } else if (frequency.size() == 3) {
            if (frequency.containsValue(3) && frequency.containsValue(1)) {
                return HAND_TYPE.THREE_OF_KIND;
            } else if (frequency.containsValue(2) && frequency.containsValue(1)) {
                return HAND_TYPE.TWO_PAIR;
            }
        } else if (frequency.size() == 4) {
            return HAND_TYPE.ONE_PAIR;
        }

        return HAND_TYPE.HIGH_CARD; // When the map size is 5
    }

    // Part 2: Write a comparator that does the same thing as part 1 but takes into account joker cards.
    // We want the joker cards to act like whatever card would make the hand the strongest type possible.
    // Therefore, there is some extra logic to make jokers act as the most frequent values in the cards thereby
    // making the hand stronger. Some edge case handling (e.g. a hand of five jokers) and some math to return
    // the final bid amount.
    private static long part2(List<Hand> hands) {
        hands.sort(new HandComparatorWithJokers());
        return calculateBidAmount(hands);
    }
}