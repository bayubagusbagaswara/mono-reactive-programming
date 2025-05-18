package com.bayu.mono_reactive_programming.service;

import com.bayu.mono_reactive_programming.dto.CreditTransferRequest;
import com.bayu.mono_reactive_programming.dto.CreditTransferResponse;
import com.bayu.mono_reactive_programming.dto.InquiryAccountRequest;
import com.bayu.mono_reactive_programming.dto.InquiryAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MiddlewareService {

    Mono<InquiryAccountResponse> inquiryAccount(InquiryAccountRequest inquiryAccountRequest, String externalId) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;

    Mono<CreditTransferResponse> creditTransfer(CreditTransferRequest creditTransferRequest, String referenceId) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException;

}
