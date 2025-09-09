package com.tfm.bandas.eventos.model.entity;

import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "event", indexes = {
  @Index(name = "idx_events_start_at", columnList = "start_at"),
  @Index(name = "idx_events_visibility_start", columnList = "visibility,start_at")
})
public class Event {

  @Id
  @Column(length = 36, nullable = false)
  private String id; // UUID as String

  @Version
  private int version;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "start_at", nullable = false)
  private Instant startAt;

  @Column(name = "end_at", nullable = false)
  private Instant endAt;

  @Column(name = "time_zone", nullable = false, length = 50)
  private String timeZone; // e.g. "Europe/Madrid"

  @Column(length = 255)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private EventType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private EventStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private EventVisibility visibility;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;
}
