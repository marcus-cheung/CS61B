package enigma;

import net.sf.saxon.functions.ConstantFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Marcus Cheung
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[_numRotors];
        _plugboard = new Permutation("", alpha);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new Rotor[_numRotors];
        int i = 0;
        int moving = 0;
        for (String name:rotors) {
            boolean e = false;
            for (Rotor r:_allRotors) {
                if (r.name().equals(name)) {
                    for (Rotor other:_rotors) {
                        if (other == r) {
                            throw error("Duplicate rotor");
                        }
                    }
                    if (r.rotates()) {
                        moving++;
                    }
                    if (moving > numPawls()) {
                        throw error("Not enough pawls");
                    }
                    _rotors[i] = r;
                    i++;
                    e = true;
                }
            }
            if (!e) {
                throw error("Rotor name not found");
            }
            e = false;

        }
        if (!(_rotors[0] instanceof Reflector)) {
            throw error("Reflector in wrong place");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        char[] s = setting.toCharArray();
        if (s.length < _rotors.length - 1) {
            throw error("Setting too short");
        } else if (s.length > _rotors.length - 1) {
            throw error("Setting too long");
        }
        int i = 1;
        for (char c:s) {
            if (!_alphabet.contains(c)) {
                throw error("Bad character in wheel setting");
            }
            _rotors[i].set(c);
            i++;
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        c = _plugboard.permute(c);
        boolean ds = _rotors[_rotors.length - 1].atNotch();
        _rotors[_rotors.length - 1].advance();
        for (int i = _rotors.length - 2; i > 0; i--) {
            Rotor current = _rotors[i];
            if ((current.atNotch() && _rotors[i - 1].rotates()) || ds) {
                ds = current.atNotch();
                current.advance();
            } else {
                break;
            }
        }

        for (int i = _rotors.length - 1; i >= 0; i--) {
            c = _rotors[i].convertForward(c);
        }

        for (int i = 1; i < _rotors.length; i++) {
            c = _rotors[i].convertBackward(c);
        }
        c = _plugboard.invert(c);
        return c;

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (char c : msg.toCharArray()) {
            if (c == ' ') {
                result = result.concat(" ");
            } else {
                char conv = _alphabet.toChar(convert(_alphabet.toInt(c)));
                result = result.concat(String.valueOf(conv));
            }
        }
        return result;
    }

    /** Sets up Ringstellung.
     * @param rs Setting for rings
     * */
    void setRingstellung(String rs) {
        if (rs.length() < _rotors.length - 1) {
            throw error("Ring too short");
        } else if (rs.length() > _rotors.length - 1) {
            throw error("Ring too long");
        }
        int i = 1;
        for (char c:rs.toCharArray()) {
            _rotors[i].setRing(c);
            i++;
        }
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _pawls;

    /** all rotors. */
    private Collection<Rotor> _allRotors;

    /** rotors. */
    private Rotor[] _rotors;

    /** plugboard. */
    private Permutation _plugboard;


}
