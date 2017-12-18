package com.db.awmd.challenge.service;

import com.db.awmd.challenge.constants.Messages;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.reqres.AccountRequestPOJO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	@Setter
	private NotificationService notificationService;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public Account createAccount(AccountRequestPOJO accountRequest) throws DuplicateAccountIdException {

		Account account = new Account(accountRequest.getAccountId(), BigDecimal.valueOf(accountRequest.getBalance()));
		return this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public boolean transferBetweenAccounts(AmountTransfer amount) throws NegativeBalanceException, EmptyDataException {
		Account fromAccount = accountsRepository.getAccount(amount.getFromAccountId());
		Account toAccount = accountsRepository.getAccount(amount.getToAccountId());

		if (null == fromAccount && null == toAccount) {
			log.error(Messages.NO_DATA_FOR_ACCOUNTS.getMessage());
			throw new EmptyDataException(Messages.NO_DATA_FOR_ACCOUNTS.getMessage());
		}
		if (null == fromAccount) {
			log.error(Messages.TRANSFERER_CAN_NOT_BE_NULL.getMessage());
			throw new EmptyDataException(Messages.TRANSFERER_CAN_NOT_BE_NULL.getMessage());
		}

		if (null == toAccount) {
			log.error(Messages.PAYEE_CAN_NOT_BE_NULL.getMessage());
			throw new EmptyDataException(Messages.PAYEE_CAN_NOT_BE_NULL.getMessage());
		}

		return this.accountsRepository.transfer(fromAccount, toAccount, amount.getAmount(), notificationService);
	}
}
