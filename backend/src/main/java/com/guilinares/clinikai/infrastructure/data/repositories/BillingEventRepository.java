package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.BillingEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BillingEventRepository extends JpaRepository<BillingEventEntity, UUID> {
}
