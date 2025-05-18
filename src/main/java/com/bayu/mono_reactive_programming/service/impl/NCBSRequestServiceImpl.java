package com.bayu.mono_reactive_programming.service.impl;

import com.bayu.mono_reactive_programming.dto.CreditTransferRequest;
import com.bayu.mono_reactive_programming.dto.CreditTransferResponse;
import com.bayu.mono_reactive_programming.dto.InquiryAccountRequest;
import com.bayu.mono_reactive_programming.dto.InquiryAccountResponse;
import com.bayu.mono_reactive_programming.exception.GeneralException;
import com.bayu.mono_reactive_programming.service.MiddlewareService;
import com.bayu.mono_reactive_programming.service.NCBSRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
@RequiredArgsConstructor
public class NCBSRequestServiceImpl implements NCBSRequestService {

    private final MiddlewareService middlewareService;
    private final Semaphore semaphore = new Semaphore(10); // max 10 concurrent calls

    @Override
    public InquiryAccountResponse inquiryAccount(InquiryAccountRequest inquiryAccountRequest, String externalId) {
        log.info("[Inquiry Account] Start - ExternalId: {}", externalId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();

        boolean acquired = false;
        try {
            semaphore.acquire();
            acquired = true;

            InquiryAccountResponse response = middlewareService.inquiryAccount(inquiryAccountRequest, externalId)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res -> log.info("[Inquiry Account] Success - ExternalId: {} - Duration: {}ms",
                            externalId, System.currentTimeMillis() - startTime))
                    .doOnError(e -> log.error("[Inquiry Account] Error during execution - ExternalId: {}", externalId, e))
                    .doFinally(signalType -> log.debug("[Inquiry Account] Operation signal - ExternalId: {} - Signal: {}",
                            externalId, signalType))
                    .block(timeout);

            log.info("[Inquiry Account] Success - ExternalId: {}", externalId);
            return response;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore the interrupted status
            log.error("[Inquiry Account] Interrupted while acquiring semaphore - ExternalId: {}", externalId, e);
            throw new GeneralException("[Inquiry Account] Thread interrupted while waiting for execution permit", e);

        } catch (WebClientResponseException e) {
            log.error("[Inquiry Account] Server Error - ExternalId: {} - Status: {} - Body: {}",
                    externalId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Inquiry Account] Server error: " + e.getStatusCode(), e);

        } catch (IllegalStateException e) {
            log.error("[Inquiry Account] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Inquiry Account] Timeout on blocking read for " + timeout.getSeconds() + " seconds", e);

        } catch (Exception e) {
            log.error("[Inquiry Account] Failed - ExternalId: {} - Error: {}", externalId, e.getMessage(), e);
            throw new GeneralException("[Inquiry Account] Request failed: " + e.getMessage(), e);

        } finally {
            if (acquired) {
                semaphore.release();
            }
            log.info("[Inquiry Account] Completed - ExternalId: {} - Total Duration: {}ms",
                    externalId, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public CreditTransferResponse creditTransfer(CreditTransferRequest creditTransferRequest, String referenceId) {
        log.info("[Credit Transfer] Start - ReferenceId: {}", referenceId);
        final Duration timeout = Duration.ofSeconds(30);
        final long startTime = System.currentTimeMillis();
        try {
            CreditTransferResponse response = middlewareService.creditTransfer(creditTransferRequest, referenceId)
                    .subscribeOn(Schedulers.boundedElastic())
                    .timeout(timeout)
                    .doOnSuccess(res ->
                            log.info("[Credit Transfer] Success - ReferenceId: {} - Duration: {}ms",
                                    referenceId, System.currentTimeMillis() - startTime))
                    .doOnError(e ->
                            log.error("[Credit Transfer] Error during execution - ReferenceId: {}",
                                    referenceId, e))
                    .doFinally(signalType ->
                            log.debug("[Credit Transfer] Operation signal - ReferenceId: {} - Signal: {}",
                                    referenceId, signalType))
                    .block(timeout);
            log.info("[Credit Transfer] Success - ReferenceId: {}", referenceId);
            return response;
        } catch (WebClientResponseException e) {
            log.error("[Credit Transfer] Server error - ReferenceId: {} - Status: {} - Body: {}",
                    referenceId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new GeneralException("[Credit Transfer] Server error: " + e.getStatusCode());
        } catch (IllegalStateException e) {
            log.error("[Credit Transfer] Timeout: {}", e.getMessage(), e);
            throw new GeneralException("[Credit Transfer] Timeout on blocking read for " + timeout.get(ChronoUnit.SECONDS) + " second");
        } catch (Exception e) {
            log.error("[Credit Transfer] Failed - ReferenceId: {} - Error: {}",
                    referenceId, e.getMessage(), e);
            throw new GeneralException("[Credit Transfer] Request failed: " + e.getMessage());
        } finally {
            log.info("[Credit Transfer] Completed - ReferenceId: {} - Total Duration: {}ms",
                    referenceId, System.currentTimeMillis() - startTime);
        }
    }

}
