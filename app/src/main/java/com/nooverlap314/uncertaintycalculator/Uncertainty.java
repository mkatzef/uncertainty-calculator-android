package com.nooverlap314.uncertaintycalculator;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Class for dealing with experimentally obtained data.
 * 
 * @author Marc Katzef
 * @date 17 November 2016
 */
class Uncertainty extends Number{
	private static final long serialVersionUID = 1L;
	private double nominalValue;
	private double absoluteUncertainty = 0.0d;

	
	Uncertainty(double nominalValue, double absoluteUncertainty){
		this.nominalValue = nominalValue;
		this.absoluteUncertainty = Math.abs(absoluteUncertainty);
	}


	double getAbsoluteUncertainty(){
		return absoluteUncertainty;
	}
	
	
	double getRatioUncertainty(){
		if (nominalValue == 0.0d)
			return 1.0d; //Any uncertainty (even 0) will be treated as 100%
		else
			return absoluteUncertainty / Math.abs(nominalValue);
	}
	
	
	/** To comply with the Number abstract class **/
	public int intValue() {
		return (int) nominalValue;
	}
	
	
	public long longValue() {
		return (long) nominalValue;
	}
	
	
	public float floatValue() {
		return (float) nominalValue;
	}
	
	
	public double doubleValue() {
		return nominalValue;
	}
	/** End compliance. **/

	
	/**
	 * Basic representation, shows all accuracy currently held.
	 */
	public String toString() {
		return String.valueOf(nominalValue) + " \u00B1 " + String.valueOf(absoluteUncertainty);
	}

	
	/**
	 * Fully formatted representation. (Absolute uncertainty 1s.f., nominal value same number of
	 * decimal places as the formatted absolute uncertainty.)
	 * @return the correctly formatted string representing the held information.
	 */
	String toStringFormatted(){
		String result = "";
		
		BigDecimal nomBD = new BigDecimal(nominalValue);
		BigDecimal absBD = new BigDecimal(absoluteUncertainty);
		
		int nomDP;
		if (nominalValue == 0.0d)
			nomDP = 0;
		else {
			double magnitude = Math.log10(Math.abs(nominalValue));
			nomDP = (int) Math.ceil(magnitude);
			if (magnitude == nomDP) //the border cases (0.1, 1, 10) should fall in the next bucket
				nomDP++;
		}
		
		int absDP;
		if (absoluteUncertainty == 0.0d) {
			return nominalValue + " \u00B1 0";
		}else{
			double magnitude = Math.log10(Math.abs(absoluteUncertainty));
			absDP = (int) Math.ceil(magnitude);
			if (magnitude == absDP) //the border cases (0.1, 1, 10) should fall in the next bucket
				absDP++;
		}
		
		absBD = absBD.round(new MathContext(1));
		
		int exponent;
		
		if (nomDP > 0) {
			exponent = nomDP - ((nomDP-1)%3)-1;
		} else {
			exponent = nomDP - ((nomDP-1)%3)-4;
		}
		
		if (nomDP < absDP) {
			nomBD = new BigDecimal(0.0f);
			exponent = 0;
		} else {
			if ((nomDP == absDP) && (nomDP < 0) && (((nomDP-1) % 3) == 0))
				exponent += 3;
			nomBD = nomBD.round(new MathContext(Math.abs(nomDP - absDP + 1)));
		}
		
		if (exponent != 0) {
			nomBD = nomBD.multiply(new BigDecimal(Math.pow(10, -exponent)));
			absBD = absBD.multiply(new BigDecimal(Math.pow(10, -exponent)));
			
			nomBD = nomBD.round(new MathContext(Math.abs(nomDP - absDP + 1)));
			absBD = absBD.round(new MathContext(1));
			
			result += "(" + nomBD.toPlainString() + " \u00B1 " + absBD.toPlainString() + ") E" + exponent;
		} else
			result += nomBD.toPlainString() + " \u00B1 " + absBD.toPlainString();
		
		return result;
	}
}
