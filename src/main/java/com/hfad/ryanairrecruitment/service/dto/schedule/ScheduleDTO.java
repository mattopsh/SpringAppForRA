package com.hfad.ryanairrecruitment.service.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScheduleDTO {
    private int month;
    private List<DeparturesOnDTO> days;
}
