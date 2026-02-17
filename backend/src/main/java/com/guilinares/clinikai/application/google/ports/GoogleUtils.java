package com.guilinares.clinikai.application.google.ports;

import com.guilinares.clinikai.infrastructure.google.adapters.GoogleUtilsAdapter;

import java.util.UUID;

public interface GoogleUtils {
    GoogleUtilsAdapter.GoogleUrlResponse buildAuthorizationUrl(UUID clinicId);
}
