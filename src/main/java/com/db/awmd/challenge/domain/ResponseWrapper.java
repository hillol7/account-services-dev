package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Setter;


public class ResponseWrapper<T> {
	@JsonPropertyOrder({"guid","data","error"})
	@JsonProperty("guid")
	@Setter
	String guid;
	@JsonProperty("data")
	@Setter
	T data;
	@JsonProperty("error")
	@Setter
	String error;
	

}
