CREATE TABLE IF NOT EXISTS predictions (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    match_id INTEGER NOT NULL CHECK (match_id > 0),
    league_code VARCHAR(20) NOT NULL,
    season_year INTEGER NOT NULL CHECK (season_year BETWEEN 2000 AND 2100),
    home_score SMALLINT NOT NULL CHECK (home_score BETWEEN 0 AND 20),
    away_score SMALLINT NOT NULL CHECK (away_score BETWEEN 0 AND 20),
    kickoff_at TIMESTAMPTZ NOT NULL,
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    points SMALLINT CHECK (points IN (0, 1, 3)),
    scored_at TIMESTAMPTZ,
    PRIMARY KEY (user_id, match_id)
);
CREATE INDEX IF NOT EXISTS idx_predictions_ranking
    ON predictions(league_code, season_year, user_id);
CREATE INDEX IF NOT EXISTS idx_predictions_kickoff
    ON predictions(kickoff_at);
