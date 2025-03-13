CREATE TABLE IF NOT EXISTS user_actions (
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    weight DECIMAL(2,1) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    CONSTRAINT pk_user_actions PRIMARY KEY (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS event_similarities (
    event_a BIGINT NOT NULL,
    event_b BIGINT NOT NULL,
    score REAL NOT NULL,
    CONSTRAINT pk_event_similarities PRIMARY KEY (event_a, event_b)
);