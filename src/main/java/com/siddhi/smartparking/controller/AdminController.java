package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.service.AdminService;
import com.siddhi.smartparking.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.siddhi.smartparking.service.PdfReceiptService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final EmailService emailService;
    private final PdfReceiptService pdfReceiptService;

    public AdminController(
            AdminService adminService,
            EmailService emailService,
            PdfReceiptService pdfReceiptService
    ) {
        this.adminService = adminService;
        this.emailService = emailService;
        this.pdfReceiptService = pdfReceiptService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {

        System.out.println("INSIDE CONTROLLER");

        return adminService.getDashboard();
    }

    @GetMapping("/test-email")
    public String testEmail() {

        emailService.sendEmail(
                "projectsiddhijava@gmail.com",
                "Smart Parking Test",
                "Congratulations! Your Spring Boot email is working."
        );

        return "Email Sent";
    }

    @GetMapping("/test-pdf")
    public String testPdf() throws Exception {

        pdfReceiptService.generateReceipt(
                "WB05AA1113",
                "Siddhi",
                "A1",
                20.0
        );

        return "PDF Generated";
    }
}