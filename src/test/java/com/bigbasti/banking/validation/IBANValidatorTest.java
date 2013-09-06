package com.bigbasti.banking.validation;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for IBAN Validation
 * @author Sebastian Gross
 *
 */
public class IBANValidatorTest {

	private final String VALID_IBAN_DE = "DE89 3704 0044 0532-0130 00";
	private final String INVALID_IBAN_DE = "DE89 3704 0044 0532-0130 01";
	private final String INVALID_IBAN_DE_2 = "DE89 3704 0044 0532-0130 001";

	private final String DE_IBAN_LENGTH = "22";
	private final String DE_IBAN_STRUCTURE = "DE2!n8!n10!n";

        /*
         * This is the only IBAN structure i found containing no country code
         */
	private final String VALID_IBAN_KZ = "KZ86 125K ZT50 0410 0100";
	private final String KZ_IBAN_LENGTH = "20";
	private final String KZ_IBAN_STRUCTURE = "2!a2!n3!n13!c";
	private final String KZ_IBAN_STRUCTURE_INVALID = "2!a2!a3!n13!c";

	private final String VALID_IBAN_LV = "LV80 BANK 0000 4351-9500 1";
	private final String INVALID_IBAN_LV = "LV80 BANK 0000 4351-9500 2";

	private final String VALID_IBAN_SM = "SM86 U032 2509 8000 0000 0270 100";
	private final String SM_IBAN_LENGTH = "27";
	private final String SM_IBAN_STRUCTURE = "SM2!n1!a5!n5!n12!c";


	@Test
	public void SplitIBanIsWorkingNoLeadingZero() {
		String [] result = IBANValidator.getValuesFromGermanIban(VALID_IBAN_DE);

		assertTrue(result[0].equals("532013000"));
		assertTrue(result[1].equals("37040044"));
	}

	@Test
	public void validGermanIbanIsBeingValidatedSuccessfully() {
		boolean result = false;

		try {
			result = IBANValidator.validateIban(VALID_IBAN_DE);
		} catch (IBANValidationException e) {}

		assertTrue(result);
	}

	@Test
	public void invalidGermanIbanFailsWithChecksum() {
		boolean result = false;

		try {
			result = IBANValidator.validateIban(INVALID_IBAN_DE);
		} catch (IBANValidationException e) {
			assertEquals(e.getIBANValidationError(), IBANValidationErrorType.INCORRECT_CHECKSUM);
		}
	}

	@Test
	public void invalidGermanIbanFailsWithLengthCheck() {
		boolean result = false;

		try {
			result = IBANValidator.validateIban(INVALID_IBAN_DE_2);
		} catch (IBANValidationException e) {
			assertEquals(e.getIBANValidationError(), IBANValidationErrorType.INCORRECT_LENGTH);
		}
	}

	@Test
	public void invalidLatvianIbanFailsWithChecksum() {
		boolean result = false;

		try {
			result = IBANValidator.validateIban(INVALID_IBAN_LV);
		} catch (IBANValidationException e) {
			assertEquals(e.getIBANValidationError(),IBANValidationErrorType.INCORRECT_CHECKSUM);
		}
	}

	@Test
	public void validLatvianIbanIsCheckedSuccessfully() {
		boolean result = false;

		try {
			result = IBANValidator.validateIban(VALID_IBAN_LV);
		} catch (IBANValidationException e) {}

		assertTrue(result);
	}

	@Test
	public void validGermanIBANStructureCheck() {
		boolean result = false;

		try {
			result = IBANValidator.checkIbanStructure(VALID_IBAN_DE, "DE", DE_IBAN_LENGTH, DE_IBAN_STRUCTURE);
		} catch (IBANValidationException e) {}

		assertTrue(result);
	}

	@Test
	public void validGermanIBANStructureCheckTooLongIBAN() {
		boolean result = false;

		try {
			result = IBANValidator.checkIbanStructure(VALID_IBAN_DE+"1", "DE", DE_IBAN_LENGTH, DE_IBAN_STRUCTURE);
		} catch (IBANValidationException e) {
			assertEquals(e.getIBANValidationError(),IBANValidationErrorType.INCORRECT_LENGTH);
		}
	}

	@Test
	public void validGermanIBANStructureCheckTooLongIBAN_2() {
		boolean result = false;

		try {
			result = IBANValidator.checkIbanStructure(INVALID_IBAN_DE_2, "DE", "23", DE_IBAN_STRUCTURE);
		} catch (IBANValidationException e) {
			assertEquals(e.getIBANValidationError(),IBANValidationErrorType.INCORRECT_STRUCTURE);
		}
	}

	@Test
	public void validKazachstanIBANStructureCheck() {
		boolean result = false;

		try {
			result = IBANValidator.checkIbanStructure(VALID_IBAN_KZ, "KZ", KZ_IBAN_LENGTH, KZ_IBAN_STRUCTURE);
		} catch (IBANValidationException e) {}

		assertTrue(result);
	}

	@Test
	public void validKazachstanBANStructureCheckInvalidStructure() {
		boolean result = false;

		try {
			result = IBANValidator.checkIbanStructure(VALID_IBAN_KZ, "KZ", KZ_IBAN_LENGTH, KZ_IBAN_STRUCTURE_INVALID);
		} catch (IBANValidationException e) {
			assertEquals(e.getIBANValidationError(),IBANValidationErrorType.INCORRECT_STRUCTURE);
		}
	}

	@Test
	public void validSanMarinoIBANStructureCheck() {
		boolean result = false;

		try {
			result = IBANValidator.checkIbanStructure(VALID_IBAN_SM, "SM", SM_IBAN_LENGTH, SM_IBAN_STRUCTURE);
		} catch (IBANValidationException e) {}

		assertTrue(result);
	}

}
