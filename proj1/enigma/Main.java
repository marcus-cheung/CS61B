package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Marcus Cheung
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        setUp(m, _input.nextLine());
        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            if (next.equals("")) {
                _output.println();
            } else if (next.charAt(0) == '*') {
                setUp(m, next);
            } else {
                printMessageLine(m.convert(next));
                _output.println();
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor r;
            String name = _config.next();
            String type = _config.next();
            String cycle = "";
            while (_config.hasNext("(\\([\\S]+\\))+")) {
                cycle += _config.next();
            }
            Permutation p = new Permutation(cycle, _alphabet);
            if (type.equals("N")) {
                r = new FixedRotor(name, p);
            } else if (type.equals("R")) {
                r = new Reflector(name, p);
            } else {
                r = new MovingRotor(name, p, type.substring(1));
            }
            return r;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner set = new Scanner(settings);
        String first = set.next();

        if (!first.equals("*")) {
            throw error("Not valid setting!");
        }

        String[] names = new String[M.numRotors()];
        for (int i = 0; i < M.numRotors(); i++) {
            names[i] = set.next();
        }
        M.insertRotors(names);
        String setting = set.next();


        String cycle = "";
        if (set.hasNext()) {
            String next = set.next();
            if (!next.equals("") && next.charAt(0) != '(') {
                M.setRingstellung(next);
            } else {
                cycle += next;
            }
        }
        M.setRotors(setting);

        while (set.hasNext()) {
            cycle += set.next();
        }
        M.setPlugboard(new Permutation(cycle, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg = msg.replaceAll("\\s", "");
        char[] a =  msg.toCharArray();
        for (int i = 0; i < a.length; i++) {
            _output.print(a[i]);
            if ((i + 1) % 5 == 0 && i < a.length - 1) {
                _output.print(' ');
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
