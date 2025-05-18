package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransferResponse {

    private String responseCode;
    private String responseMessage;

    private CreditTransferDataDTO data;

    private SubStatusProviderDTO subStatusProvider;
    private HeaderResponseDTO headerResponse;
}
