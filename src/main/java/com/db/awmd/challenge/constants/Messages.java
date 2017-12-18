package com.db.awmd.challenge.constants;

import lombok.Getter;
/**
 * 
 * @author Hillol
 * This Enum stores all the system related messages
 *
 */
public enum Messages {
	TRANSFERER_CAN_NOT_BE_NULL("The transferer account can not be null."),
	PAYEE_CAN_NOT_BE_NULL("The payee account can not be null."),
	EMPTY_DATA("Empty Data."),
	NO_DATA_FOR_ACCOUNTS("No data for account."),
	TRANSFER_OPERATION_ENCOUNTERED_A_PROBLEM("Transfer operation encountered a problem as we do not support overdrafts!"),
	INITIAL_BALANCE_MUST_BE_POSITIVE("Initial balance must be positive."),
	ACCOUNT_CREATED("Account has been created."),
	NULL_ACCOUNT_ID_CREATION("Account creation not possible with empty accountId."),
	SUCCESS("Success."), 
	ACCOUNT_ID_CAN_NOT_BE_NULL("Account Id can not be null or empty."), 
	NEGATIVE_BALANCE_ACCOUNT_ID_CREATION_NOT_POSSIBLE("Account creation not possible with negative balance.");
	
	
	/**
	 * The message about the constant description
	 */
	@Getter
	
	private String message;
	Messages(final String message) {
		this.message = message;
	}

}
