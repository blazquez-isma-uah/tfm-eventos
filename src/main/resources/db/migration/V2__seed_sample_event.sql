-- Optativo, solo para probar la aplicación con datos de ejemplo
INSERT INTO event(id, title, description, start_at, end_at, time_zone, location, type, status, visibility)
VALUES (UUID(), 'Sample Rehearsal', 'Kickoff rehearsal',
        '2025-09-20 18:00:00', '2025-09-20 20:00:00',
        'Europe/Madrid', 'Local hall', 'REHEARSAL', 'SCHEDULED', 'BAND_ONLY');
