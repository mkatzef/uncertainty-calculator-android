package com.nooverlap314.uncertaintycalculator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for dealing with (string) equations.
 *
 * @author Marc Katzef
 * @date 17 November 2016
 */
class Calculator {
	private static final String SYMBOL_PI = "\u03C0";
	private static final String SYMBOL_PM = "\u00B1";
	private static final ArrayList<String> operators = new ArrayList<>(Arrays.asList(SYMBOL_PM, "^",
			"/", "*", "+", "-"));
	private static final ArrayList<String> specialOperators = new ArrayList<>(Arrays.asList("sqrt",
			"sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "log", "ln", "exp"));

	/**
	 * Parses an equation to find the operators acting on the highest level of the equation
	 * (everything happening between operands that are outside of brackets).
	 * @param input - a string made up of only brackets, operators, and numbers
	 * @return presentOperators - the array-list of operators at the highest level of the equation.
	 * @throws Exception - if anything goes wrong (infinite loop, invalid number etc.)
     */
	static ArrayList<String> getOperators(String input) throws Exception{
		ArrayList<String> presentOperators = new ArrayList<>();
		int length = input.length();
		int bracketLevel = 0;
		if (length == 0)
			throw new Exception("Empty operand:" + presentOperators.size());
		
		if (input.charAt(0) == '(')
			bracketLevel++;
		
		for (int i = 1; i < length; i++) {
			char digit = input.charAt(i);
			if (digit == '(') {

				boolean wereSpecOps = false;
				if (bracketLevel == 0) { //in the event of: "[specialOperator]("
					for (String specOp : specialOperators) {
						if (!((i - specOp.length()) < 0) &&
								(input.substring(i - specOp.length(), i).equals(specOp))) {
							if (((i - specOp.length() - 1) >= 0) &&
									(input.charAt(i - specOp.length() - 1) == 'a')) //don't find sines within asines etc
								continue;
							
							if (((i - specOp.length() - 1) >= 0) &&
									(input.charAt(i - specOp.length() - 1) == '-')) { //negative specOp, but not [operand] - specOp(x)
								if (((i - specOp.length() - 2) < 0) || 
										operators.contains(Character.toString(input.charAt(i - specOp.length() - 2))) ||
										(input.charAt(i - specOp.length() - 2) == '('))
								    specOp = "-" + specOp;
							}
								
							presentOperators.add(specOp);	
							wereSpecOps = true;
						}
					}
					if (!wereSpecOps && !operators.contains(Character.toString(input.charAt(i-1)))) {
						presentOperators.add("*");
					}
				}
				
				bracketLevel++;
				
			} else if (digit == ')') {
				bracketLevel--;
				//if next is applicable, and is not an open bracket or operator, add "*"
				if ((bracketLevel == 0) && (i+1 < length)) {
					String nextCharAsString = Character.toString(input.charAt(i+1));
					if (!(operators.contains(nextCharAsString) ||
							nextCharAsString.equals("(") || nextCharAsString.equals(")"))) {
						presentOperators.add("*");
					}
				}
				
			} else if ((bracketLevel == 0) && operators.contains(Character.toString(digit))) {
				char previousChar = input.charAt(i-1);
				if (((digit == '-') || (digit == '+')) &&
                        (operators.contains(Character.toString(previousChar)) ||
                                previousChar == 'E')) //Ignore '-' or '+' if part of "[operator][-|+]" or "[val]E[-|+]"
					continue;
				presentOperators.add(Character.toString(digit));
			}
		}

		return presentOperators;
	}



	/**
	 * Parses an equation to find the operands to match get operators. Made to mirror each other.
	 * Separated to make more manageable. would be faster if combined.
	 * @param input - a string made up of only brackets, operators, and numbers
	 * @return presentOperands - the array-list of operators at the highest level of the equation.
	 */
	static ArrayList<String> getOperands(String input) {
		ArrayList<String> presentOperands = new ArrayList<>();
		int length = input.length();

		String currentOperand = "";
		
		// Everything except '(' is a valid start to an operand... includes the operator '-'
		int bracketLevel = 0;
		if (input.charAt(0) == '(') {
			bracketLevel++;
		} else {
			currentOperand += Character.toString(input.charAt(0)); 
		}
		
		for (int i = 1; i < length; i++) {
			char digit = input.charAt(i);
			if (digit == '(') {
				if (bracketLevel > 0) //bracket inside bracket
					currentOperand += "(";
				else if (specialOperators.contains(currentOperand) ||
						((currentOperand.length() > 0) && 
								(currentOperand.charAt(0) == '-') &&
								specialOperators.contains(currentOperand.substring(1)))) {
					currentOperand = "";
				} else if (!operators.contains(Character.toString(input.charAt(i-1)))) { //experimental ()() multiply
					if (!currentOperand.equals(""))
						presentOperands.add(currentOperand);
					currentOperand = "";
				}
				bracketLevel++;
				
			} else if (digit == ')') {
				bracketLevel--;
				if (bracketLevel > 0)
					currentOperand += ")";
				else {
					//chop it off as a new operand
					if (!currentOperand.equals(""))
						presentOperands.add(currentOperand);
					currentOperand = "";
				}
				
			} else if ((bracketLevel == 0) && operators.contains(Character.toString(digit))) {
				char previousChar = input.charAt(i-1);
				if ((digit == '-') &&
						(operators.contains(Character.toString(previousChar)) ||
								previousChar == 'E')) { //Ignore empty operand in "[operator][-|+]" or "[val]E[-|+]"
					currentOperand += "-";
					continue;
				}
				if (!currentOperand.equals(""))
					presentOperands.add(currentOperand);
				currentOperand = "";
			} else {
				currentOperand += digit;
			}	
		}
		
		if (!currentOperand.equals(""))
			presentOperands.add(currentOperand);
		
		return presentOperands;
	}


	/**
	 * Counts open and closing brackets. If unlevel, add opposing brackets to beginning or end to
	 * balance.
	 * @param input - a string made up of only brackets, operators, and numbers.
	 * @return the modified form of input.
     */
	private static String evenBrackets(String input) {
		int openBrackets = 0;
		int closeBrackets = 0;
		
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '(')
				openBrackets++;
			else if (input.charAt(i) == ')')
				closeBrackets++;
		}
		if (openBrackets > closeBrackets) {
			for (int i = 0; i < openBrackets - closeBrackets; i++)
				input += ")";
		} else {
			for (int i = 0; i < closeBrackets - openBrackets; i++)
				input = "(" + input;
		}
		
		return input;
	}


	/**
	 * Performs actions that are required only once before the recursive function (remove spaces,
	 * balance brackets) then hand it over to calculateFromString.
	 * @param input - a string made up of only brackets, operators, numbers, and spaces.
	 * @return the result of the equation.
	 * @throws Exception in the event of anything at all going wrong.
     */
	static Number equationProcessor(String input) throws Exception{
		input = replaceMathSymbolsNumeric(input);
		
		input = evenBrackets(input);
		
		return calculateFromString(input);
	}


	/**
	 * Finds all operands and operators, sends all operands for the same treatment. At lowest level,
	 * return the lone operand, or the result of [operand][operator][operand]. Works its way back up
	 * to the overall result.
	 * @param input - a string made up of only brackets, operators, and numbers.
	 * @return the result of the equation.
	 * @throws Exception in the event of anything at all going wrong.
     */
	private static Number calculateFromString(String input) throws Exception {
		final String reference = input;
		
		Number result;
		
		ArrayList<String> currentOperators = getOperators(input);
		ArrayList<String> currentOperands = getOperands(input);

		int neededOperands = 0;
		for (String operator : currentOperators) {
			if (operators.contains(operator)) {
				if (neededOperands == 0)
					neededOperands += 2;
				else
					neededOperands++;
			} else if (!(specialOperators.contains(operator) ||
					specialOperators.contains(operator.substring(1)))) {
				throw new Exception("Unrecognised operator");
			}
		}

		if (neededOperands != currentOperands.size())
			if (!((neededOperands == 0) && (currentOperands.size() == 1)))
				throw new Exception("Unmatched operators/operands");

		if ((currentOperands.size() == 1) && (currentOperators.size() == 0)) { //base case
			String operand = currentOperands.get(0);
			
			if (input.charAt(0) == '-') { // Negative operand
				input = input.substring(1);
				result = UMath.subtract(0, calculateFromString(input));
			} else if ((input.charAt(0) == '(') && (input.charAt(input.length() - 1) == ')')) {
					result = calculateFromString(input.substring(1, input.length() - 1));
				
			} else {
				boolean opFound = false;
				
				operand = operand.replace("E-", "_"); //Need a key, cannot use operators as replacement
				operand = operand.replace("E+", "~"); //Need a key, cannot use operators as replacement
				operand = operand.replace("E", "~"); //Need a key, cannot use operators as replacement
				
				for (String op : operators) {
					if (operand.contains(op)) { //An uncertainty could have still made it through
						if (operand.equals(op) || operand.equals("-" + op))
							throw new Exception("Lone operator");
						else 
							opFound = true;
					}
				}
				
				for (String op : specialOperators) {
					if (operand.contains(op)) { //An uncertainty could have still made it through
						if (operand.equals(op) || operand.equals("-" + op))
							throw new Exception("Lone special operator");
						else if (!(operand.contains("(") && operand.contains(")")))
							throw new Exception("Operator spaghetti");
						else
							opFound = true;
					}
				}
				
				operand = operand.replace("_", "E-"); //Need a key, cannot use operators as replacement
				operand = operand.replace("~", "E+"); //Need a key, cannot use operators as replacement
				
				if (opFound) {
					if (operand.equals(reference))
						throw new Exception("Would have looped");
					
					result = calculateFromString(operand);
				} else {
					try {
						result = Double.parseDouble(operand);
					} catch (NumberFormatException nfe) {
						//System.out.println(operand);
						throw new Exception("Invalid decimal to parse");
					}
				}
	
			}

		} else { //Carry out operations on simplified operands
			ArrayList<Number> numberOperands = new ArrayList<>(); //must be same length
			
			for (int j = 0; j < currentOperands.size(); j++) {
				numberOperands.add(calculateFromString(currentOperands.get(j)));
			}
			
			Number intermediateResult;
			for (String specOp: specialOperators) {
				while (currentOperators.contains(specOp) || currentOperators.contains("-" + specOp)) {

					int specOperatorIndex;
					
					boolean negative = false;
					if (currentOperators.contains("-" + specOp)) {
						specOperatorIndex = currentOperators.indexOf("-" + specOp);
						negative = true;
					} else
						specOperatorIndex = currentOperators.indexOf(specOp);

                    //decrease specOperatorIndex for each special operator before itself
                    int specCount = 0;
                    for (int k = 0; k < specOperatorIndex; k++) {
                        String previousOp = currentOperators.get(k);
                        if (!previousOp.equals("-") && (previousOp.charAt(0) == '-'))
                            previousOp = previousOp.substring(1);
                        if (specialOperators.contains(previousOp))
                            specCount++;
                    }
                    specOperatorIndex -= specCount;

					Number operand = numberOperands.get(specOperatorIndex);

					switch (specOp) {
					case "sqrt":
						intermediateResult = UMath.pow(operand, 0.5);
						break;
					case "sin":
						intermediateResult = UMath.sin(operand);
						break;
					case "cos":
						intermediateResult = UMath.cos(operand);
						break;
					case "tan":
						intermediateResult = UMath.tan(operand);
						break;
					case "asin":
						intermediateResult = UMath.asin(operand);
						break;
					case "acos":
						intermediateResult = UMath.acos(operand);
						break;
					case "atan":
						intermediateResult = UMath.atan(operand);
						break;
					case "sinh":
						intermediateResult = UMath.sinh(operand);
						break;
					case "cosh":
						intermediateResult = UMath.cosh(operand);
						break;
					case "tanh":
						intermediateResult = UMath.tanh(operand);
						break;
					case "log":
						intermediateResult = UMath.log(operand);
						break;
					case "ln":
						intermediateResult = UMath.ln(operand);
						break;
					case "exp":
						intermediateResult = UMath.exp(operand);
						break;
					default:
						throw new IllegalArgumentException("Invalid Operator.");
					}
					if (negative) {
						intermediateResult = UMath.subtract(0, intermediateResult);
					}
					numberOperands.set(specOperatorIndex, intermediateResult);
					currentOperators.remove(specOperatorIndex + specCount);
                }
			}

			for (String operator: operators) {
				while (currentOperators.contains(operator)) {
					int operatorIndex = currentOperators.indexOf(operator);
					Number operand1 = numberOperands.get(operatorIndex);
					Number operand2 = numberOperands.get(operatorIndex + 1);
					
					switch (operator) {
					case SYMBOL_PM:
						intermediateResult = new Uncertainty((double) operand1, (double) operand2);
						break;
					case "^":
						intermediateResult = UMath.pow(operand1, operand2);
						break;
					case "/":
						intermediateResult = UMath.divide(operand1, operand2);
						break;
					case "*":
						intermediateResult = UMath.multiply(operand1, operand2);
						break;
					case "+":
						intermediateResult = UMath.add(operand1, operand2);
						break;
					case "-":
						intermediateResult = UMath.subtract(operand1, operand2);
						break;
					default:
						throw new IllegalArgumentException("Invalid Operator.");
					}
					
					numberOperands.set(operatorIndex, intermediateResult);
					numberOperands.remove(operatorIndex + 1);
					currentOperators.remove(operatorIndex);
				}
			}
			
			result = numberOperands.get(0); //should have length 1
		}
		
		return result;
	}


	/**
	 * Checks to see if an equation is made up of only recognised characters.
	 * @param input any string equation.
	 * @return true if the equation is made up of symbols that are recognised in this calculator.
     */
	static boolean isOnlyValidCharacters(String input) {
		String validCharacters = "0-9A-Za-z.)( " + SYMBOL_PI;
        for (String operator : operators)
            validCharacters += operator;

        return input.matches("[" + validCharacters + "]+");
	}


	/**
	 * The algebraic analog of equationProcessor.
	 * Processes equation to show the equation preview with the same order of operations as what the
	 * calculator will carry out.
	 * @param input - a string made up of only brackets, operators, and numbers.
	 * @return the string corresponding with the formatted preview of the equation.
	 * @throws Exception in the event of anything at all going wrong.
	 */
	static String toFormula(String input) throws Exception {
		String result = "$";

		if (input.length() > 0) {
			input = replaceMathSymbolsReadable(input);
			input = evenBrackets(input);

			String theEquation = formulaFromString(input);
			int length = theEquation.length();
			if ((length > 0) && (theEquation.charAt(0) == '(') && (theEquation.charAt(length - 1) == ')')) {
				theEquation = theEquation.substring(1, length - 1);
			}
			result += theEquation;
		}
		
		result += "$";
		
		return result;
	}

	// Replaces es and pis in the way they will be considered, without losing readability
	private static String replaceMathSymbolsReadable(String input) {
		input = input.replaceAll("\\s", "");
		input = input.replaceAll(SYMBOL_PI, "(" + SYMBOL_PI + ")");

		input = input.replaceAll("exp", "e^");

		return input;
	}

	// Replaces es and pis with their values
	private static String replaceMathSymbolsNumeric(String input) {
		input = input.replaceAll("\\s", "");
		input = input.replaceAll(SYMBOL_PI, "(" + Math.PI + ")");

		input = input.replaceAll("e^", "EXP");
		input = input.replaceAll("exp", "EXP");
		input = input.replaceAll("e", "(" + Math.E + ")");
		input = input.replaceAll("EXP", "exp");

		return input;
	}


	/** SHOULD BE USED
	public static boolean containsOperator(String input) {
		boolean operatorFound = false;
		for (String operator : operators) {
			if (input.contains(operator))
					operatorFound = true;
		}
		return operatorFound;
	}**/


	/**
	 * The algebraic analog of calculateFromString.
	 * Finds all operands and operators, sends all operands for the same treatment. At lowest level,
	 * return the lone operand, or the bracketed form of [operand][operator][operand]. Works its way
	 * back up to the overall (fully bracketed) string.
	 * @param input - a string made up of only brackets, operators, and numbers.
	 * @return the result of the equation.
	 * @throws Exception in the event of anything at all going wrong.
	 */
	private static String formulaFromString(String input) throws Exception {
		final String reference = input;
		
		String result;
		
		ArrayList<String> currentOperators = getOperators(input);
		ArrayList<String> currentOperands = getOperands(input);

		int neededOperands = 0;
		for (String operator : currentOperators) {
			if (operators.contains(operator)) {
				if (neededOperands == 0)
					neededOperands += 2;
				else
					neededOperands++;
			} else if (!(specialOperators.contains(operator) ||
					specialOperators.contains(operator.substring(1)))) {
				throw new Exception("Unrecognised operator");
			}
		}
		
		if (neededOperands != currentOperands.size())
			if (!((neededOperands == 0) && (currentOperands.size() == 1)))
				throw new Exception("Unmatched operators/operands");
		
		if ((currentOperands.size() == 1) && (currentOperators.size() == 0)) { //base case
			String operand = currentOperands.get(0);
			
			if (input.charAt(0) == '-') { // Negative operand
				input = input.substring(1);
				result = "(-" + formulaFromString(input) + ")";
			} else if ((input.charAt(0) == '(') && (input.charAt(input.length() - 1) == ')')) {
					result = formulaFromString(input.substring(1, input.length() - 1));
				
			} else {
				boolean opFound = false;
				
				operand = operand.replace("E-", "_"); //Need a key, cannot use operators as replacement
				operand = operand.replace("E+", "~"); //Need a key, cannot use operators as replacement
				operand = operand.replace("E", "~"); //Need a key, cannot use operators as replacement
				
				for (String op : operators) {
					if (operand.contains(op)) { //An uncertainty could have still made it through
						if (operand.equals(op) || operand.equals("-" + op))
							throw new Exception("Lone operator");
						else 
							opFound = true;
					}
				}
				
				for (String op : specialOperators) {
					if (operand.contains(op)) {
						if (operand.equals(op) || operand.equals("-" + op))
							throw new Exception("Lone special operator");
						else if (!(operand.contains("(") && operand.contains(")")))
							throw new Exception("Operator spaghetti");
						else
							opFound = true;
					}
				}
				
				operand = operand.replace("_", "E-"); //Need a key, cannot use operators as replacement
				operand = operand.replace("~", "E"); //Need a key, cannot use operators as replacement
				
				if (opFound) {
					if (operand.equals(reference))
						throw new Exception("Would have looped");
					
					result = formulaFromString(operand);
				} else {
					if (operand.contains("E"))
						result = "(" + operand + ")";
					else if (operand.equals("Infinity") || operand.equals("NaN"))
						result = "{" + operand + "}";
					else
						result = operand; //should be a decimal
				}
	
			}
			
			
		} else { //Carry out operations on simplified operands
			ArrayList<String> processedOperands = new ArrayList<>(); //must be same length
			
			for (int j = 0; j < currentOperands.size(); j++) {
				processedOperands.add(formulaFromString(currentOperands.get(j)));
			}
			
			String intermediateResult;
			for (String specOp: specialOperators) {
				while (currentOperators.contains(specOp) || currentOperators.contains("-" + specOp)) {

					int specOperatorIndex;

					boolean negative = false;
					if (currentOperators.contains("-" + specOp)) {
						specOperatorIndex = currentOperators.indexOf("-" + specOp);
						negative = true;
					} else
						specOperatorIndex = currentOperators.indexOf(specOp);

                    //decrease specOperatorIndex for each special operator before itself
                    int specCount = 0;
                    for (int k = 0; k < specOperatorIndex; k++) {
                        String previousOp = currentOperators.get(k);
                        if (!previousOp.equals("-") && (previousOp.charAt(0) == '-'))
                            previousOp = previousOp.substring(1);
                        if (specialOperators.contains(previousOp))
                            specCount++;
                    }
                    specOperatorIndex -= specCount;

					String operand = processedOperands.get(specOperatorIndex);
					//"sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "log", "ln", "exp"
					if (!(operand.charAt(0) == '('))
						operand = "(" + operand + ")";
					
					if (specOp.equals("sqrt")) {
						intermediateResult = "\u221A" + operand;
					} else {
						intermediateResult = specOp + operand; 
					}
					
					if (negative) {
						intermediateResult = "-" + intermediateResult;
					}
					intermediateResult = "(" + intermediateResult + ")";
					
					processedOperands.set(specOperatorIndex, intermediateResult);
					currentOperators.remove(specOperatorIndex + specCount);
				}
			}
			
			
			for (String operator: operators) {
				while (currentOperators.contains(operator)) {
					int operatorIndex = currentOperators.indexOf(operator);
					String operand1 = processedOperands.get(operatorIndex);
					String operand2 = processedOperands.get(operatorIndex + 1);

					if (operator.equals("/")) {
						int len1 = operand1.length();
						if ((len1 > 0) && (operand1.charAt(0) == '(') && (operand1.charAt(len1-1) == ')'))
							operand1 = "{" + operand1.substring(1, len1-1) + "}";


						int len2 = operand2.length();
						if ((len2 > 0) && (operand2.charAt(0) == '(') && (operand2.charAt(len2-1) == ')'))
							operand2 = "{" + operand2.substring(1, len2-1) + "}";
					}

                    intermediateResult = "(" + operand1 + operator + operand2 + ")";
					
					processedOperands.set(operatorIndex, intermediateResult);
					processedOperands.remove(operatorIndex + 1);
					currentOperators.remove(operatorIndex);
				}
			}
			
			result = processedOperands.get(0); //should have length 1
		}
		
		return result;
	}
}
