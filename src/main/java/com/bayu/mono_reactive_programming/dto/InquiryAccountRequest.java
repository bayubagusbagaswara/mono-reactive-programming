package com.bayu.mono_reactive_programming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAccountRequest {

    private String settlementAmount;
    private String settlementCurrency;
    private String chargerBearerCode;
    private String senderBic;
    private String senderAcctNo;
    private String beneficiaryBic;
    private String beneficiaryAcctNo;
    private String purposeTransaction;

}
