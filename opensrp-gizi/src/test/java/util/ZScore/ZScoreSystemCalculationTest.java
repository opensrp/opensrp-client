package util.ZScore;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wildan on 3/7/18
 */
public class ZScoreSystemCalculationTest {
    @Test
    public void testCountWFA() throws Exception {
        double expectedResult;

        double result = new ZScoreSystemCalculation().countWFA(true, 4.9);
        assertEquals("", expectedResult, result);
    }

}