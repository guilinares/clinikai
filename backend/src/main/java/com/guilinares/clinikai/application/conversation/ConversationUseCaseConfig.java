package com.guilinares.clinikai.application.conversation;

import com.guilinares.clinikai.application.clinic.ports.ClinicRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.ConversationRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.ConversationStateRepositoryPort;
import com.guilinares.clinikai.application.conversation.ports.MessageRepositoryPort;
import com.guilinares.clinikai.application.conversation.usecases.MarkMessageProcessedUseCase;
import com.guilinares.clinikai.application.conversation.usecases.MessageClaimUseCase;
import com.guilinares.clinikai.application.conversation.usecases.RegisterConversationUseCase;
import com.guilinares.clinikai.application.patient.ports.PatientRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConversationUseCaseConfig {

    @Bean
    public RegisterConversationUseCase registerConversationUseCase(ConversationRepositoryPort conversationRepositoryPort,
                                                                   ConversationStateRepositoryPort conversationStateRepositoryPort,
                                                                   MessageRepositoryPort messageRepositoryPort,
                                                                   ClinicRepositoryPort clinicRepositoryPort,
                                                                   PatientRepositoryPort patientRepositoryPort) {
        return new RegisterConversationUseCase(
                conversationRepositoryPort,
                conversationStateRepositoryPort,
                messageRepositoryPort,
                clinicRepositoryPort,
                patientRepositoryPort
        );
    }

    @Bean
    public MessageClaimUseCase messageClaimUseCase(ConversationRepositoryPort conversationRepositoryPort,
                                                   MessageRepositoryPort messageRepositoryPort) {
        return new MessageClaimUseCase(
                conversationRepositoryPort, messageRepositoryPort
        );
    }

    @Bean
    public MarkMessageProcessedUseCase markMessageProcessedUseCase(MessageRepositoryPort messageRepositoryPort) {
        return new MarkMessageProcessedUseCase(messageRepositoryPort);
    }
}
