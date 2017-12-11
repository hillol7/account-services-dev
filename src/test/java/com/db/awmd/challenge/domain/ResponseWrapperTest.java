package com.db.awmd.challenge.domain;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResponseWrapperTest {
	private static ResponseWrapper<String> rw = new ResponseWrapper<>();
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		rw.setData("TEST");
		rw.setError("TEST");
		rw.setGuid("TEST");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		rw=null;
	}

	@Test
	public void test() {
		rw.setData("TEST");
		rw.setError("TEST");
		rw.setGuid("TEST");
		rw.toString();
		
	}

}
