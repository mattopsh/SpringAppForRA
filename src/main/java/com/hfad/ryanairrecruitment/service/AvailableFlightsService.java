package com.hfad.ryanairrecruitment.service;

import com.hfad.ryanairrecruitment.service.dto.*;
import com.hfad.ryanairrecruitment.service.dto.route.InterconnectedRouteDTO;
import com.hfad.ryanairrecruitment.service.dto.route.RouteDTO;
import com.hfad.ryanairrecruitment.service.dto.schedule.DeparturesOnDTO;
import com.hfad.ryanairrecruitment.service.dto.schedule.FlightDTO;
import com.hfad.ryanairrecruitment.service.dto.LegDTO;
import com.hfad.ryanairrecruitment.service.dto.schedule.ScheduleDTO;
import com.hfad.ryanairrecruitment.utils.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AvailableFlightsService {

    @Autowired
    private RoutesService routesService;

    @Autowired
    private SchedulesService schedulesService;

    public List<AvailableFlightDTO> findConnections(String departure, String arrival, Date departureDateTime, Date arrivalDateTime) {
        Calendar departureDateTimeCalendar = DateConverter.convertDateToCalendar(departureDateTime);
        Calendar arrivalDateTimeCalendar = DateConverter.convertDateToCalendar(arrivalDateTime);
        DateConverter.convertDateToCalendar(arrivalDateTime);
        List<RouteDTO> routes = routesService.findRoutes();
        List<AvailableFlightDTO> interconnections = new ArrayList<>();
        if (isDirectRouteAvailable(routes, departure, arrival)) {
            interconnections.addAll(findDirectFlightsInPeriod(departure, arrival, departureDateTimeCalendar, arrivalDateTimeCalendar));
        }
        interconnections.addAll(findInterconnectedFlightsInPeriod(findInterconnectedRoutes(routes, departure, arrival), departureDateTimeCalendar, arrivalDateTimeCalendar));
        return interconnections;
    }

    private boolean isDirectRouteAvailable(List<RouteDTO> routes, String departure, String arrival) {
        return routes.stream().anyMatch(routeDTO -> routeDTO.getAirportFrom().equals(departure) && routeDTO.getAirportTo().equals(arrival));
    }

    private List<InterconnectedRouteDTO> findInterconnectedRoutes(List<RouteDTO> routes, String departure, String arrival) {
        List<RouteDTO> indirectRoutes = routes.stream().filter(route -> !(route.getAirportFrom().equals(departure) && route.getAirportTo().equals(arrival))).collect(Collectors.toList());
        List<InterconnectedRouteDTO> interconnectedRoutes = new ArrayList<>();
        List<RouteDTO> firstRoutes = indirectRoutes.stream()
                .filter(route -> route.getAirportFrom().equals(departure))
                .collect(Collectors.toList());
        List<RouteDTO> secondRoutes = indirectRoutes.stream()
                .filter(route -> route.getAirportTo().equals(arrival))
                .collect(Collectors.toList());
        while (!firstRoutes.isEmpty()) {
            RouteDTO routeDTO = firstRoutes.remove(0);
            interconnectedRoutes.addAll(secondRoutes.stream()
                    .filter(endRoute -> routeDTO.getAirportTo().equals(endRoute.getAirportFrom()))
                    .map(endRoute -> new InterconnectedRouteDTO(routeDTO.getAirportFrom(), routeDTO.getAirportTo(), endRoute.getAirportTo()))
                    .collect(Collectors.toList()));
        }
        return interconnectedRoutes;
    }

    private List<AvailableFlightDTO> findDirectFlightsInPeriod(final String departure, final String arrival, final Calendar selectedDepartureTimeLimit, final Calendar selectedArrivalTimeLimit) {
        ScheduleDTO schedule = schedulesService.getSchedule(departure, arrival, selectedDepartureTimeLimit.getTime());
        List<AvailableFlightDTO> availableFlights = new ArrayList<>();

        for (DeparturesOnDTO departuresOnDTO : schedule.getDays()) {
            for (FlightDTO flightDTO : departuresOnDTO.getFlights()) {
                Calendar departureTime = DateConverter.convertDateToCalendar(flightDTO.getDepartureTime());
                Calendar arrivalTime = DateConverter.convertDateToCalendar(flightDTO.getArrivalTime());
                Calendar departureDateTime = Calendar.getInstance();
                Calendar arrivalDateTime = Calendar.getInstance();
                departureDateTime.set(Calendar.YEAR, selectedDepartureTimeLimit.get(Calendar.YEAR));
                departureDateTime.set(Calendar.MONTH, schedule.getMonth() - 1);
                departureDateTime.set(Calendar.DAY_OF_MONTH, departuresOnDTO.getDay());
                departureDateTime.set(Calendar.HOUR, departureTime.get(Calendar.HOUR));
                departureDateTime.set(Calendar.MINUTE, departureTime.get(Calendar.MINUTE));
                departureDateTime.set(Calendar.SECOND, 0);
                arrivalDateTime.set(Calendar.YEAR, selectedArrivalTimeLimit.get(Calendar.YEAR));
                arrivalDateTime.set(Calendar.MONTH, schedule.getMonth() - 1);
                arrivalDateTime.set(Calendar.DAY_OF_MONTH, departuresOnDTO.getDay());
                arrivalDateTime.set(Calendar.HOUR, arrivalTime.get(Calendar.HOUR));
                arrivalDateTime.set(Calendar.MINUTE, arrivalTime.get(Calendar.MINUTE));
                arrivalDateTime.set(Calendar.SECOND, 0);

                if (isFlightInTimeBounds(selectedDepartureTimeLimit, selectedArrivalTimeLimit, departureDateTime.getTime(), arrivalDateTime.getTime())) {
                    availableFlights.add(new AvailableFlightDTO(0, Collections.singletonList(
                            new LegDTO(departure, arrival, departureTime.getTime(), arrivalTime.getTime()))));
                }
            }
        }
        return availableFlights;
    }

    private List<AvailableFlightDTO> findInterconnectedFlightsInPeriod(List<InterconnectedRouteDTO> interconnectedRoutes, Calendar selectedDepartureTimeLimit, Calendar selectedArrivalTimeLimit) {
        List<AvailableFlightDTO> availableFlights = new ArrayList<>();
        for (InterconnectedRouteDTO interconnectedRoute : interconnectedRoutes) {
            List<AvailableFlightDTO> flightsBetweenStartAndMiddleLocation = findDirectFlightsInPeriod(interconnectedRoute.getAirportFrom(), interconnectedRoute.getAirportInMiddle(), selectedDepartureTimeLimit, selectedArrivalTimeLimit);
            List<AvailableFlightDTO> flightsBetweenMiddleAndEndLocation = findDirectFlightsInPeriod(interconnectedRoute.getAirportInMiddle(), interconnectedRoute.getAirportTo(), selectedDepartureTimeLimit, selectedArrivalTimeLimit);
            for (LegDTO flightBetweenStartAndMiddle : flightsBetweenStartAndMiddleLocation.stream().map(AvailableFlightDTO::getLegs).flatMap(Collection::stream).collect(Collectors.toList())) {
                for (LegDTO flightBetweenMiddleAndEnd : flightsBetweenMiddleAndEndLocation.stream().map(AvailableFlightDTO::getLegs).flatMap(Collection::stream).collect(Collectors.toList())) {
                    if (flightBetweenStartAndMiddle.getArrivalAirport().equals(flightBetweenMiddleAndEnd.getDepartureAirport()) &&
                            TimeUnit.MILLISECONDS.toHours(flightBetweenMiddleAndEnd.getDepartureDateTime().getTime() - flightBetweenStartAndMiddle.getArrivalDateTime().getTime()) >= 2) {
                        availableFlights.add(new AvailableFlightDTO(1,
                                new ArrayList<>(
                                        Arrays.asList(
                                                new LegDTO(flightBetweenStartAndMiddle.getDepartureAirport(),
                                                        flightBetweenStartAndMiddle.getArrivalAirport(),
                                                        flightBetweenStartAndMiddle.getDepartureDateTime(),
                                                        flightBetweenStartAndMiddle.getArrivalDateTime()),
                                                new LegDTO(flightBetweenMiddleAndEnd.getDepartureAirport(),
                                                        flightBetweenMiddleAndEnd.getArrivalAirport(),
                                                        flightBetweenMiddleAndEnd.getDepartureDateTime(),
                                                        flightBetweenMiddleAndEnd.getArrivalDateTime())
                                        )

                                ))
                        );
                    }
                }
            }
        }
        return availableFlights;
    }

    private boolean isFlightInTimeBounds(Calendar selectedDepartureTimeLimit, Calendar selectedArrivalTimeLimit, Date departureTime, Date arrivalTime) {
        return selectedArrivalTimeLimit.compareTo(DateConverter.convertDateToCalendar(arrivalTime)) >= 0 && selectedDepartureTimeLimit.compareTo(DateConverter.convertDateToCalendar(departureTime)) <= 0;
    }
}
