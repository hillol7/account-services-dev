package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class AmountTransfer {

	@NotNull
	@NotEmpty
	@Getter
	@Setter
	String fromAccountId;

	@NotNull
	@NotEmpty
	@Getter
	@Setter
	String toAccountId;

	@NotNull
	@Min(value = 0, message = "Amount must be positive.")
	@Getter
	@Setter
	BigDecimal amount;

	@JsonCreator
	public AmountTransfer(@JsonProperty("fromAccountId") String fromAccountId,
			@JsonProperty("toAccountId") String toAccountId, @JsonProperty("amount") BigDecimal amount) {
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;

	}
}
