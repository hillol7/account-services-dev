package com.db.awmd.challenge.web;

import com.db.awmd.challenge.constants.Messages;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.domain.ResponseWrapper;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.reqres.AccountRequestPOJO;
import com.db.awmd.reqres.AccountResponsePOJO;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

	@Setter
	private AccountsService accountsService;

	@Autowired
	public AccountsController(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper<AccountResponsePOJO>> createAccount(
			@RequestBody @Valid AccountRequestPOJO accountRequest) {

		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Creating account...", guid);
		ResponseWrapper<AccountResponsePOJO> response = new ResponseWrapper<>();
		response.setGuid(guid);
		AccountResponsePOJO accountResponse = null;

		try {
			Account accountReturned = accountsService.createAccount(accountRequest);
			accountResponse = new AccountResponsePOJO();
			accountResponse.setAccountId(accountReturned.getAccountId());
			accountResponse.setBalance(accountReturned.getBalance());
		} catch (Exception daie) {
			response.setData(null);
			response.setError(daie.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		response.setData(accountResponse);
		response.setError(null);
		log.info("GUID - [{}] Exiting create account {}", guid);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping(path = "/{accountId}")
	public ResponseEntity<ResponseWrapper<AccountResponsePOJO>> getAccount(@PathVariable @NotNull String accountId) {
		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Retrieving account for id {}", guid, accountId);
		ResponseWrapper<AccountResponsePOJO> response = new ResponseWrapper<>();
		response.setGuid(guid);
		Account account;
		if (accountId.equalsIgnoreCase("null")) {
			response.setData(null);
			response.setError(Messages.ACCOUNT_ID_CAN_NOT_BE_NULL.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		account = accountsService.getAccount(accountId);
		if (null == account) {
			response.setData(null);
			response.setError(Messages.NO_DATA_FOR_ACCOUNTS.getMessage());
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		AccountResponsePOJO accountResponse = new AccountResponsePOJO();
		accountResponse.setAccountId(accountId);
		accountResponse.setBalance(account.getBalance());
		response.setData(accountResponse);
		response.setError(null);
		log.info("GUID - [{}] Exiting Retrieving account for id {}", guid, accountId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper<Boolean>> transfer(@RequestBody @Valid AmountTransfer amount)
			throws EmptyDataException {
		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Transfering account to account {}", guid, amount);
		ResponseWrapper<Boolean> response = new ResponseWrapper<>();
		response.setGuid(guid);
		boolean returnFlag = false;
		try {
			returnFlag = accountsService.transferBetweenAccounts(amount);
		} catch (NegativeBalanceException nbe) {
			response.setData(false);
			response.setError(nbe.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (EmptyDataException ede) {
			response.setData(false);
			response.setError(ede.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		response.setData(returnFlag);
		response.setError(null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
// The below endpoints are for internal testing not meant for coding challenge
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseWrapper<List<Account>>> getAllAccount() {
		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Retrieving all accounts.", guid);
		ResponseWrapper<List<Account>> response = new ResponseWrapper<>();
		response.setGuid(guid);
		List<Account> listOfAccounts = accountsService.getAccountsRepository().getAllAccounts();
		
		response.setData(listOfAccounts);
		response.setError(null);
		log.info("GUID - [{}] Exiting Retrieving all accounts.", guid);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<ResponseWrapper<String>> delete(){
		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Clearing all accounts.", guid);
		ResponseWrapper<String> response = new ResponseWrapper<>();
		response.setGuid(guid);
		response.setData("Done.");
		response.setError(null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
