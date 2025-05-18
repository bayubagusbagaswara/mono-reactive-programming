package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAccountResponse {

    private String responseCode;
    private String responseMessage;

    private SubStatusProviderDTO subStatusProvider;

    private InquiryAccountDataDTO data;

    private HeaderResponseDTO headerResponse;

}
