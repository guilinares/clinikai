package com.guilinares.clinikai.presentation.controllers;

import com.guilinares.clinikai.application.google.usecases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CreateGoogleCalendarEventUseCase createEvent;
    private final CheckAvailabilityUseCase checkAvailability;
    private final RescheduleGoogleCalendarEventUseCase rescheduleEvent;
    private final CancelGoogleCalendarEventUseCase cancelEvent;
    private final ListCalendarEventsUseCase listEvents;
    private final UpdateGoogleCalendarEventUseCase updateEvent;

    @PostMapping("/events")
    public ResponseEntity<Void> create(@RequestBody CalendarEventRequest req) {
        createEvent.execute(req.clinicId(), req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/events")
    public ResponseEntity<List<ListCalendarEventsUseCase.CalendarEventDto>> listEvents(
            @RequestParam UUID clinicId,
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(listEvents.execute(clinicId, start, end));
    }

    @GetMapping("/availability")
    public ResponseEntity<CheckAvailabilityUseCase.AvailabilityResponse> checkAvailability(
            @RequestParam UUID clinicId,
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(checkAvailability.execute(clinicId, start, end));
    }

    @PatchMapping("/events/{eventId}/reschedule")
    public ResponseEntity<Void> reschedule(@PathVariable String eventId,
                                           @RequestBody RescheduleRequest req) {
        rescheduleEvent.execute(req.clinicId(), eventId, req.start(), req.end());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<Void> update(@PathVariable String eventId,
                                       @RequestBody UpdateEventRequest req) {
        updateEvent.execute(req.clinicId(), eventId,
                new UpdateGoogleCalendarEventUseCase.UpdateEventRequest(req.title(), req.description(), req.start(), req.end()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> cancel(@PathVariable String eventId,
                                       @RequestParam UUID clinicId) {
        cancelEvent.execute(clinicId, eventId);
        return ResponseEntity.noContent().build();
    }

    public record CalendarEventRequest(UUID clinicId, String title, String description,
                                       String start, String end) {}

    public record RescheduleRequest(UUID clinicId, String start, String end) {}

    public record UpdateEventRequest(UUID clinicId, String title, String description, String start, String end) {}
}
