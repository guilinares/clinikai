package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.infrastructure.data.entities.ClinicKbEntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

public interface ClinicKbEntryRepository extends JpaRepository<ClinicKbEntryEntity, UUID> {
    List<ClinicKbEntryEntity> findByClinicIdAndEnabledTrue(UUID clinicId);
    List<ClinicKbEntryEntity> findAllByClinicId(UUID clinicId);

    // =========================================================
    // 1) LISTAR por clínica (admin) com filtros opcionais
    // =========================================================
    @Query(value = """
        SELECT *
        FROM clinic_kb_entries
        WHERE clinic_id = :clinicId
          AND (:enabled IS NULL OR enabled = :enabled)
          AND (:category IS NULL OR category = :category)
          AND (:tag IS NULL OR :tag = ANY(tags))
        ORDER BY updated_at DESC, created_at DESC
        """,
            countQuery = """
        SELECT count(*)
        FROM clinic_kb_entries
        WHERE clinic_id = :clinicId
          AND (:enabled IS NULL OR enabled = :enabled)
          AND (:category IS NULL OR category = :category)
          AND (:tag IS NULL OR :tag = ANY(tags))
        """,
            nativeQuery = true)
    Page<ClinicKbEntryEntity> listByClinic(
            @Param("clinicId") UUID clinicId,
            @Param("enabled") Boolean enabled,
            @Param("category") String category,
            @Param("tag") String tag,
            Pageable pageable
    );

    // =========================================================
    // 2) SEARCH simples (ILIKE) para RAG
    // =========================================================
    @Query(value = """
        SELECT *
        FROM clinic_kb_entries
        WHERE clinic_id = :clinicId
          AND enabled = true
          AND (
            title ILIKE ('%' || :q || '%')
            OR content ILIKE ('%' || :q || '%')
            OR :q = ANY(tags)
          )
        ORDER BY updated_at DESC
        """,
            nativeQuery = true)
    Page<ClinicKbEntryEntity> searchSimple(
            @Param("clinicId") UUID clinicId,
            @Param("q") String q,
            Pageable pageable
    );

    // =========================================================
    // 3) ENABLE/DISABLE
    // =========================================================
    @Modifying
    @Query(value = """
        UPDATE clinic_kb_entries
        SET enabled = :enabled,
            updated_at = :now
        WHERE id = :id
        """, nativeQuery = true)
    int setEnabled(
            @Param("id") UUID id,
            @Param("enabled") boolean enabled,
            @Param("now") OffsetDateTime now
    );
}
