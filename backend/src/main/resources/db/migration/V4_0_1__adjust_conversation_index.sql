ALTER TABLE conversations
DROP CONSTRAINT uk_conversations_open;

CREATE UNIQUE INDEX uk_conversations_open
ON conversations (clinic_id, patient_id, channel)
WHERE status = 'OPEN';