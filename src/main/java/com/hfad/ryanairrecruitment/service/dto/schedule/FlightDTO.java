package com.hfad.ryanairrecruitment.service.dto.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightDTO {
    @DateTimeFormat(pattern = "HH:mm")
    private Date departureTime;
    @DateTimeFormat(pattern = "HH:mm")
    private Date arrivalTime;
}
