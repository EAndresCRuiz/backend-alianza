package com.alianza.clients.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSearchCriteria {
    private String name;
    private String email;
    private String phone;
    private LocalDate startDate;
    private LocalDate endDate;
    private String exportFormat; // CSV or EXCEL
}