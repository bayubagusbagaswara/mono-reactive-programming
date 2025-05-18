package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransferDataDTO {

    private String transactionType;
    private String payUserRefNo;
    private String infoStatus;
    private String settlementDate;
    private String beneficiaryName;
    private String beneficiaryType;
    private String beneficiaryId;
    private String beneficiaryResidentStatus;
    private String beneficiaryCityCode;
    private String cardNo;
    private String feeAmount;
    private String chargerBearerCode;
    private String userRefNoBi;

}
