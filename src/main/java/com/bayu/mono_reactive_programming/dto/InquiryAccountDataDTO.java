package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAccountDataDTO {

    private String settlementDate;

    private String beneficiaryAcctName;
    private String beneficiaryAcctNo;
    private String beneficiaryAcctType;
    private String beneficiaryType;
    private String beneficiaryId;
    private String beneficiaryResidentStatus;
    private String beneficiaryCityCode;

    private String userRefNoBi;
}
