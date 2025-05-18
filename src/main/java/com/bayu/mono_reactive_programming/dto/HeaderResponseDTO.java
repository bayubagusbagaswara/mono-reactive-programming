package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderResponseDTO {

    private String correlationId;
    private String bdiExternalId;
    private String bdiTimestamp;
    private String channelId;
    private String date;
    private String providerSystem;
    private String serviceCode;
    private String serviceRequestId;
    private String via;
    private String statusCodeResponse;

}
