package com.siddhi.smartparking.controller;

import com.siddhi.smartparking.dto.DashboardResponse;
import com.siddhi.smartparking.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(
            AdminService adminService
    ) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {

        return adminService.getDashboard();
    }
}