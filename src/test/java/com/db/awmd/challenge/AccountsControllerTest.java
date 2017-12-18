package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.db.awmd.challenge.constants.Messages;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.web.AccountsController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

	private MockMvc mockMvc;

	@Mock
	private AccountsService accountsService;

	@Autowired
	private AccountsController accountsController;

	@Autowired
	private ObjectMapper jsonMapper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		accountsController.setAccountsService(accountsService);
		this.mockMvc = MockMvcBuilders.standaloneSetup(accountsController).build();

	}

	@Test
	public void createAccount() throws Exception {

		Account account = new Account("Id-123", new BigDecimal(1000));
		when(accountsService.createAccount(anyObject())).then(((invocation) -> {
			return account;
		}));
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		when(accountsService.getAccount(eq("Id-123"))).then(((invocation) -> {
			return account;
		}));

		Account accountReturned = accountsService.getAccount("Id-123");
		assertThat(accountReturned.getAccountId()).isEqualTo("Id-123");
		assertThat(accountReturned.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void createDuplicateAccount() throws Exception {

		Account account = new Account("Id-123", new BigDecimal(1000));

		when(accountsService.createAccount(anyObject())).then(((invocation) -> {
			return account;
		}));
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		when(accountsService.createAccount(anyObject())).then(((invocation) -> {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}));
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

		Account account = new Account("Id-123", new BigDecimal(0));

		when(accountsService.createAccount(anyObject())).then(((invocation) -> {
			return account;
		}));
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":0}")).andExpect(status().isCreated());

		when(accountsService.getAccount(eq("Id-123"))).then(((invocation) -> {
			return account;
		}));

		Account accountReturned = accountsService.getAccount("Id-123");
		assertThat(accountReturned.getAccountId()).isEqualTo("Id-123");
		assertThat(accountReturned.getBalance()).isEqualByComparingTo("0");
	}
	
	@Test
	public void createAccountNullRequest() throws Exception {

		Account account = new Account("Id-123", new BigDecimal(0));

		when(accountsService.createAccount(anyObject())).then(((invocation) -> {
			return account;
		}));
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
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
		Account account = new Account(uniqueAccountId, new BigDecimal(123.45));

		when(accountsService.getAccount(eq(uniqueAccountId))).then(((invocation) -> {
			return account;
		}));
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.balance").value(123.45));
		verify(accountsService, times(1)).getAccount(uniqueAccountId);
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void getAccountEmpty() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();

		when(accountsService.getAccount(eq(uniqueAccountId))).then(((invocation) -> {
			return null;
		}));
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.data").isEmpty());

		verify(accountsService, times(1)).getAccount(uniqueAccountId);
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void getAccount_ValidationFailedWithEmptyAccountID() throws Exception {

		this.mockMvc.perform(get("/v1/accounts/" + null)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").isEmpty())
				.andExpect(jsonPath("$.error").value(Messages.ACCOUNT_ID_CAN_NOT_BE_NULL.getMessage()));

		verify(accountsService, times(0)).getAccount(anyString());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void getAccount_ValidationFailedWithEmptyAccount_ReturnedFromService() throws Exception {

		when(accountsService.getAccount(anyString())).then(((invocation) -> {
			return null;
		}));
		this.mockMvc.perform(get("/v1/accounts/" + "123")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.data").isEmpty()).andExpect(jsonPath("$.error").value("No data for account."));

		verify(accountsService, times(1)).getAccount(anyString());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void transferValid() throws Exception {
		String uniqueAccountId = "Id-1";
		String uniqueAccountIdTwo = "Id-2";

		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);
		boolean flag = true;
		when(accountsService.transferBetweenAccounts(anyObject())).then(((invocation) -> {
			return flag;
		}));
		this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true))
				.andExpect(jsonPath("$.error").isEmpty());
		verify(accountsService, times(1)).transferBetweenAccounts(anyObject());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void transfer_NOT_SUCCESSFUL_Negative_Balance() throws Exception {
		String uniqueAccountId = "Id-1";
		String uniqueAccountIdTwo = "Id-2";

		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);
		boolean flag = false;

		when(accountsService.transferBetweenAccounts(anyObject())).then(((invocation) -> {
			throw new NegativeBalanceException(Messages.TRANSFER_OPERATION_ENCOUNTERED_A_PROBLEM.getMessage());
		}));

		mockMvc.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(at))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.data").value(flag))
				.andExpect(jsonPath("$.error").value(Messages.TRANSFER_OPERATION_ENCOUNTERED_A_PROBLEM.getMessage()));
		verify(accountsService, times(1)).transferBetweenAccounts(anyObject());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void transfer_NOT_SUCCESSFUL_JSON_CAN_NOT_BE_NULL_OR_EMPTY() throws Exception {

		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(null);
		at.setToAccountId(null);

		mockMvc.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(at))).andExpect(status().isBadRequest());
		verify(accountsService, times(0)).transferBetweenAccounts(anyObject());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void transferInValid_EmptyDataException_TRANSFERER_CAN_NOT_BE_NULL() throws Exception {

		String uniqueAccountId = "Id-1";
		String uniqueAccountIdTwo = "Id-2";

		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		when(accountsService.transferBetweenAccounts(anyObject())).then(((invocation) -> {
			throw new EmptyDataException(Messages.TRANSFERER_CAN_NOT_BE_NULL.getMessage());
		}));
		this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(Messages.TRANSFERER_CAN_NOT_BE_NULL.getMessage()));

		verify(accountsService, times(1)).transferBetweenAccounts(anyObject());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void transferInValid_EmptyDataException_PAYEE_CAN_NOT_BE_NULL() throws Exception {

		String uniqueAccountId = "Id-1";
		String uniqueAccountIdTwo = "Id-2";

		AmountTransfer at = new AmountTransfer();
		at.setAmount(new BigDecimal(100));
		at.setFromAccountId(uniqueAccountId);
		at.setToAccountId(uniqueAccountIdTwo);

		when(accountsService.transferBetweenAccounts(anyObject())).then(((invocation) -> {
			throw new EmptyDataException(Messages.PAYEE_CAN_NOT_BE_NULL.getMessage());
		}));
		this.mockMvc
				.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
						.content(jsonMapper.writeValueAsBytes(at)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(Messages.PAYEE_CAN_NOT_BE_NULL.getMessage()));

		verify(accountsService, times(1)).transferBetweenAccounts(anyObject());
		verifyNoMoreInteractions(accountsService);
	}

	@Test
	public void transferInValidRequestPayload() throws Exception {
		AmountTransfer at = new AmountTransfer();
		at = null;
		this.mockMvc.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
				.content(jsonMapper.writeValueAsBytes(at))).andExpect(status().isBadRequest());
	}

}
