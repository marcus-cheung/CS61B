package enigma;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Marcus Cheung
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _charmap = new HashMap<Character, Character>();
        _invcharmap = new HashMap<Character, Character>();
        _intmap = new HashMap<Integer, Integer>();
        _invintmap = new HashMap<Integer, Integer>();
        setup();
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _intmap.get(p % _alphabet.size());
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _invintmap.get(c % _alphabet.size());
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw error("Character not in alphabet");
        }
        return _charmap.get(p);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw error("Character not in alphabet");
        }
        return _invcharmap.get(c);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _deranged;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycle of this permutation. */
    private String _cycles;

    /** charmap. */
    private HashMap<Character, Character> _charmap;

    /** invcharmap. */
    private HashMap<Character, Character> _invcharmap;

    /** intmap. */
    private HashMap<Integer, Integer> _intmap;

    /** invintmap. */
    private HashMap<Integer, Integer> _invintmap;

    /** Deranged bool. */
    private boolean _deranged = true;

    /** Setup of cycles. */
    private void setup() {
        String sanitized = _cycles.replaceAll("\\s", "");
        sanitized = sanitized.replaceAll("[(]", "");
        String[] splits = sanitized.split("\\)");
        char[][] cs = new char[splits.length][];
        for (int i = 0; i < splits.length; i++) {
            cs[i] = splits[i].toCharArray();
        }
        for (char[] s : cs) {
            for (int i = 0; i < s.length; i++) {
                char k = s[i];
                char v = s[(i + 1) % s.length];
                char inv = s[(i - 1 + s.length) % s.length];
                _charmap.put(k, v);
                _invcharmap.put(k, inv);
                _intmap.put(_alphabet.toInt(k), _alphabet.toInt(v));
                _invintmap.put(_alphabet.toInt(k), _alphabet.toInt(inv));
            }
        }

        String cycle = _cycles.replaceAll("\\s[()]", "");
        for (int i = 0; i < _alphabet.size(); i++) {
            char c = _alphabet.toChar(i);
            if (cycle.indexOf(c) == -1) {
                _deranged = false;
                _charmap.put(c, c);
                _invcharmap.put(c, c);
                int m = _alphabet.toInt(c);
                _intmap.put(m, m);
                _invintmap.put(m, m);
            }
        }

    }
}
