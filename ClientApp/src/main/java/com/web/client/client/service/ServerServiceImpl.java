package com.web.client.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class ServerServiceImpl implements ServerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerServiceImpl.class);

    @Autowired
    private WebClient webClient;

    private Retry<?> retryTimeoutOr5xxStatus = Retry.onlyIf( rc -> {
        return (rc.exception() instanceof WebClientResponseException &&
                ((WebClientResponseException) rc.exception()).getStatusCode().is5xxServerError())
                || rc.exception() instanceof TimeoutException;
    }).fixedBackoff(Duration.ofSeconds(2)).retryMax(2).doOnRetry(rc -> {
        LOGGER.info("The exception is : " + rc.exception());
    });

    @Override
    public String getResource() {
        return webClient.get().uri("/resource")
                .retrieve().bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(reactor.util.retry.Retry.backoff(2, Duration.ZERO)
                        .filter(ex -> is5xxServerError(ex) || ex instanceof TimeoutException)
                        .doAfterRetry(c -> {
                            LOGGER.info("Retry count : "+c.totalRetries());
                        }).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                is5xxServerError(retrySignal.failure())
                                        ? new IllegalArgumentException("500 exception")
                                        : new IllegalArgumentException("Timeout exception")))
                .block();
    }

    private boolean is5xxServerError(Throwable throwable) {
        return throwable instanceof WebClientResponseException &&
                ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
    }
}
