package com.hfad.ryanairrecruitment.service;

import com.hfad.ryanairrecruitment.service.dto.AvailableFlightDTO;
import com.hfad.ryanairrecruitment.service.dto.route.RouteDTO;
import com.hfad.ryanairrecruitment.service.dto.schedule.DeparturesOnDTO;
import com.hfad.ryanairrecruitment.service.dto.schedule.FlightDTO;
import com.hfad.ryanairrecruitment.service.dto.schedule.ScheduleDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AvailableFlightsServiceTest {

    @InjectMocks
    private AvailableFlightsService availableFlightsService;

    @Mock
    private SchedulesService schedulesService;

    @Mock
    private RoutesService routesService;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private static List<RouteDTO> routesMock = Arrays.asList(
            new RouteDTO("LUZ", "WRO"),
            new RouteDTO("CHQ", "WRO"),
            new RouteDTO("POZ", "WRO"),
            new RouteDTO("LUZ", "CHQ"),
            new RouteDTO("LUZ", "POZ"),
            new RouteDTO("CHQ", "SKG")
    );


    @Test
    public void findAllInterconnectedFlights() throws ParseException {
        DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        String departure = "LUZ";
        String arrival = "WRO";

        Date departureDateTime = dateTimeFormatter.parse("2016-03-01T12:40");
        Date arrivalDateTime = dateTimeFormatter.parse("2016-03-03T16:40");
        ScheduleDTO scheduleFromLUZToCHQMock = new ScheduleDTO(
                3, Collections.singletonList(
                new DeparturesOnDTO(1, Arrays.asList(
                        new FlightDTO(timeFormatter.parse("13:40"), timeFormatter.parse("16:40")),
                        new FlightDTO(timeFormatter.parse("18:45"), timeFormatter.parse("19:50"))
                )
                )
        ));
        ScheduleDTO scheduleFromCHQToWROMock = new ScheduleDTO(
                3, Collections.singletonList(
                new DeparturesOnDTO(1, Arrays.asList(
                        new FlightDTO(timeFormatter.parse("18:45"), timeFormatter.parse("19:50")),
                        new FlightDTO(timeFormatter.parse("23:00"), timeFormatter.parse("23:55"))
                )
                )
        ));

        List<RouteDTO> routesMock = Arrays.asList(
                new RouteDTO("CHQ", "WRO"),
                new RouteDTO("LUZ", "CHQ"),
                new RouteDTO("POZ", "WRO")
        );


        Mockito.when(schedulesService.getSchedule("LUZ", "CHQ", departureDateTime)).thenReturn(scheduleFromLUZToCHQMock);
        Mockito.when(schedulesService.getSchedule("CHQ", "WRO", departureDateTime)).thenReturn(scheduleFromCHQToWROMock);
        Mockito.when(routesService.findRoutes()).thenReturn(routesMock);

        List<AvailableFlightDTO> availableFlights = availableFlightsService.findConnections(departure, arrival, departureDateTime, arrivalDateTime);

        Assert.assertEquals(3, availableFlights.size());
    }

    @Test
    public void findAllFlights() throws ParseException {
        DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        String departure = "LUZ";
        String arrival = "WRO";

        Date departureDateTime = dateTimeFormatter.parse("2016-03-01T12:40");
        Date arrivalDateTime = dateTimeFormatter.parse("2016-03-03T16:40");
        ScheduleDTO scheduleMock = new ScheduleDTO(
                3, Arrays.asList(
                new DeparturesOnDTO(1, Arrays.asList(
                        new FlightDTO(timeFormatter.parse("13:40"), timeFormatter.parse("16:40")),
                        new FlightDTO(timeFormatter.parse("13:45"), timeFormatter.parse("15:40"))
                )
                ),
                new DeparturesOnDTO(2, Arrays.asList(
                        new FlightDTO(timeFormatter.parse("14:40"), timeFormatter.parse("17:40")),
                        new FlightDTO(timeFormatter.parse("19:40"), timeFormatter.parse("20:40"))
                )
                ),
                new DeparturesOnDTO(3, Arrays.asList(
                        new FlightDTO(timeFormatter.parse("17:40"), timeFormatter.parse("19:40")),
                        new FlightDTO(timeFormatter.parse("11:40"), timeFormatter.parse("18:40"))
                )
                )
        ));


        Mockito.when(schedulesService.getSchedule(departure, arrival, departureDateTime)).thenReturn(scheduleMock);
        Mockito.when(schedulesService.getSchedule("LUZ", "CHQ", departureDateTime)).thenReturn(scheduleMock);
        Mockito.when(schedulesService.getSchedule("LUZ", "POZ", departureDateTime)).thenReturn(scheduleMock);
        Mockito.when(schedulesService.getSchedule("CHQ", "WRO", departureDateTime)).thenReturn(scheduleMock);
        Mockito.when(schedulesService.getSchedule("POZ", "WRO", departureDateTime)).thenReturn(scheduleMock);
        Mockito.when(routesService.findRoutes()).thenReturn(routesMock);

        List<AvailableFlightDTO> availableFlights = availableFlightsService.findConnections(departure, arrival, departureDateTime, arrivalDateTime);

        Assert.assertEquals(10, availableFlights.size());

    }
}