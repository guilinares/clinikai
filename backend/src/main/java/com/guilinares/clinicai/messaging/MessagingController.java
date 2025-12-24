package com.guilinares.clinicai.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MessagingController {

    private final MessageSender messageSender;


}
