
-- Movies
CREATE INDEX idx_movies_title
ON movies(title);

CREATE INDEX idx_movies_release_year
ON movies(release_year);

CREATE INDEX idx_movies_status
ON movies(movie_status);


-- Reviews
CREATE INDEX idx_reviews_movie_rating
ON reviews(movie_id, rating);


-- Watch History
CREATE INDEX idx_watch_history_user_date
ON watch_history(user_id, last_watched_at);


-- Watchlist
CREATE INDEX idx_watchlist_user
ON watchlist(user_id);


-- Subscriptions
CREATE INDEX idx_user_subscriptions_user_status
ON user_subscriptions(user_id, subscription_status);

CREATE INDEX idx_user_subscriptions_end_date
ON user_subscriptions(end_date);
