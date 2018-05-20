package com.hfad.ryanairrecruitment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class LegDTO {
    private String departureAirport;
    private String arrivalAirport;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date departureDateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private Date arrivalDateTime;
}
