package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Marcus Cheung
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testsize() {
        Alphabet a = new Alphabet();
        Permutation p = new Permutation("", a);
        assertEquals(26, p.size());

        Alphabet b = new Alphabet("");
        Permutation p2 = new Permutation("", b);
        assertEquals(0, p2.size());

        Alphabet c = new Alphabet("hilf");
        Permutation p3 = new Permutation("", c);
        assertEquals(4, p3.size());
    }


    @Test
    public void testintpermute() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = new Alphabet(alphabet);
        String cycle = "(bca) (defghijklmnopqrstuvwxy) (z)";
        Permutation p = new Permutation(cycle, a);
        assertEquals(1, p.permute(0));
        assertEquals(25, p.permute(25));
        assertEquals(0, p.permute(2));
    }

    @Test
    public void testintinv() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = new Alphabet(alphabet);
        String cycle = "(bca) (defghijklmnopqrstuvwxy) (z)";
        Permutation p = new Permutation(cycle, a);
        assertEquals(2, p.invert(0));
        assertEquals(25, p.invert(25));
        assertEquals(24, p.invert(3));
    }

    @Test
    public void testcharpermute() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = new Alphabet(alphabet);
        String cycle = "(bca) (defghijklmnopqrstuvwxy) (z)";
        Permutation p = new Permutation(cycle, a);
        assertEquals('c', p.permute('b'));
        assertEquals('z', p.permute('z'));
        assertEquals('b', p.permute('a'));

    }

    @Test
    public void testcharinv() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = new Alphabet(alphabet);
        String cycle = "(bca) (defghijklmnopqrstuvwxy) (z)";
        Permutation p = new Permutation(cycle, a);
        assertEquals('c', p.invert('a'));
        assertEquals('z', p.invert('z'));
        assertEquals('y', p.invert('d'));
    }

    @Test
    public void testAlphabet() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = new Alphabet(alphabet);
        String cycle = "(bca) (defghijklmnopqrstuvwxy) (z)";
        Permutation p = new Permutation(cycle, a);
        assertEquals(a, p.alphabet());

        Alphabet b = new Alphabet("");
        Permutation p2 = new Permutation("", b);
        assertEquals(b, p2.alphabet());
    }

    @Test
    public void testDerangement() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Alphabet a = new Alphabet(alphabet);
        String cycle = "(bca) (defghijklmnopqrstuvwxy) (z)";
        Permutation p = new Permutation(cycle, a);
        assertEquals(true, p.derangement());

        Alphabet b = new Alphabet(alphabet);
        Permutation p2 = new Permutation("", b);
        assertEquals(false, p2.derangement());
    }

}
