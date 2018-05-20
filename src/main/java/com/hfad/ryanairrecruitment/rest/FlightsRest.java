package com.hfad.ryanairrecruitment.rest;

import com.hfad.ryanairrecruitment.service.AvailableFlightsService;
import com.hfad.ryanairrecruitment.service.dto.AvailableFlightDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/somevalidcontext")
public class FlightsRest {

    @Autowired
    private AvailableFlightsService availableFlightsService;

    @RequestMapping(path = "interconnections", method = RequestMethod.GET)
    public List<AvailableFlightDTO> findAllFlights(@RequestParam("departure") String departure,
                                                   @RequestParam("arrival") String arrival,
                                                   @RequestParam("departureDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date departureDateTime,
                                                   @RequestParam("arrivalDateTime") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date arrivalDateTime) {

        return availableFlightsService.findConnections(departure, arrival, departureDateTime, arrivalDateTime);
    }
}
