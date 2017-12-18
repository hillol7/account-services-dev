package com.db.awmd.challenge.domain;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResponseWrapperTest {
	private static ResponseWrapper<String> rw = new ResponseWrapper<>();

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rw.setData("TEST");
		rw.setError("TEST");
		rw.setGuid("TEST");
	}

	/**
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		rw = null;
	}

	/**
	 * Simple Setter
	 */
	@Test
	public void test() {
		rw.setData("TEST");
		rw.setError("TEST");
		rw.setGuid("TEST");
		rw.toString();

	}

}
