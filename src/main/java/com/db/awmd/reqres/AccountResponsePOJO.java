package com.db.awmd.reqres;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({ "account_id", "balance" })
public class AccountResponsePOJO implements Serializable {

	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	@JsonProperty("accountId")
	@NotNull
	@NotEmpty
	private String accountId;
	@Getter
	@Setter
	@JsonProperty("balance")
	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	private BigDecimal balance;
}
