package com.hfad.ryanairrecruitment.service.dto.route;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterconnectedRouteDTO extends RouteDTO {

    private String airportInMiddle;

    public InterconnectedRouteDTO(String airportFrom, String airportInMiddle, String airportTo) {
        super(airportFrom, airportTo);
        this.airportInMiddle = airportInMiddle;
    }
}
