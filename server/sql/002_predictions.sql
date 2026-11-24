


CREATE TABLE IF NOT EXISTS predictions (

    id BIGSERIAL PRIMARY KEY, --主キー

    user_id BIGINT NOT NULL
        REFERENCES users(id) ON DELETE CASCADE,

        --参照先のユーザーが削除されたら関連する予想も削除

    match_id INTEGER NOT NULL,

    predicted_home_score INTEGER NOT NULL
        CHECK (predicted_home_score >= 0),

    predicted_away_score INTEGER NOT NULL
        CHECK (predicted_away_score >= 0),

    earned_points INTEGER
        CHECK (earned_points IN (0, 1, 3)),

    created_at TIMESTAMPTZ NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    graded_at TIMESTAMPTZ,

    --採点日時のカラム

    CONSTRAINT uq_predictions_user_match
        UNIQUE (user_id, match_id)

        --UNIQUEは、（）内の組み合わせが重複しないことを制約。
);



CREATE INDEX IF NOT EXISTS idx_predictions_ungraded_match
    ON predictions (match_id)
    WHERE earned_points IS NULL;

--ポイントが計算されていない予想
--既存のテーブルにあるデータを対象に、インデックスにより木構造で検索できる→計算量がO(log N)になる！。


--predictionsテーブル（実際のデータ）
--         │
--         │ 未採点の行だけを対象
--         ↓
--  部分インデックス（検索用の索引）
--         │
--         │ match_id = 500 を探す
--         ↓
--  該当する行の位置を特定
--         ↓
--  元のpredictionsテーブルからデータを取得