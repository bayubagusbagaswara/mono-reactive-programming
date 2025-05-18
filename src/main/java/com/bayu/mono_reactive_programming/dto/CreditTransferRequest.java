package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditTransferRequest {

    private String transactionType;
    private String category;
    private String settlementAmount;
    private String settlementCurrency;
    private String settlementDate;
    private String feeAmount;
    private String chargerBearerCode;
    private String senderAcctNo;
    private String senderAcctType;
    private String senderBic;

    private String beneficiaryBic;
    private String beneficiaryName;
    private String beneficiaryId;
    private String beneficiaryAcctNo;
    private String beneficiaryAcctType;

    private String proxyType;
    private String proxyValue;
    private String description;

    private String beneficiaryType;
    private String beneficiaryResidentStatus;
    private String beneficiaryCityCode;
    private String purposeTransaction;

    private String cardNo;

}
