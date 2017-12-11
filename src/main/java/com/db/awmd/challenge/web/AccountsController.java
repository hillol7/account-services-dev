package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.domain.ResponseWrapper;
import com.db.awmd.challenge.exception.EmptyDataException;
import com.db.awmd.challenge.exception.NegativeBalanceException;
import com.db.awmd.challenge.service.AccountsService;

import java.util.UUID;

import javax.validation.Valid;

import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<ResponseWrapper<Account>> createAccount(@RequestBody @Valid Account account) {

		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Creating account {}", guid, account);
		ResponseWrapper<Account> response = new ResponseWrapper<>();
		response.setGuid(guid);
		try {
			this.accountsService.createAccount(account);
		} catch (Exception daie) {
			response.setData(null);
			response.setError(daie.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		response.setData(account);
		response.setError(null);
		log.info("GUID - [{}] Exiting create account {}", guid, account);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping(path = "/{accountId}")
	public ResponseEntity<ResponseWrapper<Account>> getAccount(@PathVariable String accountId) {
		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Retrieving account for id {}", guid, accountId);
		ResponseWrapper<Account> response = new ResponseWrapper<>();
		response.setGuid(guid);
		Account account = accountsService.getAccount(accountId);
		if (null == account) {
			response.setData(null);
			response.setError(null);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		response.setData(account);
		response.setError(null);
		log.info("GUID - [{}] Exiting Retrieving account for id {}", guid, accountId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Synchronized
	public ResponseEntity<ResponseWrapper<String>> transfer(@RequestBody @Valid AmountTransfer amount)
			throws EmptyDataException {
		String guid = UUID.randomUUID().toString();
		log.info("GUID - [{}] Transfering account to account {}", guid, amount);
		ResponseWrapper<String> response = new ResponseWrapper<>();
		response.setGuid(guid);
		try {
			this.accountsService.transferBetweenAccounts(amount);
		} catch (NegativeBalanceException nbe) {
			response.setData(nbe.getMessage());
			response.setError(nbe.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		} catch (EmptyDataException ede) {
			response.setData(ede.getMessage());
			response.setError(ede.getMessage());
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		response.setData("Success.");
		response.setError(null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
