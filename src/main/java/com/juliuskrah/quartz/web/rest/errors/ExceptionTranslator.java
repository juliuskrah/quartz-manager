package com.juliuskrah.quartz.web.rest.errors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionTranslator {
    private Scheduler scheduler = Schedulers.elastic();

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorVO>> processUnsupportedTriggerError(IllegalStateException ex) {
        ErrorVO dto = ImmutableErrorVO.builder()
                .message("400: Bad Request")
                .description(ex.getMessage())
                .build();
        return Mono.just(
                ResponseEntity
                        .status(BAD_REQUEST)
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorVO>> processEmptyInputError(ServerWebInputException ex) {
        ErrorVO dto = ImmutableErrorVO.builder()
                .message("400: Bad Request")
                .description("Request body is missing")
                .build();
        return Mono.just(
                ResponseEntity
                        .status(BAD_REQUEST)
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorVO>> processInvalidCronExpressionError(IllegalArgumentException ex) {
        ErrorVO dto = ImmutableErrorVO.builder()
                .message("400: Bad Request")
                .description(ex.getMessage())
                .build();
        return Mono.just(
                ResponseEntity
                        .status(BAD_REQUEST)
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorVO>> processValidationError(WebExchangeBindException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        ImmutableErrorVO.Builder builder = ImmutableErrorVO.builder()
                .message("400: Bad Request")
                .description("Validation errors exist");

        for (FieldError fieldError : fieldErrors) {
            builder.addFieldErrors(ImmutableFieldErrorVO.builder()
                    .objectName(fieldError.getObjectName())
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }
        ErrorVO dto = builder.build();
        return Mono.just(
                ResponseEntity
                        .status(BAD_REQUEST)
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public Mono<ResponseEntity<ErrorVO>> processMethodNotAllowedError(MethodNotAllowedException ex) {
        // TODO This is not getting called. Investigate
        ErrorVO dto = ImmutableErrorVO.builder()
                .message("405: Method Not Allowed")
                .description(ex.getMessage())
                .build();
        return Mono.just(
                ResponseEntity
                        .status(METHOD_NOT_ALLOWED)
                        .allow(ex.getSupportedMethods().stream().toArray(HttpMethod[]::new))
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(NotAcceptableStatusException.class)
    public Mono<ResponseEntity<Void>> processnNotAcceptableError(NotAcceptableStatusException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(ex.getSupportedMediaTypes());
        return Mono.just(
                new ResponseEntity<Void> (headers, NOT_ACCEPTABLE)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorVO>> processDataIntegrityViolationError(DataIntegrityViolationException ex) {
        ErrorVO dto = ImmutableErrorVO.builder()
                .message("409: Conflict")
                .description(ex.getMessage())
                .build();
        return Mono.just(
                ResponseEntity
                        .status(CONFLICT)
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    public Mono<ResponseEntity<ErrorVO>> processUnsupportedMediaTypeError(UnsupportedMediaTypeStatusException ex) {
        ErrorVO dto = ImmutableErrorVO.builder()
                .message("415: Unsupported Media Type")
                .description(ex.getMessage())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(ex.getSupportedMediaTypes());
        return Mono.just(
                ResponseEntity
                        .status(UNSUPPORTED_MEDIA_TYPE)
                        .headers(headers)
                        .body(dto)
        ).subscribeOn(this.scheduler);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorVO>> processException(Exception ex) throws Exception {
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
        return Mono.just(
                ResponseEntity.status(INTERNAL_SERVER_ERROR).body(dto))
                .subscribeOn(this.scheduler);
    }
}
