package com.bayu.mono_reactive_programming.service.impl;

import com.bayu.mono_reactive_programming.dto.InquiryAccountRequest;
import com.bayu.mono_reactive_programming.dto.InquiryAccountResponse;
import com.bayu.mono_reactive_programming.service.MiddlewareService;
import com.bayu.mono_reactive_programming.service.NCBSRequestReactiveService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class NCBSRequestReactiveServiceImpl implements NCBSRequestReactiveService {

    private final MiddlewareService middlewareService;

    @Override
    public Mono<InquiryAccountResponse> inquiryAccount(InquiryAccountRequest request, String externalId) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();

        return middlewareService.inquiryAccount(request, externalId)
                .timeout(timeout)
                .doOnSuccess(res -> log.info("[Inquiry Account] Success - ExternalId: {} - Duration: {}ms",
                        externalId, System.currentTimeMillis() - startTime))
                .doOnError(e -> log.error("[Inquiry Account] Error during execution - ExternalId: {}", externalId, e))
                .doFinally(signalType -> log.debug("[Inquiry Account] Operation signal - ExternalId: {} - Signal: {}", externalId, signalType));
    }
}
