package com.restaurant.management.service;

import com.restaurant.management.dto.invoice.InvoiceDetailResponse;
import com.restaurant.management.dto.invoice.InvoiceResponse;

public interface InvoiceService {
    InvoiceResponse generateInvoice(Long paymentId);
    InvoiceDetailResponse getInvoiceById(Long invoiceId);
    InvoiceDetailResponse getInvoiceByPaymentId(Long paymentId);
}
