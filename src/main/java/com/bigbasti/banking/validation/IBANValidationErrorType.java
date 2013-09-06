package com.bigbasti.banking.validation;

/**
 * Enum with customized error messages
 * @author Sebastian Gross
 */
public enum IBANValidationErrorType {

	INCORRECT_CHECKSUM{
		public String toString(){
			return "Checksum calculation failed for IBAN";
		}
	},

	INCORRECT_LENGTH{
		public String toString(){
			return "The length of the IBAN is not correct";
		}
	},

	INCORRECT_STRUCTURE{
		public String toString(){
			return "The given structure does not math the IBAN";
		}
	}

}
