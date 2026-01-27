ALTER TABLE clinics
  ALTER COLUMN ai_config TYPE text
  USING ai_config::text;

ALTER TABLE conversation_state
ALTER COLUMN state TYPE text
USING state::text;