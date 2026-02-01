ALTER TABLE messages ADD COLUMN claim_id uuid;
ALTER TABLE messages ADD COLUMN claimed_at timestamptz;

CREATE INDEX ix_messages_claim
ON messages (conversation_id, claimed_at, processed_at, created_at);