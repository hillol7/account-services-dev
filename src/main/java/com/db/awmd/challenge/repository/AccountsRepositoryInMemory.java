package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.NotificationService;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountsRepositoryInMemory implements AccountsRepository {

	private static final Map<String, Account> accounts = new ConcurrentHashMap<>();

	private static final Map<String, AtomicReference<BigDecimal>> accountIdToBalanceMap = new ConcurrentHashMap<>();

	@Override
	@Synchronized
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}

		AtomicReference<BigDecimal> accountIdBalance = accountIdToBalanceMap.putIfAbsent(account.getAccountId(),
				new AtomicReference<>(account.getBalance()));
		if (accountIdBalance != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}

	}

	@Override
	@Synchronized
	public Account getAccount(String accountId) {
		return (null == accounts.get(accountId) ? null : accounts.get(accountId));
	}

	@Override
	@Synchronized
	public void clearAccounts() {
		accounts.clear();
		accountIdToBalanceMap.clear();
	}

	@Override
	@Synchronized
	public void transfer(String fromAccountId, String toAccountId, BigDecimal amount, NotificationService notificationService)
			throws NegativeBalanceException, EmptyDataException {

		AtomicReference<BigDecimal> fromAccountIdBalance = accountIdToBalanceMap.get(fromAccountId);
		if (null == fromAccountIdBalance) {
			throw new EmptyDataException("Empty Data.");
		}
		log.info("Retrieved fromAccountId info [{}]", fromAccountIdBalance);
		BigDecimal fromAccountIdBalanceRaw = fromAccountIdBalance.get();

		if (fromAccountIdBalanceRaw.subtract(amount).doubleValue() >= 0) {
			AtomicReference<BigDecimal> toAccountIdBalance = accountIdToBalanceMap.get(toAccountId);
			if (null == toAccountIdBalance) {
				throw new EmptyDataException("Empty Data.");
			}
			log.info("Retrieved toAccountIdBalance info [{}]", toAccountIdBalance);
			BigDecimal toAccountIdBalanceRawOld = toAccountIdBalance.get();
			fromAccountIdBalance.compareAndSet(fromAccountIdBalanceRaw, fromAccountIdBalanceRaw.subtract(amount));
			Account tempFrom = accounts.get(fromAccountId);
			tempFrom.setBalance(fromAccountIdBalance.get());
			accounts.put(fromAccountId, tempFrom);

			log.info("From account balance updated [{}]", fromAccountIdBalance);
			notificationService.notifyAboutTransfer(tempFrom, String.format("Account Id %s has been credited with %f.", toAccountId, amount.doubleValue()));
			toAccountIdBalance.compareAndSet(toAccountIdBalanceRawOld, toAccountIdBalanceRawOld.add(amount));
			Account tempTo = accounts.get(toAccountId);
			tempTo.setBalance(toAccountIdBalance.get());
			accounts.put(toAccountId, tempTo);
			log.info("To account balance updated [{}]", toAccountIdBalance);
			notificationService.notifyAboutTransfer(tempTo, String.format("Your Account has been credited with %f , the debited account id is %s.", amount.doubleValue(),fromAccountId));
			return;

		} else {
			throw new NegativeBalanceException(
					"Transfer operation encountered a problem as we do not support overdrafts!");
		}

	}
}
