package com.db.awmd.challenge.repository;

import java.math.BigDecimal;
import java.util.List;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.NotificationService;

public interface AccountsRepository {

	Account createAccount(Account account) throws DuplicateAccountIdException;

	Account getAccount(String accountId);

	void clearAccounts();
	
	List<Account> getAllAccounts();

	boolean transfer(Account fromAccount, Account toAccount, BigDecimal ammount, NotificationService notificationService) throws NegativeBalanceException, EmptyDataException;
}
