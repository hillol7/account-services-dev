package com.db.awmd.challenge.domain;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Account {

	@NotNull
	@NotEmpty
	@Getter
	private String accountId = "";

	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	@Getter
	private volatile BigDecimal balance=BigDecimal.valueOf(0);
	
	private final Lock lock;

	public Account(String accountId) {

		this.accountId = accountId;
		this.balance = BigDecimal.ZERO;
		this.lock = new ReentrantLock();
		}
	

	public Account(String accountId, BigDecimal balance) {

		this.accountId = accountId;
		this.balance = balance;
		this.lock = new ReentrantLock();


	}

	public int compare(Account object) {
		return this.accountId.compareTo(object.accountId);
	}
}
