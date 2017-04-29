package com.nooverlap314.uncertaintycalculator;

import static org.junit.Assert.*;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class CalculatorTest {

	private String testString;
	private double tolerance = 0.0000000000001;
	
	@Before
	public void initialize() {
		testString = "-a+b/-c";
	}
	
	@Test
	public void testGetOperators() throws Exception {
		ArrayList<String> result = Calculator.getOperators(testString);
		assertEquals("+", result.get(0));
		assertEquals("/", result.get(1));

        testString = "2(A)";
        result = Calculator.getOperators(testString);
        assertEquals("*", result.get(0));

		String eqn = "(1(1))";
		result = Calculator.getOperators(eqn);
		assertEquals(0, result.size());

		eqn = "1(1)";
		result = Calculator.getOperators(eqn);
		assertEquals("*", result.get(0));
	}
	
	
	@Test
	public void testGetOperands() { //ensure "log(" and ")" are part of the operand.
		ArrayList<String> result = Calculator.getOperands(testString);
		assertEquals("-a", result.get(0));
		assertEquals("b", result.get(1));
		assertEquals("-c", result.get(2));

        testString = "2(A)";
        result = Calculator.getOperands(testString);
        assertEquals("2", result.get(0));
        assertEquals("A", result.get(1));

		String eqn = "(1(1))";
		result = Calculator.getOperands(eqn);
		assertEquals("1(1)", result.get(0));

		eqn = "1(1)";
		result = Calculator.getOperands(eqn);
		assertEquals("1", result.get(0));
		assertEquals("1", result.get(1));

		eqn = "1/-1";
		result = Calculator.getOperands(eqn);
		assertEquals("1", result.get(0));
		assertEquals("-1", result.get(1));

		eqn = "1/+1";
		result = Calculator.getOperands(eqn);
		assertEquals("1", result.get(0));
		assertEquals("1", result.get(1));
	}
	
	
	@Test
	public void testEquationProcessor() throws Exception{
		String eqn = "1";
		Number result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, (double) result, tolerance);
		
		eqn = "1±1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, result.doubleValue(), tolerance);
		assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "1±1/2";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0.5, result.doubleValue(), tolerance);
		assertEquals(0.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "1±(1/2)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, result.doubleValue(), tolerance);
		assertEquals(0.5, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "1±1+1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(2.0, result.doubleValue(), tolerance);
		assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "1±1+1±1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(2.0, result.doubleValue(), tolerance);
		assertEquals(2.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "((1-1)±1)+(1+1)±(1*2)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(2.0, result.doubleValue(), tolerance);
		assertEquals(3.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "-1±1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-1.0, result.doubleValue(), tolerance);
		assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "-(1±1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-1.0, result.doubleValue(), tolerance);
		assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "(1±1)/(1±0)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, result.doubleValue(), tolerance);
		assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "1/-1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-1.0, result.doubleValue(), tolerance);

		eqn = "1/+1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, result.doubleValue(), tolerance);

		eqn = "log(10±90)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, result.doubleValue(), tolerance);
		assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);
		
		eqn = "exp(ln(3.2))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(3.2, (double) result, tolerance);
		
		eqn = "log(1) + ln(1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0.0, (double) result, tolerance);
		
		eqn = "asin(sin(1.5±0))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.5, result.doubleValue(), tolerance);
		//assertEquals(1.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "(1±1)(1±1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1.0, result.doubleValue(), tolerance);
		assertEquals(2.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "2(1±1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(2.0, result.doubleValue(), tolerance);
		assertEquals(2.0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "(exp(ln(2(1±0.5))))^2";
		result = Calculator.equationProcessor(eqn);
		assertEquals(4.0, result.doubleValue(), tolerance);

		eqn = "0.0000000000145±0.000000000005 * 2";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0.000000000029, result.doubleValue(), tolerance);
		assertEquals(0.00000000001, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "0.0000000000145±0.000000000005 * 200000000000";
		result = Calculator.equationProcessor(eqn);
		assertEquals(2.9, result.doubleValue(), tolerance);
		assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);
		
		eqn = "(1±1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1, result.doubleValue(), tolerance);
		//assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);
		
		eqn = "sin((0±1))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0, result.doubleValue(), tolerance);
		//assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);
		
		eqn = "1±2";
		result = Calculator.equationProcessor(eqn);
		//System.out.println("Input:\t" + eqn);
		//System.out.println("Output:\t" + result);
		
		eqn = "-log(10±1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-1, result.doubleValue(), tolerance);
		//assertEquals(1, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);
		
		eqn = "log(1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0, (result).doubleValue(), tolerance);
		
		eqn = "(log(1))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0, (result).doubleValue(), tolerance);
		
		eqn = "((1))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1, (result).doubleValue(), tolerance);
		
		eqn = "((-1))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-1, (result).doubleValue(), tolerance);
		
		eqn = "1(1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1, (result).doubleValue(), tolerance);

		eqn = "(1(1))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1, (result).doubleValue(), tolerance);
		
		eqn = "(1)2";
		result = Calculator.equationProcessor(eqn);
		assertEquals(2, (result).doubleValue(), tolerance);
		
		eqn = "17.2E3±8201.3E1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(17200, (result).doubleValue(), tolerance);
		assertEquals(82013, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);
		
		eqn = "17.2E3±8201.3E-1";
		result = Calculator.equationProcessor(eqn);
		assertEquals(17200, (result).doubleValue(), tolerance);
		assertEquals(820.13, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "log(10\u00B10)/sqrt(4)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0.5, (result).doubleValue(), tolerance);
		assertEquals(0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "log(10\u00B10)/sqrt(4)*log(100\u00B10)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(1, (result).doubleValue(), tolerance);
		assertEquals(0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "log(10\u00B10)/-sqrt(4)*log(100\u00B10)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-1, (result).doubleValue(), tolerance);
		assertEquals(0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "1-2+1*2/1^(2+1)";
		result = Calculator.equationProcessor(eqn);
		assertEquals(-3, (result).doubleValue(), tolerance);
		//assertEquals(0, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "sqrt(10^log(10\u00B190))^2";
		result = Calculator.equationProcessor(eqn);
		assertEquals(10, (result).doubleValue(), tolerance);
		assertEquals(90, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

		eqn = "asin(sin(acos(cos(atan(tan(0.5\u00B10.25))))))";
		result = Calculator.equationProcessor(eqn);
		assertEquals(0.5, (result).doubleValue(), tolerance);
		assertEquals(0.25, ((Uncertainty) result).getAbsoluteUncertainty(), tolerance);

	}
	
	
	@Test
	public void testIsOnlyValidCharacters() {
		String eqn;
		eqn = "1\u00B12/4*C";
		assertTrue(Calculator.isOnlyValidCharacters(eqn));
		
		eqn = "1.023456789+-/*\u00B1\u03C0abcdefghijklmnopqrstuvwxyz()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		assertTrue(Calculator.isOnlyValidCharacters(eqn));
		
		eqn = "$1.023456789+-/*\u00B1abcdefghijklmnopqrstuvwxyz()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		assertFalse(Calculator.isOnlyValidCharacters(eqn));
		
		eqn = "1.023456789+-/*^ \u00B1abcdefghijklmnopqrstuv_wxyz()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		assertFalse(Calculator.isOnlyValidCharacters(eqn));
		
		eqn = "{";
		assertFalse(Calculator.isOnlyValidCharacters(eqn));
	}
	
	
	@Test
	public void testToFormula() throws Exception {
		String eqn;
		eqn = "1\u00B12/4*C";
		assertEquals("$({1\u00B12}/4)*C$", Calculator.toFormula(eqn));

		eqn = "exp(log(sin(1/2+1)/2))";
		assertEquals("$e^(log({sin((1/2)+1)}/2))$", Calculator.toFormula(eqn));

		eqn = "(1\u00B12)(3\u00B14)";
		assertEquals("$(1\u00B12)*(3\u00B14)$", Calculator.toFormula(eqn));
		
		eqn = "-1";
		assertEquals("$-1$", Calculator.toFormula(eqn));

		eqn = "((1\u00B11))";
		assertEquals("$1\u00B11$", Calculator.toFormula(eqn));

		eqn = "(1(1))";
		assertEquals("$1*1$", Calculator.toFormula(eqn));
		
		eqn = "12.67E6 \u00B1 4532345923E-3";
		assertEquals("$(12.67E6)\u00B1(4532345923E-3)$", Calculator.toFormula(eqn));
		
		eqn = "12.67E6/2";
		assertEquals("${12.67E6}/2$", Calculator.toFormula(eqn));

		eqn = "cos(1\u00B12)/sin(2)";
		assertEquals("${cos(1\u00B12)}/{sin(2)}$", Calculator.toFormula(eqn));

		eqn = "sin(1\u00B12)/sqrt(2)";
		assertEquals("${sin(1\u00B12)}/{√(2)}$", Calculator.toFormula(eqn));

		eqn = "Infinity \u00B1 NaN";
		assertEquals("${Infinity}±{NaN}$", Calculator.toFormula(eqn));

		eqn = "1 / Infinity \u00B1 NaN";
		assertEquals("$1/{{Infinity}±{NaN}}$", Calculator.toFormula(eqn));

		eqn = "1 / - {Infinity} \u00B1 NaN";
		assertEquals("$1/{(-{Infinity})±{NaN}}$", Calculator.toFormula(eqn));
/**
        eqn = "1 / (2A)";
        assertEquals("$1/{2*A}$", Calculator.toFormula(eqn));
**/
        eqn = "sqrt(2*9.81*(0.11\u00B10.002))"; //actual lab
		//System.out.println(Calculator.toFormula(eqn));
		//System.out.println(Calculator.equationProcessor(eqn));
	}

}
