package com.guilinares.clinikai.application.clinic.ports;

import java.util.UUID;

public interface FlowRefinementPort {
    void requestRefinement(UUID clinicId, String flowJson);
}
