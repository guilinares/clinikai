package com.guilinares.clinikai.application.google.ports;

import java.util.UUID;

public interface GoogleUtils {
    String buildAuthorizationUrl(UUID clinicId);
}
