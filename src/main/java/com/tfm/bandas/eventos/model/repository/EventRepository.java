package com.tfm.bandas.eventos.model.repository;

import com.tfm.bandas.eventos.model.entity.Event;
import com.tfm.bandas.eventos.utils.EventVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {
  List<Event> findAllByStartAtBetweenOrderByStartAtAsc(Instant from, Instant to);
  List<Event> findAllByStartAtBeforeOrderByStartAtDesc(Instant before);
  List<Event> findAllByEndAtBeforeOrderByEndAtDesc(Instant before);
  List<Event> findAllByVisibilityAndStartAtBetweenOrderByStartAtAsc(EventVisibility visibility, Instant from, Instant to);
}
