package com.bayu.mono_reactive_programming.controller;

import com.bayu.mono_reactive_programming.dto.InquiryAccountRequest;
import com.bayu.mono_reactive_programming.dto.InquiryAccountResponse;
import com.bayu.mono_reactive_programming.service.NCBSRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/ncbs")
public class NCBSRequestController {

    private final NCBSRequestService ncbsRequestService;

    public NCBSRequestController(NCBSRequestService ncbsRequestService) {
        this.ncbsRequestService = ncbsRequestService;
    }

//    @PostMapping("/inquiry-account")
//    public Mono<ResponseEntity<InquiryAccountResponse>> inquiryAccount(
//            @RequestBody InquiryAccountRequest request,
//            @RequestHeader("externalId") String externalId) {
//
//        return ncbsRequestService.inquiryAccount(request, externalId)
//                .map(response -> ResponseEntity.ok(response))
//                .timeout(Duration.ofSeconds(30))
//                .onErrorResume(WebClientResponseException.class, ex -> {
//                    // Tangani error dari server downstream
//                    InquiryAccountResponse errorResponse = InquiryAccountResponse.builder()
//                            .responseCode(String.valueOf(ex.getStatusCode().value()))
//                            .responseMessage("Server error: " + ex.getStatusText())
//                            .build();
//                    return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorResponse));
//                })
//                .onErrorResume(TimeoutException.class, ex -> {
//                    InquiryAccountResponse timeoutResponse = InquiryAccountResponse.builder()
//                            .responseCode("408")
//                            .responseMessage("Request timeout")
//                            .build();
//                    return Mono.just(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(timeoutResponse));
//                })
//                .onErrorResume(e -> {
//                    InquiryAccountResponse genericError = InquiryAccountResponse.builder()
//                            .responseCode("500")
//                            .responseMessage("Internal server error: " + e.getMessage())
//                            .build();
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericError));
//                });
//    }

//    Kalau ingin batasi concurrency (semaphore) di versi reactive bisa pakai operator flatMap dengan concurrency limit, misalnya:
//    Flux.fromIterable(requests)
//            .flatMap(req -> ncbsRequestService.inquiryAccount(req, externalId), 5) // maksimal 5 concurrent
//            .collectList();

}
