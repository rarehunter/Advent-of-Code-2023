import java.io.File;
import java.io.IOException;
import java.util.*;


enum Pulse { LOW, HIGH }

record PulseTuple(int lowPulses, int highPulses) {}

class Module {
    private final String name;
    private final List<String> destinationModuleNames;

    public Module (String name, List<String> destinationModuleNames) {
        this.name = name;
        this.destinationModuleNames = destinationModuleNames;
    }

    public String getName() { return this.name; }
    public List<String> getDestinationModuleNames() { return this.destinationModuleNames; }
    public Pulse receivePulse(String name, Pulse pulse) { return null; }
    public void reset() {}
}

class Conjunction extends Module {
    private final Map<String, Pulse> pulses;

    public Conjunction(String name, List<String> destinationModuleNames) {
        super(name, destinationModuleNames);
        this.pulses = new HashMap<>();
    }

    // Default to remembering a low pulse for each input.
    public void initializePulseMap(String name) {
        pulses.put(name, Pulse.LOW);
    }

    // Remembers the type of the most recent pulse received from each of its input modules
    // When a pulse is received, the conjunction module first updates its memory for that input.
    // Then, if it remembers high pulses for all inputs, it sends a low pulse.
    // Otherwise, it sends a high pulse.
    public Pulse receivePulse(String name, Pulse pulse) {
        pulses.put(name, pulse);

        boolean allHighPulses = true;

        for (String s : pulses.keySet()) {
            if (pulses.get(s) == Pulse.LOW)
                allHighPulses = false;
        }

        if (allHighPulses) return Pulse.LOW;
        return Pulse.HIGH;
    }
}

class FlipFlop extends Module {
    private boolean on;

    public FlipFlop(String name, List<String> destinationModuleNames) {
        super(name, destinationModuleNames);
        this.on = false;
    }

    private void toggleStatus() { on = !on; }

    // If a flip-flop module receives a high pulse, it is ignored and nothing happens.
    // If a flip-flop module receives a low pulse, it flips between on and off.
    // If it was off, it turns on and sends a high pulse.
    // If it was on, it turns off and sends a low pulse.
    public Pulse receivePulse(String name, Pulse pulse) {
        //System.out.println("Flip flop " + name + " received pulse: " + pulse);
        if (pulse == Pulse.HIGH) return null;
        if (on) { toggleStatus(); return Pulse.LOW; }
        else { toggleStatus(); return Pulse.HIGH; }
    }
    public void reset() { on = false; }
}

class Broadcaster extends Module {
    public Broadcaster(List<String> destinationModuleNames) {
        super("broadcaster", destinationModuleNames);
    }

    // The broadcast module sends the same pulse to all of its destination modules.
    public Pulse receivePulse(String name, Pulse pulse) { return pulse; }
    public String toString() { return super.getDestinationModuleNames().toString(); }
}

// A tuple representing a module, the pulse that is sent to it, and the sender's name.
class PulseState {
    public Module module;
    public Pulse pulse;
    public String pulseSender;

    public PulseState(Module module, Pulse pulse, String pulseSender) {
        this.module = module;
        this.pulse = pulse;
        this.pulseSender = pulseSender;
    }

    public String toString() { return "(" + module + ", " + pulse + ", " + pulseSender + ")"; }
}

public class Day20_Pulse_Propagation {
    public static void main(String[] args) {
        File file = new File("./inputs/day20/day20.txt");
        Map<String, Module> moduleLookup = new HashMap<>();
        List<Conjunction> conjunctions = new ArrayList<>();
        Broadcaster broadcaster = null;

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String label = line.split(" -> ")[0];
                String[] destinations = line.split(" -> ")[1].split(", ");

                // Parse a broadcaster module
                if (label.contains("broadcaster")) {
                    broadcaster = new Broadcaster(List.of(destinations));
                    continue;
                }

                // Parse a flip-flop module
                if (label.charAt(0) == '%') {
                    FlipFlop ff = new FlipFlop(label.substring(1), List.of(destinations));
                    moduleLookup.put(label.substring(1), ff);
                    continue;
                }

                // Parse a conjunction module
                if (label.charAt(0) == '&') {
                    Conjunction con = new Conjunction(label.substring(1), List.of(destinations));
                    moduleLookup.put(label.substring(1), con);
                    conjunctions.add(con);
                }
            }

            // For each conjunction module, build up a list of its input modules.
            for (Conjunction con : conjunctions) {
                for (String name : moduleLookup.keySet()) {
                    Module m = moduleLookup.get(name);
                    // If this module's destination is the conjunction module,
                    // record this module as an input to the conjunction module.
                    if (m.getDestinationModuleNames().contains(con.getName())) {
                        con.initializePulseMap(m.getName());
                    }
                }
            }

            long part1 = part1(moduleLookup, broadcaster);
            System.out.println("Part 1 is: " + part1);

            // Reset all modules to their starting state before performing part 2.
            for (String name : moduleLookup.keySet()) {
                moduleLookup.get(name).reset();
            }

            long part2 = part2(moduleLookup, broadcaster);
            System.out.println("Part 2 is: " + part2);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Handles a pulse sent by the given module. Returns a tuple of the counts of the low and high pulses.
    private static PulseTuple pulse(Map<String, Module> moduleLookup, Module module) {
        Queue<PulseState> queue = new LinkedList<>();
        // Start with a pulse to the broadcaster module whose sender is the button module.
        queue.add(new PulseState(module, Pulse.LOW, "button"));
        int lowPulses = 0;
        int highPulses = 0;

        while (!queue.isEmpty()) {
            PulseState state = queue.poll();

            // We could be sending to a module that doesn't exist or that doesn't
            // have any receive behavior so let's just move on.
            if (state.module == null)
                continue;

            // Each module knows what to do when they receive a pulse.
            Pulse newPulse = state.module.receivePulse(state.pulseSender, state.pulse);

            // If no pulse is returned, we move on.
            // This can only happen if a flip-flop module receives a high pulse.
            if (newPulse == null)
                continue;

            for (String child : state.module.getDestinationModuleNames()) {
                queue.add(new PulseState(moduleLookup.get(child), newPulse, state.module.getName()));

                // Keep track of our pulse counts.
                if (newPulse == Pulse.LOW)
                    lowPulses++;
                else
                    highPulses++;
            }
        }

        return new PulseTuple (lowPulses, highPulses);
    }

    // Part 1: Simulate 1000 button pushes through the network
    // and return how many low and high pulses were triggered.
    private static long part1(Map<String, Module> moduleLookup, Broadcaster broadcaster) {
        int low = 0;
        int high = 0;
        for (int i = 0; i < 1000; i++) {
            // Send one button push to the broadcaster module.
            PulseTuple pulseCount = pulse(moduleLookup, broadcaster);
            low += pulseCount.lowPulses() + 1; // Account for the low pulse of the button module itself.
            high += pulseCount.highPulses() ;
        }

        return (long) high * low;
    }

    // Find the least common multiple (LCM) of a and b.
    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    // Find the least common multiple (LCM) of a list of inputs.
    private static long lcm(List<Integer> input) {
        long result = input.get(0);
        for(int i = 1; i < input.size(); i++) {
            result = lcm(result, input.get(i));
        }
        return result;
    }

    // Find the greatest common divisor (GCD) between a and b.
    // This implements the Euclidean method of repeated modulo.
    private static long gcd(long a, long b) {
        while (b > 0) {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    // Keep pulsing until we've calculated the cycle lengths for all four feeder nodes.
    // The majority of this code is the same as the 'pulse' method above.
    // The difference is the recording of cycle length for each of the four feeder nodes.
    private static Map<String, Integer> pulseToFeederNodes(Map<String, Module> moduleLookup,
                                                  Broadcaster broadcaster,
                                                  List<String> feederNodes) {
        Map<String, Integer> modules = new HashMap<>();

        int presses = 0;

        // Keep running into we've calculated the cycle lengths for all four subgraphs.
        while (true) {
            presses++;
            Queue<PulseState> queue = new LinkedList<>();
            // Start with a pulse to the broadcaster module whose sender is the button module.
            queue.add(new PulseState(broadcaster, Pulse.LOW, "button"));

            while (!queue.isEmpty()) {
                PulseState state = queue.poll();

                // We could be sending to a module that doesn't exist or that doesn't
                // have any receive behavior so let's just move on.
                if (state.module == null)
                    continue;

                // Test when one of the feeder modules (the modules that feed into the module that feed into rx)
                // receive a low pulse and record the number of presses it took to achieve that.
                // In order for rx to receive a low pulse, module gq needs to send a low pulse. Because
                // gq is a conjunction module, all of its input modules must be high in order for it to send a low.
                // In order for all its input modules to send a high pulse,
                // then it must receive at least one low pulse.
                String moduleName = state.module.getName();
                if (feederNodes.contains(moduleName) && state.pulse == Pulse.LOW) {
                    if (!modules.containsKey(moduleName)) {
                        modules.put(moduleName, presses);
                    }

                    // Once we've found all four feeder modules, return the hashmap.
                    if (modules.size() == 4) {
                        return modules;
                    }
                }

                // Each module knows what to do when they receive a pulse.
                Pulse newPulse = state.module.receivePulse(state.pulseSender, state.pulse);

                // If no pulse is returned, we move on.
                // This can only happen if a flip-flop module receives a high pulse.
                if (newPulse == null)
                    continue;

                for (String child : state.module.getDestinationModuleNames()) {
                    queue.add(new PulseState(moduleLookup.get(child), newPulse, state.module.getName()));
                }
            }
        }
    }

    // Part 2: Find the minimum number of button presses needed to send a low pulse to the rx module.
    // Conjunction module gq is the only module connected to rx. There are four conjunction modules that feed into
    // module gq. Therefore, in order for a low pulse to be sent to rx, the four conjunction modules that
    // feed into gq must all be set to high. In order for them all to be set to high, they must all receive a low pulse.
    // The approach here is to recognize that these four conjunction modules are four sub-graphs
    // each with their own cycle lengths. At some point in the button pressing process, these four modules will receive
    // a low pulse, but we don't know when that will all line up. Therefore, we can calculate the cycle lengths
    // for each of these sub-graphs and take the least common multiple (LCM) of these cycle lengths in order to find
    // the first button press in which all four modules receive a low, thereby sending a low to rx.
    private static long part2(Map<String, Module> moduleLookup, Broadcaster broadcaster) {
        List<String> feederNodes = Arrays.asList("km", "qs", "xj", "kz");

        // Calculate the cycle lengths of all four feeder modules.
        Map<String, Integer> modules = pulseToFeederNodes(moduleLookup, broadcaster, feederNodes);

        List<Integer> cycleLengths = new ArrayList<>();

        for (String name : modules.keySet()) {
            cycleLengths.add(modules.get(name));
        }

        // Return the least common multiple (LCM) of the four cycle lengths to find the first cycle
        // that all four modules are set to high.
        return lcm(cycleLengths);
    }
}