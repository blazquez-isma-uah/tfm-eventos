-- Optativo, solo para probar la aplicación con datos de ejemplo
INSERT INTO event(id, title, description, start_at, end_at, time_zone, location, type, status, visibility)
VALUES (UUID(), 'Ensayo semanal', 'Ensayo semanal de la banda',
        '2025-09-20 20:00:00', '2025-09-20 22:00:00',
        'Europe/Madrid', 'Local hall', 'REHEARSAL', 'SCHEDULED', 'BAND_ONLY');

INSERT INTO event(id, title, description, start_at, end_at, time_zone, location, type, status, visibility)
VALUES (UUID(), 'Ensayo general con público', 'Ensayo general abierto al público',
        '2025-09-25 18:00:00', '2025-09-25 20:00:00',
        'Europe/Madrid', 'Local hall', 'REHEARSAL', 'SCHEDULED', 'PUBLIC');
