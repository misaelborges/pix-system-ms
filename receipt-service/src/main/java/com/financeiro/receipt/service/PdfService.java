package com.financeiro.receipt.service;

import com.financeiro.receipt.event.PaymentCompletedEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateReceipt(PaymentCompletedEvent paymentCompletedEvent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter pdfWriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);

        LocalDateTime dateTime = LocalDateTime.parse(paymentCompletedEvent.createdAt(), DateTimeFormatter.ISO_DATE_TIME);
        String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        document.add(new Paragraph("Transação Pix System - MS").setFontSize(16).setUnderline().simulateBold());
        document.add(new Paragraph("Data: " + formatted));

        document.add(new Paragraph(""));

        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth().setMarginTop(10).setMarginBottom(10);

        table.addCell(new Cell().add(new Paragraph("Número da transação:").setFontSize(16).setUnderline().simulateBold()));
        table.addCell(paymentCompletedEvent.transactionId());

        table.addCell(new Cell().add(new Paragraph("Pix recebido de:").setFontSize(16).setUnderline().simulateBold()));
        table.addCell(String.valueOf(paymentCompletedEvent.receiverAccountId()));

        table.addCell(new Cell().add(new Paragraph("Valor:").setFontSize(16).setUnderline().simulateBold()));
        table.addCell("R$ " + String.format("%.2f", paymentCompletedEvent.amount()));

        document.add(table);

        document.close();

        return outputStream.toByteArray();
    }
}
