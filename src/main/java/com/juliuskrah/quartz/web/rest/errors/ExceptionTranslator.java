package com.juliuskrah.quartz.web.rest.errors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionTranslator {

	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorVO processUnsupportedTriggerError(IllegalStateException ex) {
		ErrorVO dto = ImmutableErrorVO.builder()
				.message("400: Bad Request")
				.description(ex.getMessage())
				.build();
		return dto;
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(BAD_REQUEST)
	public ErrorVO processInvalidCronExpressionError(IllegalArgumentException ex) {
		ErrorVO dto = ImmutableErrorVO.builder()
				.message("400: Bad Request")
				.description(ex.getMessage())
				.build();
		return dto;
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorVO processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ImmutableErrorVO.Builder builder = ImmutableErrorVO.builder()
        		.message("400: Bad Request")
        		.description("Validation errors exist");
        
        for (FieldError fieldError : fieldErrors) {
            builder.addFieldErrors(ImmutableFieldErrorVO.builder()
            		.objectName(fieldError.getObjectName())
            		.field(fieldError.getField())
            		.message(fieldError.getCode())
            		.build());
        }
        return builder.build();
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(CONFLICT)
	public ErrorVO processDataIntegrityViolationError(DataIntegrityViolationException ex) {
		ErrorVO dto = ImmutableErrorVO.builder()
				.message("409: Conflict")
				.description(ex.getMessage())
				.build();
		return dto;
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorVO> processMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		ErrorVO dto = ImmutableErrorVO.builder()
				.message("405: Method Not Allowed")
				.description(ex.getMessage())
				.build();
        return ResponseEntity
        		.status(METHOD_NOT_ALLOWED)
        		.header("allow", ex.getSupportedMethods())
        		.body(dto);
    }
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorVO> processException(Exception ex) throws Exception {
	    if (log.isDebugEnabled()) {
            log.debug("An unexpected error occurred: {}", ex.getMessage(), ex);
        } else {
            log.error("An unexpected error occurred: {}", ex.getMessage());
        }
	    // If the exception is annotated with @ResponseStatus rethrow it and let
	    // the framework handle it 
	    // AnnotationUtils is a Spring Framework utility class.
	    if (AnnotationUtils.findAnnotation
	                (ex.getClass(), ResponseStatus.class) != null)
	        throw ex;
	    
	    ErrorVO dto = ImmutableErrorVO.builder()
				.message("500: Internal server error")
				.description("Internal server error has occurred")
				.build();
	    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(dto);
	  }
}
