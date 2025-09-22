package com.tfm.bandas.eventos.model.repository;

import com.tfm.bandas.eventos.model.entity.EventEntity;
import com.tfm.bandas.eventos.utils.EventVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, String>,
        JpaSpecificationExecutor<EventEntity> {
  Page<EventEntity> findAllByStartAtBetweenOrderByStartAtAsc(Instant from, Instant to, Pageable pageable);
  Page<EventEntity> findAllByStartAtBeforeOrderByStartAtDesc(Instant before, Pageable pageable);
  Page<EventEntity> findAllByEndAtBeforeOrderByEndAtDesc(Instant before, Pageable pageable);
  Page<EventEntity> findAllByVisibilityAndStartAtBetweenOrderByStartAtAsc(EventVisibility visibility, Instant from, Instant to, Pageable pageable);

  @Query("""
  select count(e) from EventEntity e
  where (:location is not null and e.location = :location)
    and e.status <> com.tfm.bandas.eventos.utils.EventStatus.CANCELED
    and (:excludeId is null or e.id <> :excludeId)
    and e.startAt < :end
    and e.endAt   > :start
""")
  long countConflictsAtLocation(String location, Instant start, Instant end, String excludeId);

}
