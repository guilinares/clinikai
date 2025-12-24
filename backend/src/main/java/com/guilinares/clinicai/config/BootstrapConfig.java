package com.guilinares.clinicai.config;

import com.guilinares.clinicai.clinic.ClinicEntity;
import com.guilinares.clinicai.clinic.ClinicRepository;
import com.guilinares.clinicai.user.UserEntity;
import com.guilinares.clinicai.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Configuration
public class BootstrapConfig {

    @Bean
    public CommandLineRunner initData(
            ClinicRepository clinicRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return; // já tem dados, não faz nada
            }

            // Cria clínica demo
            ClinicEntity clinic = new ClinicEntity();
            clinic.setName("Clínica Demo");
            clinic.setSpecialty("Dermatologia");
            clinic.setWhatsappNumber("+5500000000000");
            clinic.setTimezone("America/Sao_Paulo");
            clinic.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            clinic.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

            clinic = clinicRepository.save(clinic);

            // Cria admin demo
            UserEntity admin = new UserEntity();
            admin.setClinic(clinic);
            admin.setName("Admin Demo");
            admin.setEmail("admin@clinicai.com");
            admin.setRole("ADMIN");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            admin.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

            userRepository.save(admin);

            System.out.println(">>> Usuário admin criado: admin@clinicai.com / Admin@123");
        };
    }
}
