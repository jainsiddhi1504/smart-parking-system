package com.siddhi.smartparking.service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import java.io.File;

import java.io.File;

@Service
public class PdfReceiptService {

    public File generateReceipt(
            String vehicleNumber,
            String ownerName,
            String slotNumber,
            double fee
    ) throws Exception {

        String fileName =
                "receipt_" + vehicleNumber + ".pdf";

        PdfWriter writer =
                new PdfWriter(fileName);

        PdfDocument pdf =
                new PdfDocument(writer);

        Document document =
                new Document(pdf);

        document.add(
                new Paragraph("SMART PARKING RECEIPT")
        );

        document.add(
                new Paragraph("----------------------------")
        );

        document.add(
                new Paragraph(
                        "Owner: " + ownerName
                )
        );

        document.add(
                new Paragraph(
                        "Vehicle: " + vehicleNumber
                )
        );

        document.add(
                new Paragraph(
                        "Slot: " + slotNumber
                )
        );

        document.add(
                new Paragraph(
                        "Parking Fee: ₹" + fee
                )
        );

        document.close();

        return new File(fileName);
    }
}