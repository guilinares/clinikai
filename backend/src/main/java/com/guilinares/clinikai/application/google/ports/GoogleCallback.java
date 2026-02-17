package com.guilinares.clinikai.application.google.ports;

import java.io.IOException;
import java.util.UUID;

public interface GoogleCallback {
    String handle(String code, UUID clinicId);
}
