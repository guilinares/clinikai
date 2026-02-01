package com.guilinares.clinikai.infrastructure.data.repositories;

import com.guilinares.clinikai.domain.enums.MessageDirection;
import com.guilinares.clinikai.infrastructure.data.entities.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findTop20ByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    Page<MessageEntity> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    @Query("""
        select m from MessageEntity m
        where m.conversation.id = :conversationId
          and m.direction = :direction
          and m.processedAt is null
          and m.claimedAt is null
        order by m.createdAt asc, m.id asc
    """)
    List<MessageEntity> findPendingForClaim(
            @Param("conversationId") UUID conversationId,
            @Param("direction") MessageDirection direction,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update MessageEntity m
        set m.claimId = :claimId,
            m.claimedAt = :now
        where m.id in :ids
          and m.claimedAt is null
          and m.processedAt is null
    """)
    int claimByIds(
            @Param("ids") List<UUID> ids,
            @Param("claimId") UUID claimId,
            @Param("now") OffsetDateTime now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update MessageEntity m
        set m.processedAt = :now
        where m.claimId = :claimId
          and m.processedAt is null
    """)
    int markProcessedByClaimId(@Param("claimId") UUID claimId, @Param("now") OffsetDateTime now);
}
