package com.juliuskrah.quartz.web.rest.errors;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableFieldErrorVO.class)
@JsonDeserialize(as = ImmutableFieldErrorVO.class)
public interface FieldErrorVO extends Serializable {
	String objectName();

	String field();

	String message();
}
