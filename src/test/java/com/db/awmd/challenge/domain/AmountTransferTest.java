package com.db.awmd.challenge.domain;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmountTransferTest {
	private static AmountTransfer accountOne=null;
	private static AmountTransfer accountTwo=null;
	private static AmountTransfer accountThree=null;
	private static AmountTransfer accountFor=null;
	private static AmountTransfer accountFive=null;
	private static AmountTransfer accountSix=new AmountTransfer();
	private static AmountTransfer accountSeven=null;
	private static AmountTransfer accountEight=new AmountTransfer();
	private static AmountTransfer accountNine=null;
	private static AmountTransfer accountTen=null;
	private static String test="";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		accountOne = new AmountTransfer("123","222",new BigDecimal(100));
		accountTwo = new AmountTransfer("1234","2224",new BigDecimal(100));
		accountThree = new AmountTransfer("1235","2225",new BigDecimal(1001));
		accountFor = new AmountTransfer("1236","222",new BigDecimal(100));
		accountFive = new AmountTransfer("123",null,new BigDecimal(1003));
		accountSeven =new AmountTransfer(null,"2224",new BigDecimal(100));
		accountNine = new AmountTransfer(null,null,null);
		accountTen = new AmountTransfer(null,null,null);
	}
	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		accountOne=null;
		accountTwo=null;
		accountThree=null;
		accountFor=null;
		accountFive=null;
		accountSix=null;
		accountSeven=null;
		accountEight=null;
		accountNine=null;
		accountTen=null;
	}

	@Test
	public void testGetters() {
		assertTrue("true",accountOne.getAmount().doubleValue()==100d);
		assertTrue("false",accountOne.getFromAccountId().equalsIgnoreCase("123"));
		assertTrue("true",accountTwo.getToAccountId().equalsIgnoreCase("2224"));
	}
	
	@Test
	public void testSetters() {
		accountSix.setAmount(new BigDecimal(100));
		accountSix.setFromAccountId("123");
		accountSix.setToAccountId("222");
		
	}
	
	@Test
	public void testEquals() {
		/*assertFalse(accountOne.equals(accountTwo));
		assertFalse(accountTwo.equals(accountThree));
		assertFalse(accountFor.equals(accountFive));
		assertFalse(accountFor.equals(""));
		assertFalse(accountFor.equals(accountTwo));*/
		assertFalse(accountFor.equals(null));
		/*assertFalse(accountThree.equals(accountFive));
		assertFalse(accountFive.equals(accountSix));*/
		assertFalse(accountFive.equals(test));
		/*assertFalse(accountFive.equals(accountSeven));*/
		
		/*assertTrue(accountThree.equals(accountThree));*/
		assertTrue(accountNine.equals(accountTen));
		assertTrue(accountSix.equals(accountEight));
		
	}
	
	@Test
	public void testHCAndTS() {
		accountOne.toString();
		accountOne.hashCode();
		accountTen.hashCode();
	}

}
