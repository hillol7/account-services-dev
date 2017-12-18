package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.constants.Messages;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountsRepositoryInMemory implements AccountsRepository {

	private static final Map<String, Account> accounts = new ConcurrentHashMap<>();

	private static final Map<String, AtomicReference<BigDecimal>> accountIdToBalanceMap = new ConcurrentHashMap<>();

	@Override
	public Account createAccount(Account account) throws DuplicateAccountIdException {
		Object lock = account.getLock();
		synchronized (lock) {
			Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
			if (previousAccount != null) {
				throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
			}

			accountIdToBalanceMap.putIfAbsent(account.getAccountId(), new AtomicReference<>(account.getBalance()));
		}
		return account;
	}

	@Override
	public Account getAccount(String accountId) {
		return (null == accounts.get(accountId) ? null : accounts.get(accountId));
	}

	@Override
	public synchronized void clearAccounts() {
		accounts.clear();
		accountIdToBalanceMap.clear();
	}

	@Override
	public boolean transfer(Account fromAccount, Account toAccount, BigDecimal amount,
			NotificationService notificationService) throws NegativeBalanceException, EmptyDataException {

		Object lockFirst = fromAccount.compare(toAccount) < 0 ? fromAccount.getLock() : toAccount.getLock();
		Object lockSecond = fromAccount.compare(toAccount) < 0 ? toAccount.getLock() : fromAccount.getLock();
		synchronized (lockFirst) {
			synchronized (lockSecond) {

				String fromAccountId = fromAccount.getAccountId();
				String toAccountId = toAccount.getAccountId();
				AtomicReference<BigDecimal> fromAccountIdBalance = accountIdToBalanceMap.get(fromAccountId);

				log.info("Retrieved fromAccountId info [{}]", fromAccountIdBalance);
				BigDecimal fromAccountIdBalanceRaw = fromAccountIdBalance.get();

				if (fromAccountIdBalanceRaw.subtract(amount).doubleValue() >= 0) {
					AtomicReference<BigDecimal> toAccountIdBalance = accountIdToBalanceMap.get(toAccountId);
					log.info("Retrieved toAccountIdBalance info [{}]", toAccountIdBalance);
					BigDecimal toAccountIdBalanceRawOld = toAccountIdBalance.get();
					fromAccountIdBalance.compareAndSet(fromAccountIdBalanceRaw,
							fromAccountIdBalanceRaw.subtract(amount));
					Account tempFrom = accounts.get(fromAccountId);
					tempFrom.setBalance(fromAccountIdBalance.get());
					accounts.put(fromAccountId, tempFrom);

					log.info("From account balance updated [{}]", fromAccountIdBalance);
					notificationService.notifyAboutTransfer(tempFrom, String
							.format("Account Id %s has been credited with %f.", toAccountId, amount.doubleValue()));
					toAccountIdBalance.compareAndSet(toAccountIdBalanceRawOld, toAccountIdBalanceRawOld.add(amount));
					Account tempTo = accounts.get(toAccountId);
					tempTo.setBalance(toAccountIdBalance.get());
					accounts.put(toAccountId, tempTo);
					log.info("To account balance updated [{}]", toAccountIdBalance);
					notificationService.notifyAboutTransfer(tempTo,
							String.format("Your Account has been credited with %f , the debited account id is %s.",
									amount.doubleValue(), fromAccountId));
					return true;

				} else {
					throw new NegativeBalanceException(Messages.TRANSFER_OPERATION_ENCOUNTERED_A_PROBLEM.getMessage());
				}
			}
		}
	}

	@Override
	public List<Account> getAllAccounts() {
		return accounts.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}
}
