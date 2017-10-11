package com.juliuskrah.quartz.web.rest.errors;

import java.io.Serializable;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableErrorVO.class)
@JsonDeserialize(as = ImmutableErrorVO.class)
public interface ErrorVO extends Serializable {
	String message();

	String description();

	List<FieldErrorVO> fieldErrors();
}
