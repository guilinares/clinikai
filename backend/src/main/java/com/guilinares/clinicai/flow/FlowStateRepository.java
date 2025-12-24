package com.guilinares.clinicai.flow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FlowStateRepository extends JpaRepository<FlowStateEntity, UUID> {

    Optional<FlowStateEntity> findByClinicIdAndPhone(UUID clinicId, String phone);
}
