package com.hfad.ryanairrecruitment.service.dto.route;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteDTO {
    private String airportFrom;
    private String airportTo;
}
