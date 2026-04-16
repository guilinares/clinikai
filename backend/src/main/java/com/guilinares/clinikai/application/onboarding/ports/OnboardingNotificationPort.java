package com.guilinares.clinikai.application.onboarding.ports;

import java.util.UUID;

public interface OnboardingNotificationPort {
    void notifyNewClinicRegistration(UUID clinicId, String clinicName, String userName, String email, String whatsapp);
}
