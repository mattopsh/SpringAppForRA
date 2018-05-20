package com.hfad.ryanairrecruitment.service;


import com.hfad.ryanairrecruitment.service.dto.route.RouteDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RoutesService {
    List<RouteDTO> findRoutes() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<RouteDTO>> routesResponse = restTemplate.exchange("https://api.ryanair.com/core/3/routes/",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<RouteDTO>>() {
                });
        return routesResponse.getBody();
    }
}
