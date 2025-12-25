package com.tfm.bandas.events.model.entity;

import com.tfm.bandas.events.utils.EventStatus;
import com.tfm.bandas.events.utils.EventType;
import com.tfm.bandas.events.utils.EventVisibility;
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
public class EventEntity {

  @Id
  @Column(length = 36, nullable = false)
  private String id; // UUID as String

  @Version
  @Column(name = "version")
  private Integer version;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(name = "start_at", nullable = false)
  private Instant startAt;

  @Column(name = "end_at", nullable = false)
  private Instant endAt;

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
