package com.db.awmd.challenge.domain;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;

/**
 * AccountTest to test the account object.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest {
	private static Account accountOne = null;
	private static Account accountTwo = null;
	private static Account accountThree = null;
	private static Account accountFor = null;
	private static Account accountFive = null;
	private static Account accountSix = new Account("");
	private static Account accountSeven = null;
	private static Account accountEight = new Account("");
	private static Account accountNine = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		accountOne = new Account("123");
		accountTwo = new Account("222", new BigDecimal(100));
		accountThree = new Account(null, null);
		accountFor = new Account(null, new BigDecimal(100));
		accountFive = new Account("200", null);
		accountSeven = new Account("222", new BigDecimal(100));
		accountNine = new Account(null, null);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		accountOne = null;
		accountTwo = null;
		accountThree = null;
		accountFor = null;
		accountFive = null;
		accountSix = null;
		accountSeven = null;
		accountEight = null;
		accountNine = null;
	}

	@Test
	public void testGetters() {
		assertTrue("true", accountOne.getAccountId().equalsIgnoreCase("123"));
		assertFalse("false", accountOne.getAccountId().equalsIgnoreCase("12311"));
		assertTrue("true", accountTwo.getBalance().doubleValue() == 100d);
		assertFalse("false", accountFor.getBalance().doubleValue() == 1009d);
	}

	@Test
	public void testSetters() {
		accountSix.setBalance(new BigDecimal(100));
		accountSix.setBalance(new BigDecimal(-100));
		accountSix.setBalance(null);

	}

	@Test
	public void testEquals() {
		assertFalse(accountOne.equals(accountTwo));
		assertFalse(accountTwo.equals(accountThree));
		assertFalse(accountFor.equals(accountFive));
		assertFalse(accountFor.equals(new String("")));
		assertFalse(accountFor.equals(accountTwo));
		assertFalse(accountThree.equals(accountFive));

		assertFalse(accountThree.equals(accountNine));
		assertTrue(accountTwo.equals(accountTwo));
		assertFalse(accountTwo.equals(accountSeven));
		assertFalse(accountSix.equals(accountEight));

	}

	@Test
	public void testHCAndTS() {
		accountSeven.toString();
		accountTwo.hashCode();
		accountOne.hashCode();
		accountFor.hashCode();
		accountNine.hashCode();
	}

}
