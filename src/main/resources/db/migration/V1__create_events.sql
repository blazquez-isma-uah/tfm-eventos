CREATE TABLE event (
        id VARCHAR(36) NOT NULL,
        version INT NOT NULL DEFAULT 0,
        title VARCHAR(200) NOT NULL,
        description TEXT NULL,
        start_at DATETIME(6) NOT NULL,
        end_at DATETIME(6) NOT NULL,
        time_zone VARCHAR(50) NOT NULL,
        location VARCHAR(255) NULL,
        type VARCHAR(32) NOT NULL,
        status VARCHAR(32) NOT NULL,
        visibility VARCHAR(32) NOT NULL,
        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
        updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
        PRIMARY KEY (id)
);

CREATE INDEX idx_event_start_at ON event (start_at);
CREATE INDEX idx_event_visibility_start ON event (visibility, start_at);
