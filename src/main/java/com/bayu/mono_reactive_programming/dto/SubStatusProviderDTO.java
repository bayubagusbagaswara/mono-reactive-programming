package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubStatusProviderDTO {

    private String providerSystem;
    private String statusCode;
    private String statusMessage;

}
