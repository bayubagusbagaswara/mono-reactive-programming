package com.bayu.mono_reactive_programming.service;

import com.bayu.mono_reactive_programming.dto.CreditTransferRequest;
import com.bayu.mono_reactive_programming.dto.CreditTransferResponse;
import com.bayu.mono_reactive_programming.dto.InquiryAccountRequest;
import com.bayu.mono_reactive_programming.dto.InquiryAccountResponse;

public interface NCBSRequestService {

    InquiryAccountResponse inquiryAccount(InquiryAccountRequest inquiryAccountRequest, String externalId);

    CreditTransferResponse creditTransfer(CreditTransferRequest creditTransferRequest, String referenceId);

}
