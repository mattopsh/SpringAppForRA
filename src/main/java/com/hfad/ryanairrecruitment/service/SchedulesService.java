package com.hfad.ryanairrecruitment.service;

import com.hfad.ryanairrecruitment.service.dto.schedule.ScheduleDTO;
import com.hfad.ryanairrecruitment.tech.exceptions.ServiceUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;

@Service
public class SchedulesService {
    ScheduleDTO getSchedule(String departure, String arrival, Date departureDateTime) {
        RestTemplate restTemplate = new RestTemplate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(departureDateTime);
        int departureYear = calendar.get(Calendar.YEAR);
        int departureMonth = calendar.get(Calendar.MONTH);
        ScheduleDTO schedule;
        try {
            schedule = restTemplate.getForObject("https://api.ryanair.com/timetable/3/schedules/" + departure
                    + "/" + arrival + "/years/" + departureYear + "/months/" + departureMonth, ScheduleDTO.class);
        } catch (HttpClientErrorException httpClientErrorException) {
            throw new ServiceUnavailableException("Schedules microservice is not available");
        }
        return schedule;
    }
}
