package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Account {

	@NotNull
	@NotEmpty
	@Getter(onMethod = @__({ @Synchronized }))
	private final String accountId;

	@NotNull
	@Min(value = 0, message = "Initial balance must be positive.")
	@Getter(onMethod = @__({ @Synchronized }))
	@Setter(onMethod = @__({ @Synchronized }))
	private BigDecimal balance;

	public Account(String accountId) {
		synchronized ($lock) {
			this.accountId = accountId;
			this.balance = BigDecimal.ZERO;
		}
	}

	@JsonCreator
	public Account(@JsonProperty("accountId") String accountId, @JsonProperty("balance") BigDecimal balance) {
		synchronized ($lock) {
			this.accountId = accountId;
			this.balance = balance;
		}
	}
}
