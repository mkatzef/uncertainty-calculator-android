package com.nooverlap314.uncertaintycalculator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UMathTest {
	Number a;
	Number b;
	Number c;
	Number d;
	Number e;
    Number f;
	Number result;

	private double tolerance = 0.00000000000001;
	
	@Before
	public void initialize() {
		a = new Uncertainty(1, 1);
		b = new Uncertainty(4, 3);
		c = 1.5d;
		d = -1.5f;
        e = new Uncertainty(0.5, 0.5);
        f = 0.75;
	}

	@Test
	public void testTranslateInputs() {
		assertEquals(0, UMath.translateInputs(c));
		assertEquals(1, UMath.translateInputs(a));
		assertEquals(0, UMath.translateInputs(c, c));
		assertEquals(1, UMath.translateInputs(a, c));
		assertEquals(2, UMath.translateInputs(c, a));
		assertEquals(3, UMath.translateInputs(a, b));
	}


	@Test
	public void testAdd() {
		//a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
		result = UMath.add(a, b); //two uncertainties
		assertTrue(result instanceof Uncertainty);
		assertEquals(5, result.doubleValue(), tolerance);
		assertEquals(4, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.add(a, c); //uncertainty and double
		assertTrue(result instanceof Uncertainty);
		assertEquals(2.5, result.doubleValue(), tolerance);
		assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.add(c, a); //double and uncertainty
		assertTrue(result instanceof Uncertainty);
		assertEquals(2.5, result.doubleValue(), tolerance);
		assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.add(c, d); //double and double
		assertFalse(result instanceof Uncertainty);
		assertEquals(0, result.doubleValue(), tolerance);
	}
	
	
	@Test
	public void testSubtract() {
		//a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
		result = UMath.subtract(a, b); //two uncertainties
		assertTrue(result instanceof Uncertainty);
		assertEquals(-3, result.doubleValue(), tolerance);
		assertEquals(4, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.subtract(a, c); //uncertainty and double
		assertTrue(result instanceof Uncertainty);
		assertEquals(-0.5, result.doubleValue(), tolerance);
		assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.subtract(c, a); //double and uncertainty
		assertTrue(result instanceof Uncertainty);
		assertEquals(0.5, result.doubleValue(), tolerance);
		assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.subtract(c, d); //double and double
		assertFalse(result instanceof Uncertainty);
		assertEquals(3, result.doubleValue(), tolerance);
	}
	
	
	@Test
	public void testMultiply() {
		//a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
		result = UMath.multiply(a, b); //two uncertainties
		assertTrue(result instanceof Uncertainty);
		assertEquals(4, result.doubleValue(), tolerance);
		assertEquals(7, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.multiply(a, c); //uncertainty and double
		assertTrue(result instanceof Uncertainty);
		assertEquals(1.5, result.doubleValue(), tolerance);
		assertEquals(1.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.multiply(c, a); //double and uncertainty
		assertTrue(result instanceof Uncertainty);
		assertEquals(1.5, result.doubleValue(), tolerance);
		assertEquals(1.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.multiply(c, d); //double and double
		assertFalse(result instanceof Uncertainty);
		assertEquals(-2.25, result.doubleValue(), tolerance);
	}
	
	
	@Test
	public void testDivide() {
		//a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
		result = UMath.divide(a, b); //two uncertainties
		assertTrue(result instanceof Uncertainty);
		assertEquals(0.25, result.doubleValue(), tolerance);
		assertEquals(1.75*0.25, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.divide(a, c); //uncertainty and double
		assertTrue(result instanceof Uncertainty);
		assertEquals(1.0/1.5, result.doubleValue(), tolerance);
		assertEquals(1.0/1.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.divide(c, a); //double and uncertainty
		assertTrue(result instanceof Uncertainty);
		assertEquals(1.5, result.doubleValue(), tolerance);
		assertEquals(1.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.divide(c, d); //double and double
		assertFalse(result instanceof Uncertainty);
		assertEquals(-1, result.doubleValue(), tolerance);
	}
	

	@Test
	public void testPower() throws IllegalArgumentException {
		//a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
		result = UMath.pow(a, b); //two uncertainties
		assertTrue(result instanceof Uncertainty);
		assertEquals(1, result.doubleValue(), tolerance);
		assertEquals(127, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.pow(a, c); //uncertainty and double
		assertTrue(result instanceof Uncertainty);
		assertEquals(1, result.doubleValue(), tolerance);
		assertEquals(1.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.pow(c, a); //double and uncertainty
		assertTrue(result instanceof Uncertainty);
		assertEquals(1.5, result.doubleValue(), tolerance);
		assertEquals(0.75, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		result = UMath.pow(c, d); //double and double
		assertFalse(result instanceof Uncertainty);
		assertEquals(0.5443310539518174, result.doubleValue(), tolerance);
	}


    @Test
    public void testSin() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.sin(a); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.8414709848078965, result.doubleValue(), tolerance);
        assertEquals(0.0678264420177852, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.sin(c); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.9974949866040544, result.doubleValue(), tolerance);
    }


    @Test
    public void testCos() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.cos(a); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.5403023058681398, result.doubleValue(), tolerance);
        assertEquals(0.9564491424152821, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.cos(c); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.0707372016677029, result.doubleValue(), tolerance);
    }

    @Test
    public void testTan() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.tan(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.5463024898437905, result.doubleValue(), tolerance);
        assertEquals(1.0111052348111118, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.tan(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.9315964599440725, result.doubleValue(), tolerance);
    }

    @Test
    public void testAsin() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.asin(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.5235987755982989, result.doubleValue(), tolerance);
        assertEquals(1.0471975511965976, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.asin(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.848062078981481, result.doubleValue(), tolerance);
    }

    @Test
    public void testAcos() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.acos(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(1.0471975511965979, result.doubleValue(), tolerance);
        assertEquals(1.0471975511965979, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.acos(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.7227342478134157, result.doubleValue(), tolerance);
    }

    @Test
    public void testAtan() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.atan(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.4636476090008061, result.doubleValue(), tolerance);
        assertEquals(0.3217505543966422, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.atan(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.6435011087932844, result.doubleValue(), tolerance);
    }

    @Test
    public void testSinh() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.sinh(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.5210953054937474, result.doubleValue(), tolerance);
        assertEquals(0.654105888150054, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.sinh(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.82231673193583, result.doubleValue(), tolerance);
    }

    @Test
    public void testCosh() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.cosh(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(1.1276259652063807, result.doubleValue(), tolerance);
        assertEquals(0.415454669608863, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.cosh(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(1.2946832846768448, result.doubleValue(), tolerance);
    }

    @Test
    public void testTanh() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.tanh(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(0.46211715726000974, result.doubleValue(), tolerance);
        assertEquals(0.2994769986957551, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.tanh(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(0.6351489523872873, result.doubleValue(), tolerance);
    }

    @Test
    public void testLog() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.log(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(-0.3010299956639812, result.doubleValue(), tolerance);
        assertEquals(0.3010299956639812, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.log(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(-0.12493873660829995, result.doubleValue(), tolerance);
    }

    @Test
    public void testLn() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.ln(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(-0.6931471805599453, result.doubleValue(), tolerance);
        assertEquals(0.6931471805599453, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.ln(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(-0.2876820724517809, result.doubleValue(), tolerance);
    }

    @Test
    public void testExp() throws IllegalArgumentException {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        result = UMath.exp(e); //an uncertainty
        assertTrue(result instanceof Uncertainty);
        assertEquals(1.6487212707001282, result.doubleValue(), tolerance);
        assertEquals(1.069560557758917, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

        result = UMath.exp(f); //a double
        assertFalse(result instanceof Uncertainty);
        assertEquals(2.117000016612675, result.doubleValue(), tolerance);
    }


    @Test
    public void testTranslateBruteForce() {
        //a U(1, 1), b U(4, 3), c 1.5d, d -1.5f;
        //TODO
    }

	@Test
	public void testFormattedNumber() {
		Double test1 = 10d;
		assertEquals("10", UMath.formattedNumber(test1));
		
		test1 = 100d;
		assertEquals("100", UMath.formattedNumber(test1));
		
		test1 = 1000d;
		assertEquals("1.000 E3", UMath.formattedNumber(test1));
		
		test1 = 1.04543;
		assertEquals("1.045", UMath.formattedNumber(test1));
		
		test1 = 12345678.9d;
		assertEquals("12.35 E6", UMath.formattedNumber(test1));
		
		test1 = 0.1;
		assertEquals("100.0 E-3", UMath.formattedNumber(test1));
		
		test1 = 0.000023423;
		assertEquals("23.42 E-6", UMath.formattedNumber(test1));
		
		test1 = 0d;
		assertEquals("0", UMath.formattedNumber(test1));
		
		test1 = -0.000023423;
		assertEquals("-23.42 E-6", UMath.formattedNumber(test1));
		
		test1 = 15.885E9;
		assertEquals("15.89 E9", UMath.formattedNumber(test1));
		
		test1 = 999.9;
		assertEquals("999.9", UMath.formattedNumber(test1));
		
		test1 = 999.95;
		assertEquals("1.000 E3", UMath.formattedNumber(test1));
		
		test1 = 999999.4;
		assertEquals("1.000 E6", UMath.formattedNumber(test1));
		
		test1 = 10.01;
		assertEquals("10.01", UMath.formattedNumber(test1));
	}
}
