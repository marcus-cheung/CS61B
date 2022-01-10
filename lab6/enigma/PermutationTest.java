package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @author
 */
public abstract class PermutationTest {

    /**
     * For this lab, you must use this to get a new Permutation,
     * the equivalent to:
     * new Permutation(cycles, alphabet)
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
    abstract Permutation getNewPermutation(String cycles, Alphabet alphabet);

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet(chars)
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
    abstract Alphabet getNewAlphabet(String chars);

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet()
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
    abstract Alphabet getNewAlphabet();

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    @Test
    public void testsize(){
        Alphabet a = getNewAlphabet();
        Permutation p = getNewPermutation("", a);
        assertEquals(26, p.size());

        Alphabet b = getNewAlphabet("");
        Permutation p2 = getNewPermutation("",b);
        assertEquals(0, p2.size());

        Alphabet c = getNewAlphabet("hilf");
        Permutation p3 = getNewPermutation("",c);
        assertEquals(4, p3.size());
    }


    @Test
    public void testintpermute(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = getNewAlphabet(alphabet);
        Permutation p = getNewPermutation("(bca) (defghijklmnopqrstuvwxy) (z)", a);
        assertEquals(1, p.permute(0));
        assertEquals(25, p.permute(25));
        assertEquals(0, p.permute(2));
    }

    @Test
    public void testintinv(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = getNewAlphabet(alphabet);
        Permutation p = getNewPermutation("(bca) (defghijklmnopqrstuvwxy) (z)", a);
        assertEquals(2, p.invert(0));
        assertEquals(25,p.invert(25));
        assertEquals(24,p.invert(3));
    }

    @Test
    public void testcharpermute(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = getNewAlphabet(alphabet);
        Permutation p = getNewPermutation("(bca) (defghijklmnopqrstuvwxy) (z)", a);
        assertEquals('c', p.permute('b'));
        assertEquals('z', p.permute('z'));
        assertEquals('b', p.permute('a'));

    }

    @Test
    public void testcharinv(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = getNewAlphabet(alphabet);
        Permutation p = getNewPermutation("(bca) (defghijklmnopqrstuvwxy) (z)", a);
        assertEquals('c',p.invert('a'));
        assertEquals('z',p.invert('z'));
        assertEquals('y',p.invert('d'));
    }

    @Test
    public void testAlphabet(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = getNewAlphabet(alphabet);
        Permutation p = getNewPermutation("(bca) (defghijklmnopqrstuvwxy) (z)", a);
        assertEquals(a, p.alphabet());

        Alphabet b = getNewAlphabet("");
        Permutation p2 = getNewPermutation("", b);
        assertEquals(b, p2.alphabet());
    }

    @Test
    public void testDerangement(){
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = getNewAlphabet(alphabet);
        Permutation p = getNewPermutation("(bca) (defghijklmnopqrstuvwxyz)", a);
        assertEquals(true, p.derangement());

        Alphabet b = getNewAlphabet(alphabet);
        Permutation p2 = getNewPermutation("", b);
        assertEquals(false, p2.derangement());
    }





}
