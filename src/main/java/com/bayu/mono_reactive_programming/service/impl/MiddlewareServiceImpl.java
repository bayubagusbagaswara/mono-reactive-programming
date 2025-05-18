package com.bayu.mono_reactive_programming.service.impl;

import com.bayu.mono_reactive_programming.dto.*;
import com.bayu.mono_reactive_programming.service.MiddlewareService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class MiddlewareServiceImpl implements MiddlewareService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // **General Header
    @Value("${api.secret-key}")
    private String secretKey;
    @Value("${api.bdi-key}")
    private String apiBDIKey;

    // **Inquiry Account Header
    @Value("${api.inquiry-account.bdi-channel}")
    private String inquiryAccountBDIChannel;
    @Value("${api.inquiry-account.bdi-service-code}")
    private String inquiryAccountBDIServiceCode;
    @Value("${api.inquiry-account-url}")
    private String inquiryAccountUrl;

    // **Credit Transfer Header
    @Value("${api.credit-transfer.bdi-channel}")
    private String creditTransferBDIChannel;
    @Value("${api.credit-transfer.bdi-service-code}")
    private String creditTransferBDIServiceCode;
    @Value("${api.credit-transfer-url}")
    private String creditTransferUrl;

    @Override
    public Mono<InquiryAccountResponse> inquiryAccount(InquiryAccountRequest inquiryAccountRequest, String externalId) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String jsonRequestBody = objectMapper.writeValueAsString(inquiryAccountRequest);
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");
        String currentTimestamp = getCurrentTimestamp();
        String dataToSign = currentTimestamp + processedRequestBody;
        String signature = computeHMACSHA512(dataToSign, secretKey);

        logRequestHeader("Inquiry Account",  inquiryAccountUrl, apiBDIKey, externalId, inquiryAccountBDIChannel, inquiryAccountBDIServiceCode);
        logRequestBody("Inquiry Account", processedRequestBody, currentTimestamp, dataToSign, signature);

        return webClient.post()
                .uri(inquiryAccountUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(header -> buildHeaders(header, externalId, currentTimestamp, inquiryAccountBDIChannel, inquiryAccountBDIServiceCode, signature))
                .bodyValue(inquiryAccountRequest)
                .exchangeToMono(response -> {
                    HttpHeaders httpHeaders = response.headers().asHttpHeaders();
                    httpHeaders.forEach((key, value) ->
                            log.info("[Inquiry Account] Header response: {} = {}", key, value)
                    );

                    HttpStatusCode statusCode = response.statusCode();
                    log.info("[Inquiry Account] Status code response: {}", statusCode);

                    HeaderResponseDTO headerResponse = getHeaderResponseDTO(httpHeaders, statusCode);

                    return response.bodyToMono(InquiryAccountResponse.class)
                            .map(body -> {
                                body.setHeaderResponse(headerResponse);
                                return body;
                            })
                            .switchIfEmpty(Mono.just(
                                    InquiryAccountResponse.builder()
                                            .headerResponse(headerResponse)
                                            .build()
                            ));

                })
                .onErrorResume(e -> {
                    log.error("[Inquiry Account] Error: ", e);
                    return Mono.just(
                            InquiryAccountResponse.builder()
                                    .responseCode("500")
                                    .responseMessage("[Inquiry Account] Service error: " + e.getMessage())
                                    .build()
                    );
                })
                ;
    }

    @Override
    public Mono<CreditTransferResponse> creditTransfer(CreditTransferRequest creditTransferRequest, String referenceId) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        String jsonRequestBody = objectMapper.writeValueAsString(creditTransferRequest);
        String processedRequestBody = jsonRequestBody.replaceAll("\\s", "");
        String currentTimestamp = getCurrentTimestamp();
        String dataToSign = currentTimestamp + processedRequestBody;
        String signature = computeHMACSHA512(dataToSign, secretKey);

        logRequestHeader("Credit Transfer",  creditTransferUrl, apiBDIKey, referenceId, creditTransferBDIChannel, creditTransferBDIServiceCode);
        logRequestBody("Credit Transfer", processedRequestBody, currentTimestamp, dataToSign, signature);

        return webClient.post()
                .uri(creditTransferUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(header -> buildHeaders(header, referenceId, currentTimestamp, creditTransferBDIChannel, creditTransferBDIServiceCode, signature))
                .bodyValue(creditTransferRequest)
                .exchangeToMono(response -> {
                    HttpHeaders httpHeaders = response.headers().asHttpHeaders();
                    httpHeaders.forEach((key, value) ->
                            log.info("[Credit Transfer] Header response: {} = {}", key, value)
                    );

                    HttpStatusCode statusCode = response.statusCode();
                    log.info("[Credit Transfer] Status code response: {}", statusCode);

                    HeaderResponseDTO headerResponse = getHeaderResponseDTO(httpHeaders, statusCode);

                    return response.bodyToMono(CreditTransferResponse.class)
                            .map(body -> {
                                body.setHeaderResponse(headerResponse);
                                return body;
                            })
                            .switchIfEmpty(Mono.just(
                                    CreditTransferResponse.builder()
                                            .headerResponse(headerResponse)
                                            .build()
                            ));
                })
                .onErrorResume(e -> {
                    log.error("[Credit Transfer] Error: ", e);
                    return Mono.just(
                            CreditTransferResponse.builder()
                                    .responseCode("500")
                                    .responseMessage("[Credit Transfer] Service error: " + e.getMessage())
                                    .build()
                    );
                })
                ;
    }

    private static HeaderResponseDTO getHeaderResponseDTO(HttpHeaders httpHeaders, HttpStatusCode statusCode) {
        return HeaderResponseDTO.builder()
                .correlationId(httpHeaders.getFirst("CORRELATION_ID"))
                .bdiExternalId(httpHeaders.getFirst("BDI_EXTERNAL_ID"))
                .bdiTimestamp(httpHeaders.getFirst("BDI_TIMESTAMP"))
                .channelId(httpHeaders.getFirst("CHANNEL_ID"))
                .date(httpHeaders.getFirst("DATE"))
                .providerSystem(httpHeaders.getFirst("PROVIDER_SYSTEM"))
                .serviceCode(httpHeaders.getFirst("SERVICE_CODE"))
                .serviceRequestId(httpHeaders.getFirst("SERVICE_REQUEST_ID"))
                .via(httpHeaders.getFirst(HttpHeaders.VIA))
                .statusCodeResponse(String.valueOf(statusCode))
                .build();
    }

    private void buildHeaders(org.springframework.http.HttpHeaders headers, String referenceId, String timestamp, String channelId, String serviceCode, String signature) {
        headers.add("BDI_KEY", apiBDIKey);
        headers.add("BDI_EXTERNAL_ID", referenceId);
        headers.add("BDI_TIMESTAMP", timestamp);
        headers.add("BDI_CHANNEL", channelId);
        headers.add("BDI_SERVICE_CODE", serviceCode);
        headers.add("BDI_SIGNATURE", signature);
    }

    private static String getCurrentTimestamp() {
        String currentTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX"));
        log.info("Get current timestamp: {}", currentTimestamp);
        return currentTimestamp;
    }

    private static String computeHMACSHA512(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    private void logRequestHeader(String serviceType, String url, String bdiKey, String externalId, String channel, String serviceCode) {
        log.info("[{}] URL: {}", serviceType, url);
        log.info("[{}] BDI Key: {}", serviceType, bdiKey);
        log.info("[{}] External Id: {}", serviceType, externalId);
        log.info("[{}] Channel: {}", serviceType, channel);
        log.info("[{}] Service Code: {}", serviceType, serviceCode);
    }

    private void logRequestBody(String serviceType, String processedRequestBody, String currentTimestamp, String dataToSign, String signature) {
        log.info("[{}] Request Body: {}", serviceType, processedRequestBody);
        log.info("[{}] Current timestamp: {}", serviceType, currentTimestamp);
        log.info("[{}] Data to sign: {}", serviceType, dataToSign);
        log.info("[{}] Signature: {}", serviceType, signature);
    }
}
