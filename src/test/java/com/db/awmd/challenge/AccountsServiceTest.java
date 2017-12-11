package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void testTransfer() throws NegativeBalanceException, EmptyDataException {
		String uniqueAccountId = "Id-1";
		Account account = new Account(uniqueAccountId, new BigDecimal("1000"));
		String uniqueAccountIdTwo = "Id-2";
		Account two = new Account(uniqueAccountIdTwo, new BigDecimal("1000"));
		this.accountsService.createAccount(account);
		this.accountsService.createAccount(two);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		this.accountsService.transferBetweenAccounts(at);
		assertThat(this.accountsService.getAccount(uniqueAccountId).getBalance().doubleValue() == 900d);
		assertThat(this.accountsService.getAccount(uniqueAccountIdTwo).getBalance().doubleValue() == 1100d);
	}
}
