package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.google.usecases.CreateGoogleCalendarEventUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CreateGoogleCalendarEventUseCase createEvent;

    @PostMapping("/events")
    public void create(@RequestBody CalendarEventRequest req) throws Exception {
        createEvent.execute(req.clinicId(), req);
    }

    public record CalendarEventRequest(UUID clinicId, String title, String description,
                                       String start, String end) {}
}
