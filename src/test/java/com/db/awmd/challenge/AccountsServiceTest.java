package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;
import com.db.awmd.reqres.AccountRequestPOJO;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	public static long averageTime = 0;
	@Autowired
	private AccountsService accountsService;

	@Mock
	private NotificationService notification;
	
	@Before
	public void setUp() {
		accountsService.setNotificationService(notification);
	}
	@Test
	public void addAccount() throws Exception {
		AccountRequestPOJO account = new AccountRequestPOJO();
		account.setBalance(1000);
		account.setAccountId("Id-123456");
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123456").getAccountId().equals("Id-123456"));
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		AccountRequestPOJO account = new AccountRequestPOJO();
		account.setAccountId(uniqueId);
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
		String uniqueAccountId = "Id-1SERVICE";
		String uniqueAccountIdTwo = "Id-2SERVICE";
		AccountRequestPOJO account = new AccountRequestPOJO();
		account.setAccountId(uniqueAccountId);
		account.setBalance(1000);

		AccountRequestPOJO accountTwo = new AccountRequestPOJO();
		accountTwo.setAccountId(uniqueAccountIdTwo);
		accountTwo.setBalance(1000);

		this.accountsService.createAccount(account);
		this.accountsService.createAccount(accountTwo);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		Mockito.doNothing().when(notification).notifyAboutTransfer(anyObject(), anyString());
		
		this.accountsService.transferBetweenAccounts(at);
		assertThat(this.accountsService.getAccount(uniqueAccountId).getBalance().doubleValue() == 900d);
		assertThat(this.accountsService.getAccount(uniqueAccountIdTwo).getBalance().doubleValue() == 1100d);
	}

	@Test(expected = NegativeBalanceException.class)
	public void testTransfe_Must_fail_due_to_insufficient_funds() throws NegativeBalanceException, EmptyDataException {
		String uniqueAccountId = "Id-1";
		String uniqueAccountIdTwo = "Id-2";
		AccountRequestPOJO account = new AccountRequestPOJO();
		account.setAccountId(uniqueAccountId);
		account.setBalance(100);

		AccountRequestPOJO accountTwo = new AccountRequestPOJO();
		accountTwo.setAccountId(uniqueAccountIdTwo);
		accountTwo.setBalance(1000);

		this.accountsService.createAccount(account);
		this.accountsService.createAccount(accountTwo);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(1000));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		this.accountsService.transferBetweenAccounts(at);
	}

	@Test(expected = EmptyDataException.class)
	public void testTransfe_Must_fail_due_to_non_existence_of_accounts()
			throws NegativeBalanceException, EmptyDataException {
		String uniqueAccountId = "Id-1tttttttttttttttttt";
		String uniqueAccountIdTwo = "Id-ttttttttttttttttttttttttttttt";
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(1000));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		this.accountsService.transferBetweenAccounts(at);
	}
	
	@Test(expected = EmptyDataException.class)
	public void testTransfe_Must_fail_due_to_non_existence_of_to_account() throws NegativeBalanceException, EmptyDataException {
		String uniqueAccountId = "Id-1DDDDDDDDDDDDDDDDDDDD";
		String uniqueAccountIdTwo = "Id-2DDDDDDDDDDDDDDDDDDDDDD";
		AccountRequestPOJO account = new AccountRequestPOJO();
		account.setAccountId(uniqueAccountId);
		account.setBalance(100);

		AccountRequestPOJO accountTwo = new AccountRequestPOJO();
		accountTwo.setAccountId(uniqueAccountIdTwo);
		accountTwo.setBalance(1000);

		this.accountsService.createAccount(account);
		//this.accountsService.createAccount(accountTwo);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(1000));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		this.accountsService.transferBetweenAccounts(at);
	}
	
	@Test(expected = EmptyDataException.class)
	public void testTransfe_Must_fail_due_to_non_existence_of_from_account() throws NegativeBalanceException, EmptyDataException {
		String uniqueAccountId = "Id-1DDDDDDDDDDDDDDDDDDDDRRRRRRRRRRR";
		String uniqueAccountIdTwo = "Id-2DDDDDDDDDDDDDDDDDDDDDDTTTTTTTTTTT";
		AccountRequestPOJO account = new AccountRequestPOJO();
		account.setAccountId(uniqueAccountId);
		account.setBalance(100);

		AccountRequestPOJO accountTwo = new AccountRequestPOJO();
		accountTwo.setAccountId(uniqueAccountIdTwo);
		accountTwo.setBalance(1000);

		//this.accountsService.createAccount(account);
		this.accountsService.createAccount(accountTwo);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(1000));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		this.accountsService.transferBetweenAccounts(at);
	}

}
