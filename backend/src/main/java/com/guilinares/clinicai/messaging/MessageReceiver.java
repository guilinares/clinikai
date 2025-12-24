package com.guilinares.clinicai.messaging;

public interface MessageReceiver {

    void receiveMessage(String phone, String connectedPhone, String message);
}
