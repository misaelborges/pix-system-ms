package com.financeiro.receipt.exception;

public class PdfGenerationException extends RuntimeException {
    public PdfGenerationException(String message) {
        super(message);
    }
}
