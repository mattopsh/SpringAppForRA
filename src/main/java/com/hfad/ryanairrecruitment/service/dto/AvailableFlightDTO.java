package com.hfad.ryanairrecruitment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AvailableFlightDTO {
    private int stops;
    private List<LegDTO> legs;
}
