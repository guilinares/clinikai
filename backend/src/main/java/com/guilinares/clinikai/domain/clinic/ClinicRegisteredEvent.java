package com.guilinares.clinikai.domain.clinic;

import java.util.UUID;

public record ClinicRegisteredEvent(
        UUID clinicId,
        String clinicName
) {}