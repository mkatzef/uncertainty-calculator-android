package com.nooverlap314.uncertaintycalculator;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Class for performing typical math functions on combinations of uncertainties and normal values.
 *
 * @author Marc Katzef
 * @date 17 November 2016
 */
class UMath {

    /**
     * Produces a standard-format number by either calling the toStringFormatted function (for
     * uncertainties) or reducing accuracy of the normal value to 4s.f..
     * @param value a number that must be formatted.
     * @return the string containing the formatted form of the number.
     */
    static String formattedNumber(Number value) {
		String message;

		if (value instanceof Uncertainty) {
			message = ((Uncertainty) value).toStringFormatted();
		} else {
			if (value.doubleValue() == 0) {
				message = "0";
			} else {
				
				BigDecimal nomBD = new BigDecimal(value.doubleValue());
				nomBD = nomBD.round(new MathContext(4));
				
				int nomDP;
				double magnitude = Math.log10(Math.abs(nomBD.doubleValue()));
				nomDP = (int) Math.ceil(magnitude);
				if (magnitude == nomDP) //the border cases (0.1, 1, 10) should fall in the next bucket
					nomDP++;
				
				int exponent;
				
				if (nomDP > 0) {
					exponent = nomDP - ((nomDP-1)%3)-1;
				} else {
					exponent = nomDP - ((nomDP-1)%3)-4;
				}
				
				if (nomDP < 0 && (((nomDP-1) % 3) == 0))
					exponent += 3;
				
				if (exponent != 0) {
					nomBD = nomBD.multiply(new BigDecimal(Math.pow(10, -exponent)));
					
					nomBD = nomBD.round(new MathContext(4));
					
					message = nomBD.toPlainString() + " E" + exponent;
				} else
					message = nomBD.toPlainString();
			}
		}

		return message;
	}

    /**
     * Returns the appropriate integer of (0, 1) representing the cases where the input value is not
     * or is an uncertainty. Uses integers rather than boolean to match the two operand equivalent.
     * @param value an uncertainty or other number... that's what we're here to find out
     * @return a 1-bit number [value is uncertainty]
     */
	static int translateInputs(Number value) {
		int result = 0;
		if (value instanceof Uncertainty) {
			result += 1;
		}
		return result;
	}
	
	
	/**
	 * Returns the appropriate integer of (0, 1, 2, 3) representing the cases where
	 * the input values are (both not uncertainties, not an uncertainty and an uncertainty, an
     * uncertainty and not, and both uncertainties)
	 * @param value1 an uncertainty or other number... that's what we're here to find out
	 * @param value2 same as above
	 * @return a 2-bit number [value 2 is uncertainty][value1 is uncertainty]
	 */
	static int translateInputs(Number value1, Number value2) {
		int result = 0;
		if (value1 instanceof Uncertainty) {
			result += 1;
		}
		if (value2 instanceof Uncertainty) {
			result += 2;
		}
		return result;
	}
	
	
	/**
	 * Adds two numbers in a way that considers uncertainties.
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 * @return operand1 + operand2 with uncertainties
	 */
	static Number add(Number value1, Number value2) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value1, value2)) {
		case 0:
			result = value1.doubleValue() + value2.doubleValue();
			break;
			
		case 1:
			nominalValue = value1.doubleValue() + value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value1).getAbsoluteUncertainty();
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 2:
			nominalValue = value1.doubleValue() + value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value2).getAbsoluteUncertainty();
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 3:
			nominalValue = value1.doubleValue() + value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value1).getAbsoluteUncertainty() + ((Uncertainty) value2).getAbsoluteUncertainty();
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	

	/**
	 * Subtracts one number from another with either being an uncertainty.
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 * @return operand1 - operand2 with uncertainties
	 */
	static Number subtract(Number value1, Number value2) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value1, value2)) {
		case 0:
			result = value1.doubleValue() - value2.doubleValue();
			break;
			
		case 1:
			nominalValue = value1.doubleValue() - value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value1).getAbsoluteUncertainty();
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 2:
			nominalValue = value1.doubleValue() - value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value2).getAbsoluteUncertainty();
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 3:
			nominalValue = value1.doubleValue() - value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value1).getAbsoluteUncertainty() + ((Uncertainty) value2).getAbsoluteUncertainty();
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
		}
	
	
	/**
	 * Multiplication considering uncertainties.
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 * @return operand1 * operand2 with uncertainties
	 */
	static Number multiply(Number value1, Number value2) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value1, value2)) {
		case 0:
			result = value1.doubleValue() * value2.doubleValue();
			break;
			
		case 1:
			nominalValue = value1.doubleValue() * value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value1).getRatioUncertainty() * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 2:
			nominalValue = value1.doubleValue() * value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value2).getRatioUncertainty() * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 3:
			nominalValue = value1.doubleValue() * value2.doubleValue();
			double percentageUncertainty = ((Uncertainty) value1).getRatioUncertainty() + ((Uncertainty) value2).getRatioUncertainty();
			absoluteUncertainty =  percentageUncertainty * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Division considering uncertainties.
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 * @return operand1 / operand2 with uncertainties
	 */
	static Number divide(Number value1, Number value2) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value1, value2)) {
		case 0:
			result = value1.doubleValue() / value2.doubleValue();
			break;
			
		case 1:
			nominalValue = value1.doubleValue() / value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value1).getRatioUncertainty() * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 2:
			nominalValue = value1.doubleValue() / value2.doubleValue();
			absoluteUncertainty = ((Uncertainty) value2).getRatioUncertainty() * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 3:
			nominalValue = value1.doubleValue() / value2.doubleValue();
			double percentageUncertainty = ((Uncertainty) value1).getRatioUncertainty() + ((Uncertainty) value2).getRatioUncertainty();
			absoluteUncertainty =  percentageUncertainty * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Power considering uncertainties.
	 * @param value1 the first operand.
	 * @param value2 the second operand.
	 * @return operand1 ^ operand2 with uncertainties
	 */
	static Number pow(Number value1, Number value2) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value1, value2)) {
		case 0:
			result = Math.pow(value1.doubleValue(), value2.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.pow(value1.doubleValue(), value2.doubleValue());
			double percentageUncertainty = ((Uncertainty) value1).getRatioUncertainty() * Math.abs(value2.doubleValue());
			absoluteUncertainty = percentageUncertainty * nominalValue;
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 2:// brute force
			nominalValue = Math.pow(value1.doubleValue(), value2.doubleValue());
			absoluteUncertainty = Math.pow(value1.doubleValue(), value2.doubleValue() + ((Uncertainty)value2).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(absoluteUncertainty - nominalValue);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
			
		case 3:
			nominalValue = Math.pow(value1.doubleValue(), value2.doubleValue());
			absoluteUncertainty = Math.pow(value1.doubleValue() + ((Uncertainty)value1).getAbsoluteUncertainty(), value2.doubleValue() + ((Uncertainty)value2).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(absoluteUncertainty - nominalValue);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
			break;
		}
		
		return result;
	}
	
	
	/**
	 * Sine considering uncertainties.
	 * @param value the only operand.
	 * @return sin(operand) with uncertainties
	 */
	static Number sin(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.sin(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.sin(value.doubleValue());
			double bruteForceNominal = Math.sin(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Cosine considering uncertainties.
	 * @param value the only operand.
	 * @return cos(operand) with uncertainties
	 */
	static Number cos(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.cos(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.cos(value.doubleValue());
			double bruteForceNominal = Math.cos(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Tangent considering uncertainties.
	 * @param value the only operand.
	 * @return tan(operand) with uncertainties
	 */
	static Number tan(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.tan(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.tan(value.doubleValue());
			double bruteForceNominal = Math.tan(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	/**
	 * Checks to see if the uncertainty can comply with the brute force
	 * uncertainty method, and if so whether the absolute uncertainty 
	 * can be added to the nominal value, or must be subtracted from it.
	 * Addition case: 0
	 * Subtraction case: 1
	 * Error case: 2
     */
	private static int translateBruteForce (Uncertainty value, double domMin, double domMax) {
		double nominalValue = value.doubleValue();
		double absoluteUncertainty = value.getAbsoluteUncertainty();
		
		boolean outOfDomain = nominalValue > domMax || nominalValue < domMin;
		boolean outOfDomainPlus = (nominalValue + absoluteUncertainty) > domMax;
		boolean outOfDomainMinus = (nominalValue - absoluteUncertainty) < domMin;
		
		if (outOfDomain || (outOfDomainPlus && outOfDomainMinus)) {
			return 2;
		} else if (!outOfDomainPlus){ //First choice
			return 0;
		} else {
			return 1;
		}
	}
	
	
	/**
	 * Arcsine considering uncertainties.
	 * Domain: -1 to 1
     * @param value the only operand.
     * @return asin(operand) with uncertainties
	 */
	static Number asin(Number value) throws IllegalArgumentException{
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		int domMin = -1;
		int domMax = 1;

		switch (translateInputs(value)) {
		case 0:
			if ((value.doubleValue() > domMax) || (value.doubleValue() < domMin))
				throw new IllegalArgumentException("Invalid Inputs (asin)");
			result = Math.asin(value.doubleValue());
			break;
			
		case 1:
			Uncertainty castValue = (Uncertainty) value;
			
			nominalValue = castValue.doubleValue();
			absoluteUncertainty = castValue.getAbsoluteUncertainty();
			
			int whichBruteForce = translateBruteForce(castValue, domMin, domMax);
			
			double bruteForceNominal;
			
			switch (whichBruteForce) {
			case 0:
				bruteForceNominal = Math.asin(nominalValue + absoluteUncertainty);
				break;
			case 1:
				bruteForceNominal = Math.asin(nominalValue - absoluteUncertainty);
				break;
			default:
				throw new IllegalArgumentException("Invalid Inputs (asin)");
			}
			nominalValue = Math.asin(castValue.doubleValue());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Arccosine considering uncertainties.
     * @param value the only operand.
     * @return acos(operand) with uncertainties
	 */
	static Number acos(Number value) throws IllegalArgumentException {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		int domMin = -1;
		int domMax = 1;
		
		switch (translateInputs(value)) {
		case 0:
			if ((value.doubleValue() > domMax) || (value.doubleValue() < domMin))
				throw new IllegalArgumentException("Invalid Inputs (acos)");
			result = Math.acos(value.doubleValue());
			break;
			
		case 1:
			Uncertainty castValue = (Uncertainty) value;
			
			nominalValue = castValue.doubleValue();
			absoluteUncertainty = castValue.getAbsoluteUncertainty();
			
			int whichBruteForce = translateBruteForce(castValue, -1, 1);
			
			double bruteForceNominal;
			
			switch (whichBruteForce) {
			case 0:
				bruteForceNominal = Math.acos(nominalValue + absoluteUncertainty);
				break;
			case 1:
				bruteForceNominal = Math.acos(nominalValue - absoluteUncertainty);
				break;
			default:
				throw new IllegalArgumentException("Invalid Inputs (acos)");
			}
			nominalValue = Math.acos(castValue.doubleValue());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Arctan considering uncertainties.
     * @param value the only operand.
     * @return atan(operand) with uncertainties
	 */
	static Number atan(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.atan(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.atan(value.doubleValue());
			double bruteForceNominal = Math.atan(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Hyperbolic sine considering uncertainties.
     * @param value the only operand.
     * @return sinh(operand) with uncertainties
	 */
	static Number sinh(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.sinh(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.sinh(value.doubleValue());
			double bruteForceNominal = Math.sinh(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Hyperbolic cosine considering uncertainties.
     * @param value the only operand.
     * @return cosh(operand) with uncertainties
	 */
	static Number cosh(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.cosh(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.cosh(value.doubleValue());
			double bruteForceNominal = Math.cosh(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Hyperbolic tangent considering uncertainties.
     * @param value the only operand.
     * @return tanh(operand) with uncertainties
	 */
	static Number tanh(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.tanh(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.tanh(value.doubleValue());
			double bruteForceNominal = Math.tanh(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Logarithms considering uncertainties.
     * @param value the only operand.
     * @return log(operand) with uncertainties
	 */
	static Number log(Number value) throws IllegalArgumentException {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		int domMin = 0;
		
		switch (translateInputs(value)) {
		case 0:
			if (value.doubleValue() < domMin)
				throw new IllegalArgumentException("Invalid Inputs (log)");
			
			result = Math.log10(value.doubleValue());
			break;
			
		case 1:
			if (value.doubleValue() < 0)
				throw new IllegalArgumentException("Invalid Inputs (log)");
			
			nominalValue = Math.log10(value.doubleValue());
			double bruteForceNominal = Math.log10(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Natural logarithm considering uncertainties.
     * @param value the only operand.
     * @return ln(operand) with uncertainties
	 */
	static Number ln(Number value) throws IllegalArgumentException {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		int domMin = 0;
		
		switch (translateInputs(value)) {
		case 0:
			if (value.doubleValue() < domMin)
				throw new IllegalArgumentException("Invalid Inputs (ln)");
			
			result = Math.log(value.doubleValue());
			break;
			
		case 1:
			if (value.doubleValue() < 0)
				throw new IllegalArgumentException("Invalid Inputs (ln)");
			
			nominalValue = Math.log(value.doubleValue());
			double bruteForceNominal = Math.log(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}
	
	
	/**
	 * Exponential considering uncertainties.
     * @param value the only operand.
     * @return exp(operand) with uncertainties
	 */
	static Number exp(Number value) {
		Number result = 0.0d;
		double nominalValue;
		double absoluteUncertainty;
		
		switch (translateInputs(value)) {
		case 0:
			result = Math.exp(value.doubleValue());
			break;
			
		case 1:
			nominalValue = Math.exp(value.doubleValue());
			double bruteForceNominal = Math.exp(value.doubleValue() + ((Uncertainty) value).getAbsoluteUncertainty());
			absoluteUncertainty = Math.abs(nominalValue - bruteForceNominal);
			result = new Uncertainty(nominalValue, absoluteUncertainty);
		}
		
		return result;
	}	
}
