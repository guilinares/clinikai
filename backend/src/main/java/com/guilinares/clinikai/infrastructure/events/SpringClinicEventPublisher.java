package com.guilinares.clinikai.infrastructure.events;

import com.guilinares.clinikai.application.clinic.ports.ClinicEventPublisherPort;
import com.guilinares.clinikai.domain.clinic.ClinicRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringClinicEventPublisher implements ClinicEventPublisherPort {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(ClinicRegisteredEvent event) {
        publisher.publishEvent(event);
    }
}
