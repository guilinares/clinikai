package com.guilinares.clinicai.clinic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ClinicKbEntryRepository extends JpaRepository<ClinicKbEntryEntity, UUID> {

    @Query(value = """
        select *
        from clinic_kb_entries
        where clinic_id = :clinicId
          and active = true
          and to_tsvector('portuguese', title || ' ' || content) @@ plainto_tsquery('portuguese', :q)
        order by ts_rank(
          to_tsvector('portuguese', title || ' ' || content),
          plainto_tsquery('portuguese', :q)
        ) desc
        limit :limit
        """, nativeQuery = true)
    List<ClinicKbEntryEntity> search(UUID clinicId, String q, int limit);

    List<ClinicKbEntryEntity> findTop5ByClinicIdAndActiveTrueOrderByUpdatedAtDesc(UUID clinicId);
}