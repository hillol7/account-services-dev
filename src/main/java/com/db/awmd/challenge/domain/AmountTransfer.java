package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude={"fromAccountId", "toAccountId","amount"})
public class AmountTransfer {

	@NotNull
	@NotEmpty
	String fromAccountId;

	@NotNull
	@NotEmpty
	String toAccountId;
	@NotNull
	@Min(value = 0, message = "Amount must be positive.")
	BigDecimal amount;
	
	@JsonCreator
	public AmountTransfer(@JsonProperty("fromAccountId") String fromAccountId, @JsonProperty("toAccountId") String toAccountId, @JsonProperty("amount") BigDecimal amount) {
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
		this.amount = amount;
		
	}
}
