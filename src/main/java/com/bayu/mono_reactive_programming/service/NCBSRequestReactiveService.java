package com.bayu.mono_reactive_programming.service;

import com.bayu.mono_reactive_programming.dto.InquiryAccountRequest;
import com.bayu.mono_reactive_programming.dto.InquiryAccountResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface NCBSRequestReactiveService {

    Mono<InquiryAccountResponse> inquiryAccount(InquiryAccountRequest request, String externalId) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException;

}
