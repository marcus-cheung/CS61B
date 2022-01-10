import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.
        assertEquals(0, 0); */
        assertEquals(CompoundInterest.numYears(2021),0, 0.01);
        assertEquals(CompoundInterest.numYears(2022),1,0.01);
        assertEquals(CompoundInterest.numYears(2121),100,0.01);
    }

    @Test
    public void testFutureValue() {
        assertEquals(CompoundInterest.futureValue(100,10,2021),100,0.01);
        assertEquals(CompoundInterest.futureValue(100,100,2022),200,0.01);
    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(50, CompoundInterest.futureValueReal(100,0,2022,50), tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(16550, CompoundInterest.totalSavings(5000, 2023, 10), tolerance);
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(15572, CompoundInterest.totalSavingsReal(5000, 2023, 10, 3),tolerance);

    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
