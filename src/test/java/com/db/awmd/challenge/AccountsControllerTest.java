package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.service.AccountsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper jsonMapper;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void createAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		Account account = accountsService.getAccount("Id-123");
		assertThat(account.getAccountId()).isEqualTo("Id-123");
		assertThat(account.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void createDuplicateAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("{\"balance\":1000}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBalance() throws Exception {
		this.mockMvc.perform(
				post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"Id-123\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBody() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNegativeBalance() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountEmptyAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void getAccount() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.balance").value(123.45));
	}
	
	@Test
	public void getAccountEmpty() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueAccountId+"RTE", new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.data").isEmpty());
	}

	@Test
	public void transferValid() throws Exception {
		String uniqueAccountId = "Id-1";
		Account account = new Account(uniqueAccountId, new BigDecimal("1000"));
		String uniqueAccountIdTwo = "Id-2";
		Account accountTwo = new Account(uniqueAccountIdTwo, new BigDecimal("1000"));
		this.accountsService.createAccount(account);
		this.accountsService.createAccount(accountTwo);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);
		this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").value("Success.")).andReturn();
	}

	@Test
	public void transferInValidFromAccountBalanceNull() throws Exception {
		String uniqueAccountId = "Id-1";
		new Account(uniqueAccountId, new BigDecimal("1000"));
		String uniqueAccountIdTwo = "Id-2";
		Account accountTwo = new Account(uniqueAccountIdTwo, new BigDecimal("1000"));
		this.accountsService.createAccount(accountTwo);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);
		this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.data").value("Empty Data.")).andReturn();
	}

	@Test
	public void transferInValidToAccountEmpty() throws Exception {
		String uniqueAccountId = "Id-1";
		Account account = new Account(uniqueAccountId, new BigDecimal("1000"));
		String uniqueAccountIdTwo = "Id-2";
		new Account(uniqueAccountIdTwo, new BigDecimal("1000"));
		this.accountsService.createAccount(account);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);
		this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.data").value("Empty Data.")).andReturn();
	}

	@Test
	public void transferInValidRequestPayload() throws Exception {
		String uniqueAccountId = "Id-1";
		Account account = new Account(uniqueAccountId, new BigDecimal("1000"));
		String uniqueAccountIdTwo = "Id-2";
		new Account(uniqueAccountIdTwo, new BigDecimal("1000"));
		this.accountsService.createAccount(account);
		AmountTransfer at = new AmountTransfer();
		at = null;
		this.mockMvc.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(at))).andExpect(status().isBadRequest());
	}

	@Test
	public void transferInValidFromAccountHasLessBalance() throws Exception {
		String uniqueAccountId = "Id-1";
		Account account = new Account(uniqueAccountId, new BigDecimal("10"));
		String uniqueAccountIdTwo = "Id-2";
		this.accountsService.createAccount(account);
		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);
		this.mockMvc
				.perform(put("/v1/accounts/transfer")
						.contentType(MediaType.APPLICATION_JSON).content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data")
						.value("Transfer operation encountered a problem as we do not support overdrafts!"))
				.andReturn();
	}

}
