ALTER TABLE messages ADD COLUMN processed_at timestamptz;
CREATE INDEX ix_messages_conv_processed ON messages (conversation_id, processed_at, created_at);
CREATE INDEX ix_messages_conv_created ON messages (conversation_id, created_at DESC);