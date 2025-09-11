package com.tfm.bandas.eventos.model.repository;

import com.tfm.bandas.eventos.model.entity.Event;
import com.tfm.bandas.eventos.utils.EventVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {
  List<Event> findAllByStartAtBetweenOrderByStartAtAsc(Instant from, Instant to);
  List<Event> findAllByStartAtBeforeOrderByStartAtDesc(Instant before);
  List<Event> findAllByEndAtBeforeOrderByEndAtDesc(Instant before);
  List<Event> findAllByVisibilityAndStartAtBetweenOrderByStartAtAsc(EventVisibility visibility, Instant from, Instant to);
  @Query("""
  select count(e) from Event e
  where (:location is not null and e.location = :location)
    and e.status <> com.tfm.bandas.eventos.utils.EventStatus.CANCELED
    and (:excludeId is null or e.id <> :excludeId)
    and e.startAt < :end
    and e.endAt   > :start
""")
  long countConflictsAtLocation(String location, Instant start, Instant end, String excludeId);

}
