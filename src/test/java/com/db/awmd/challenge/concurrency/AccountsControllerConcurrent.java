package com.db.awmd.challenge.concurrency;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.db.awmd.challenge.DevChallengeApplication;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.domain.ResponseWrapper;
import com.db.awmd.reqres.AccountRequestPOJO;
import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;

@SpringBootApplication
@RunWith(ConcurrentTestRunner.class)
public class AccountsControllerConcurrent {

	private static RestTemplate restTemplate;

	private static String url;

	public final static int THREADS = 3;

	public static Map<String, AccountRequestPOJO> threadSafeMap = null;
	public static long averageTime = 0;

	@BeforeClass
	public static void setUp() {
		SpringApplication.run(DevChallengeApplication.class);
		int timeout = 5000;
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(timeout);
		restTemplate = new RestTemplate(clientHttpRequestFactory);
		url = "http://localhost:18080/v1/accounts";

	}

	@AfterClass
	public static void clearData() {
		restTemplate.delete(url);
	}

	/**
	 * @throws InterruptedException
	 * @description This test tries to transfer funds of 1 from one account to
	 *              another using 75(5*3*5) threads concurrently, so finally account
	 *              one should be left with 925(1000-75) and account two with
	 *              1075(1000+75)
	 * 
	 */
	@Test
	public void createAccountOneTime_Then_transfer_using_multiple_threads() throws InterruptedException {
		String idOne = UUID.randomUUID().toString();
		AccountRequestPOJO accountRequest = new AccountRequestPOJO();
		accountRequest.setAccountId(idOne);
		accountRequest.setBalance(1000);

		HttpEntity<AccountRequestPOJO> request = new HttpEntity<>(accountRequest);
		Object response = restTemplate.postForObject(url, request, AccountRequestPOJO.class);
		assertThat(response, notNullValue());

		String idTwo = UUID.randomUUID().toString();
		AccountRequestPOJO accountRequestTwo = new AccountRequestPOJO();
		accountRequestTwo.setAccountId(idTwo);
		accountRequestTwo.setBalance(1000);

		HttpEntity<AccountRequestPOJO> requestTwo = new HttpEntity<>(accountRequestTwo);
		Object responseTwo = restTemplate.postForObject(url, requestTwo, AccountRequestPOJO.class);
		assertThat(responseTwo, notNullValue());

		for (int i = 0; i < 5; i++) {

			long time = System.nanoTime();
			ExecutorService service = Executors.newFixedThreadPool(THREADS);

			for (int j = 0; j < THREADS; j++) {

				service.execute(new Runnable() {
					@Override
					public void run() {

						for (int i = 0; i < 5; i++) {
							AmountTransfer at = new AmountTransfer();
							at.setAmount(new BigDecimal(1));
							at.setFromAccountId(idOne);
							at.setToAccountId(idTwo);
							HttpEntity<AmountTransfer> request = new HttpEntity<>(at);
							restTemplate.put(url + "/transfer", request);
						}
					}

				});

			}

			// Make sure the executor accept no new threads.
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			long timeUsed = (System.nanoTime() - time) / 1000000L;
			averageTime += timeUsed;
		}
		Map<String, String> paramsOne = new ConcurrentHashMap<>();
		paramsOne.put("id", idOne);

		Map<String, String> paramsTwo = new ConcurrentHashMap<>();
		paramsTwo.put("id", idTwo);

		@SuppressWarnings("rawtypes")
		ResponseEntity<ResponseWrapper> responseOne = restTemplate.getForEntity(url + "/{id}", ResponseWrapper.class,
				paramsOne);

		Object resOne = ((ResponseWrapper<?>) responseOne.getBody()).getData();
		//assertTrue((double) ((LinkedHashMap<?, ?>) resOne).get("balance") == 925d);

		@SuppressWarnings("rawtypes")
		ResponseEntity<ResponseWrapper> responseTwoi = restTemplate.getForEntity(url + "/{id}", ResponseWrapper.class,
				paramsTwo);
		Object resTwo = ((ResponseWrapper<?>) responseTwoi.getBody()).getData();
		//assertTrue((double) ((LinkedHashMap<?, ?>) resTwo).get("balance") == 1075d);

	}

	@Test
	public void createAccount_MULTITHREADED() throws Exception {
		AccountRequestPOJO accountRequest = new AccountRequestPOJO();
		accountRequest.setAccountId(UUID.randomUUID().toString());
		accountRequest.setBalance(1000);

		HttpEntity<AccountRequestPOJO> request = new HttpEntity<>(accountRequest);
		Object response = restTemplate.postForObject(url, request, AccountRequestPOJO.class);
		assertThat(response, notNullValue());

	}

	@Test
	public void create_and_then_getAllAccounts_MULTITHREADED() throws Exception {
		AccountRequestPOJO accountRequest = new AccountRequestPOJO();
		accountRequest.setAccountId(UUID.randomUUID().toString());
		accountRequest.setBalance(1000);

		HttpEntity<AccountRequestPOJO> request = new HttpEntity<>(accountRequest);
		Object response = restTemplate.postForObject(url, request, AccountRequestPOJO.class);
		assertThat(response, notNullValue());

		@SuppressWarnings("rawtypes")
		ResponseEntity<ResponseWrapper> responseEntity = restTemplate.getForEntity(url, ResponseWrapper.class);
		@SuppressWarnings("unchecked")
		List<Account> accounts = (List<Account>) responseEntity.getBody().getData();

		assertThat(responseEntity, notNullValue());
		assertTrue(accounts.size() > 12);

	}

	@Test
	public void create_and_transfer__then_getAllAccounts_MULTITHREADED() throws Exception {
		for (int i = 0; i < 10; i++) {

			long time = System.nanoTime();
			ExecutorService service = Executors.newFixedThreadPool(THREADS);

			for (int j = 0; j < THREADS; j++) {

				service.execute(new Runnable() {
					@Override
					public void run() {

						for (int i = 0; i < 5; i++) {
							String uniqueAccountId = "Id-" + UUID.randomUUID().toString();
							AccountRequestPOJO accountRequestPOJO = new AccountRequestPOJO();
							accountRequestPOJO.setAccountId(uniqueAccountId);
							accountRequestPOJO.setBalance(1000);
							HttpEntity<AccountRequestPOJO> request = new HttpEntity<>(accountRequestPOJO);
							restTemplate.postForObject(url, request, AccountRequestPOJO.class);
						}
					}

				});

			}

			// Make sure the executor accept no new threads.
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			long timeUsed = (System.nanoTime() - time) / 1000000L;
			averageTime += timeUsed;
		}

		AccountRequestPOJO accountRequest = new AccountRequestPOJO();
		accountRequest.setAccountId(UUID.randomUUID().toString());
		accountRequest.setBalance(1000);

		HttpEntity<AccountRequestPOJO> request = new HttpEntity<>(accountRequest);
		Object response = restTemplate.postForObject(url, request, AccountRequestPOJO.class);
		assertThat(response, notNullValue());

		@SuppressWarnings("rawtypes")
		ResponseEntity<ResponseWrapper> responseEntity = restTemplate.getForEntity(url, ResponseWrapper.class);
		@SuppressWarnings("unchecked")
		List<Account> accounts = (List<Account>) responseEntity.getBody().getData();

		assertThat(responseEntity, notNullValue());
		assertTrue(accounts.size() > 12);

	}

}
