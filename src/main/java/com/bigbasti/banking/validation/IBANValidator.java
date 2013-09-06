package com.bigbasti.banking.validation;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Validates and performs a structural check on IBANs
 * @author Sebastian Gross
 *
 */
public class IBANValidator {

	/**
	 * Extracts the Accountnumber and the BLZ from a german IBAN
	 * @param germanIban
	 * @return Value 0: Accountnumber (without leading zero)
	 * 		   Value 1: BLZ
	 */
	public static String[] getValuesFromGermanIban(String germanIban){
		String clearedIban = removeUnneededCharacters(germanIban);

		String blz;
		String knr;

		blz = clearedIban.substring(4).substring(0,8);
		knr = clearedIban.substring(12);

		return new String[] {knr.startsWith("0") ? knr.substring(1) : knr, blz};
	}

	/**
	 * Performs a complete structural check on an IBAN 
	 * @param iban The IBAN you want to check
	 * @param country Country code (example: DE)
	 * @param length The length of the iban (example: 22 (for a german IBAN))
	 * @param sctructure The structure to use in the validation (example: DE2!n8!n10!n (for a german IBAN))
	 * @return True if the check is successful or {@link IBANValidationException} in case of an error
	 * @throws IBANValidationException
	 */
	public static boolean checkIbanStructure(String iban, String country, String length, String structure) throws IBANValidationException{
		String clearedIban = removeUnneededCharacters(iban);

		if(clearedIban.length() != Integer.parseInt(length)){
			throw new IBANValidationException(IBANValidationErrorType.INCORRECT_LENGTH);
		}

		return performIBANSctructureCheck(clearedIban, structure, country.toUpperCase());
	}

	/**
	 * Calculates the checknumber for the given IBAN (and also checks it)
	 * @param iban The IBAN you want to check
	 * @return True if the check is successful or {@link IBANValidationException} in case of an error
	 * @throws IBANValidationException
	 */
	public static boolean validateIban(String iban) throws IBANValidationException{

		//Deutsche IBANS muessen 22 Zeichen lang sein!
		String clearedIban = removeUnneededCharacters(iban);

		if(isGermanIBAN(clearedIban)){
			if(clearedIban.length() != 22){
				throw new IBANValidationException(IBANValidationErrorType.INCORRECT_LENGTH);
			}
		}

		//Pruefsumme checken
		String checksumIban = moveCountryDataToEnd(clearedIban);
		String numericalIban = convertCharsToNumbers(checksumIban);
		int checkNumber = calculateCheckNumber(numericalIban);

		if(checkNumber != 1){
			throw new IBANValidationException(IBANValidationErrorType.INCORRECT_CHECKSUM);
		}

		return true;
	}

	/**
	 * Performs a structural check on an IBAN (no check for length)
	 * @param iban The IBAN you want to check
	 * @param sctructure The structure to use in the validation (example: DE2!n8!n10!n (for a german IBAN))
	 * @param country Country code (example: DE)
	 * @return True if the check is successful or {@link IBANValidationException} in case of an error
	 * @throws IBANValidationException
	 */
	private static boolean performIBANSctructureCheck(String iban, String structure, String country) throws IBANValidationException{

		boolean checkSuccessfull = true;

		String countryPrefix = iban.substring(0, 2);
		String noCountryIban = iban;

		String noCountryStructure = structure;
		/**
		 * some structures doesn't provide a country code at the beginning
		 * in which case the country code should not be removed!
		 */
		if(Pattern.matches("[A-Z]+", structure.substring(0,2))){
			noCountryStructure = structure.substring(2);
			noCountryIban = iban.substring(2);
		}

		if(!countryPrefix.equals(country)){
			checkSuccessfull = false;
		}

		LinkedList<String> structureParts = getStructureParts(noCountryStructure);

		if(!validateStructureParts(structureParts, noCountryIban)){
			checkSuccessfull = false;
		}

		if(!checkSuccessfull){
			throw new IBANValidationException(IBANValidationErrorType.INCORRECT_STRUCTURE);
		}

		return checkSuccessfull;
	}

	/**
	 * Checks an IBAN (without leading country code) for structural validity
	 * @param structureParts All parts of an IBAN structure (without country code)
	 * @param noCountryIban IBAN withount leading country code
	 * @return True if the check is successful
	 */
	private static boolean validateStructureParts(LinkedList<String> structureParts, String noCountryIban){
		StringBuilder regex = new StringBuilder();
		for(String structurePart : structureParts){
			String[] guidelines = structurePart.split("!");

			if(guidelines[1].equals("n")){
				regex.append("[0-9]");
			}else if(guidelines[1].equals("a")){
				regex.append("[A-Z]");
			}else if(guidelines[1].equals("c")){
				regex.append("[A-Za-z0-9]");
			}else if(guidelines[1].equals("e")){
				regex.append("[ ]");	//Ungetestet, da es noch keine IBAN mit so einem Strukturteil gibt
			}
			regex.append("{").append(guidelines[0]).append("}");
		}
		return Pattern.matches(regex.toString(), noCountryIban);
	}

	/**
	 * Extracts all parts from an IBAN structure
	 * @param noCountryStructure IBAN structure without country code
	 * @return List containig all extracted parts of the structure
	 */
	private static LinkedList<String> getStructureParts(String noCountryStructure){
		LinkedList<String> structureParts = new LinkedList<String>();
		StringBuffer part = new StringBuffer();
		for(int i = 0; i < noCountryStructure.length(); i++){
			if(i > 0 && noCountryStructure.charAt(i-1) == '!'){
				part.append(noCountryStructure.charAt(i));
				structureParts.add(part.toString());
				part = new StringBuffer();
			}else{
				part.append(noCountryStructure.charAt(i));
			}
		}
		return structureParts;
	}

	/**
	 * Calculate the checknumber using Modulo
	 * @param numericalIban IBAN where all characters have been converted to numbers
	 * @return the calculated checknumber
	 */
	private static int calculateCheckNumber(String numericalIban){
		BigDecimal iban = new BigDecimal(numericalIban.toString());
		return iban.remainder(new BigDecimal(97)).intValue();
	}

	/**
	 * Converts all characters in the IBAN into numbers, this is needed in Order to calculate the checknumber
	 * @param iban IBAN
	 * @return converted IBAN
	 */
	private static String convertCharsToNumbers(String iban){
		StringBuilder numericIban = new StringBuilder();

		for(int i = 0; i < iban.length(); i++){
			char c = iban.charAt(i);
			if(c >= '0' && c <= '9'){
				numericIban.append(c);
			}else if(c >= 'A' && c <= 'Z'){
				int newChar = c - 'A' + 10;
				numericIban.append(newChar);
			}
		}

		return numericIban.toString();
	}

	/**
	 * Moves the first 4 characters of the IBAN (country code + checknumber) to the end of the IBAN
	 * @param iban IBAN
	 * @return IBAN with switched parts
	 */
	private static String moveCountryDataToEnd(String iban){
		return iban.substring(4).concat(iban.substring(0,4)).toUpperCase();
	}

	/**
	 * Removes all unneeded characters from the IBAN
	 * Allowed characters are numbers and letters
	 * @param iban IBAN-String to clean up
	 * @return cleaned up IBAN
	 */
	private static String removeUnneededCharacters(String iban){
		return iban.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
	}

	/**
	 * Checks if the IBAN belongs to a german bank
	 * @param iban IBAN
	 * @return true if IBAN belongs to a german bank
	 */
	private static boolean isGermanIBAN(String iban){
		return iban.startsWith("DE");
	}
}
