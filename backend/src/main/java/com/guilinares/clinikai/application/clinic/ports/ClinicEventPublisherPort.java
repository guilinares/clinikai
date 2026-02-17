package com.guilinares.clinikai.application.clinic.ports;

import com.guilinares.clinikai.domain.clinic.ClinicRegisteredEvent;

public interface ClinicEventPublisherPort {
    void publish(ClinicRegisteredEvent event);
}