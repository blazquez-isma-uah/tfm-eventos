package com.tfm.bandas.eventos.model.specification;

import com.tfm.bandas.eventos.model.entity.EventEntity;
import com.tfm.bandas.eventos.utils.EventStatus;
import com.tfm.bandas.eventos.utils.EventType;
import com.tfm.bandas.eventos.utils.EventVisibility;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class EventSpecifications {
    public static Specification<EventEntity> all() {
        return (root, q, cb) -> cb.conjunction();
    }

    public static Specification<EventEntity> text(String qText) {
        if (qText == null || qText.isBlank()) return null;
        String like = "%" + qText.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like),
                cb.like(cb.lower(root.get("location")), like)
        );
    }

    public static Specification<EventEntity> titleContains(String title) {
        if (title == null || title.isBlank()) return null;
        String like = "%" + title.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("title")), like);
    }

    public static Specification<EventEntity> descriptionContains(String description) {
        if (description == null || description.isBlank()) return null;
        String like = "%" + description.trim().toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("description")), like);
    }

    public static Specification<EventEntity> locationEquals(String location) {
        if (location == null || location.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("location"), location);
    }

    public static Specification<EventEntity> timeZoneEquals(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) return null;
        return (root, q, cb) -> cb.equal(root.get("timeZone"), timeZone);
    }

    public static Specification<EventEntity> typeEquals(EventType type) {
        if (type == null) return null;
        return (root, q, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<EventEntity> statusEquals(EventStatus status) {
        if (status == null) return null;
        return (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<EventEntity> visibilityEquals(EventVisibility visibility) {
        if (visibility == null) return null;
        return (root, q, cb) -> cb.equal(root.get("visibility"), visibility);
    }

    /** Eventos cuyo intervalo [startAt, endAt] interseca con [from, to] */
    public static Specification<EventEntity> overlaps(Instant from, Instant to) {
        if (from == null && to == null) return null;
        if (from != null && to != null) {
            return (root, q, cb) -> cb.and(
                    cb.lessThan(root.get("startAt"), to),
                    cb.greaterThan(root.get("endAt"), from)
            );
        }
        if (from != null) {
            return (root, q, cb) -> cb.greaterThan(root.get("endAt"), from);
        }
        // to != null
        return (root, q, cb) -> cb.lessThan(root.get("startAt"), to);
    }

    /** Eventos íntegramente dentro de [from, to] (opcional si prefieres contención estricta) */
    public static Specification<EventEntity> containedIn(Instant from, Instant to) {
        if (from == null || to == null) return null;
        return (root, q, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("startAt"), from),
                cb.lessThanOrEqualTo(root.get("endAt"), to)
        );
    }
}
