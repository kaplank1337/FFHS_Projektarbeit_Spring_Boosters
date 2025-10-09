-- V2__create_email_log_table.sql
-- Erstelle die email_log Tabelle im notification_service Schema

CREATE TABLE notification_service.email_log (
    id BIGSERIAL PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT,
    sent_at TIMESTAMP NOT NULL,
    success BOOLEAN NOT NULL,
    error_message TEXT
);

-- Erstelle Indexe für bessere Performance
CREATE INDEX idx_email_log_recipient ON notification_service.email_log(recipient_email);
CREATE INDEX idx_email_log_sent_at ON notification_service.email_log(sent_at);
CREATE INDEX idx_email_log_success ON notification_service.email_log(success);

-- Kommentar für die Tabelle
COMMENT ON TABLE notification_service.email_log IS 'Log-Tabelle für alle versendeten E-Mails des Notification Service';
COMMENT ON COLUMN notification_service.email_log.recipient_email IS 'E-Mail-Adresse des Empfängers';
COMMENT ON COLUMN notification_service.email_log.recipient_name IS 'Name des Empfängers';
COMMENT ON COLUMN notification_service.email_log.subject IS 'Betreff der E-Mail';
COMMENT ON COLUMN notification_service.email_log.content IS 'HTML-Inhalt der E-Mail';
COMMENT ON COLUMN notification_service.email_log.sent_at IS 'Zeitstempel wann die E-Mail versendet wurde';
COMMENT ON COLUMN notification_service.email_log.success IS 'Gibt an ob die E-Mail erfolgreich versendet wurde';
COMMENT ON COLUMN notification_service.email_log.error_message IS 'Fehlermeldung falls der Versand fehlgeschlagen ist';
-- V1__create_schema.sql
-- Erstelle das notification_service Schema

CREATE SCHEMA IF NOT EXISTS notification_service;

