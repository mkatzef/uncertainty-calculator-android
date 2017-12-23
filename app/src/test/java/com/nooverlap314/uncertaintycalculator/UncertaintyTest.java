package com.nooverlap314.uncertaintycalculator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class UncertaintyTest {
	private Number a;
    private Number b;
    private Number c;

	private double tolerance = 0.00000000000001;
	
	@Before
	public void initialize() {
		a = 1.0d;
		b = new Uncertainty(60.0d, 6.0d);
		c = new Uncertainty(0.0d, 1.23d);
	}
	
	
	@Test
	public void testdoubleValue() {
		assertEquals(1.0d, a.doubleValue(), tolerance);
		assertEquals(60.0d, b.doubleValue(), tolerance);
	}
	
	
	@Test
	public void testGetAbsoluteUncertainty() {
		assertEquals(1.23d, ((Uncertainty) c).getAbsoluteUncertainty(), tolerance);
		assertEquals(6.0d, ((Uncertainty) b).getAbsoluteUncertainty(), tolerance);
	}
	
	
	@Test
	public void testgetRatioUncertainty() {
		assertEquals(0.1d, ((Uncertainty) b).getRatioUncertainty(), tolerance);
		assertEquals(1.0d, ((Uncertainty) c).getRatioUncertainty(), tolerance);
	}
	
	
	@Test
	public void testToString() {
		assertEquals("1.0", a.toString());
		assertEquals("60.0 " + (char) 0xB1 + " 6.0", b.toString());
	}

	
	@Test
	public void testToStringFormatted() {
		Uncertainty test;
		test = new Uncertainty(1, 1);
		assertEquals("1 \u00B1 1", test.toStringFormatted());
		
		test = new Uncertainty(11, 10);
		assertEquals("10 \u00B1 10", test.toStringFormatted());
		
		test = new Uncertainty(11, 11);
		assertEquals("10 \u00B1 10", test.toStringFormatted());
		
		test = new Uncertainty(11.3, 10);
		assertEquals("10 \u00B1 10", test.toStringFormatted());
		
		test = new Uncertainty(113, 13.42);
		assertEquals("110 \u00B1 10", test.toStringFormatted());
		
		test = new Uncertainty(1135.2, 16.8);
		assertEquals("(1.14 \u00B1 0.02) E3", test.toStringFormatted());
		
		test = new Uncertainty(11.204, 0.0023);
		assertEquals("11.204 \u00B1 0.002", test.toStringFormatted());
		
		test = new Uncertainty(116234.4567888, 0.000344);
		assertEquals("(116.2344568 \u00B1 0.0000003) E3", test.toStringFormatted());
	
		test = new Uncertainty(0.00003456, 1.1);
		assertEquals("0 \u00B1 1", test.toStringFormatted());
		
		test = new Uncertainty(0.00003456, 0.00000012);
		assertEquals("(34.6 \u00B1 0.1) E-6", test.toStringFormatted());
		
		test = new Uncertainty(34.56, 0);
		assertEquals("34.56 \u00B1 0", test.toStringFormatted());
		
		test = new Uncertainty(0, 0);
		assertEquals("0.0 \u00B1 0", test.toStringFormatted());
		
		test = new Uncertainty(0.002, 0.009);
		assertEquals("(2 \u00B1 9) E-3", test.toStringFormatted());
		
		test = new Uncertainty(0.0002, 0.0009);
		assertEquals("(200 \u00B1 900) E-6", test.toStringFormatted());

		test = new Uncertainty(0.0014, 0.001);
		assertEquals("(1 \u00B1 1) E-3", test.toStringFormatted());

		test = new Uncertainty(0.0015, 0.001);
		assertEquals("(2 \u00B1 1) E-3", test.toStringFormatted());
	}
}
