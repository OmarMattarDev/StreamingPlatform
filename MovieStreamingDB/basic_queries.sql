
-- 1. Top rated movies with review count
SELECT m.title, 
       COUNT(r.review_id) AS total_reviews, 
       ROUND(AVG(r.rating), 1) AS avg_rating
FROM movies m
JOIN reviews r ON m.movie_id = r.movie_id
GROUP BY m.movie_id, m.title
HAVING COUNT(r.review_id) >= 1
ORDER BY avg_rating DESC, total_reviews DESC;

-- 2. Movies with their combined genres
SELECT m.title, 
       m.release_year, 
       GROUP_CONCAT(g.genre_name SEPARATOR ', ') AS genres
FROM movies m
LEFT JOIN movie_genres mg ON m.movie_id = mg.movie_id
LEFT JOIN genres g ON mg.genre_id = g.genre_id
GROUP BY m.movie_id, m.title, m.release_year
ORDER BY m.title;

-- 3. Active subscriptions expiring within 7 days
SELECT u.username, 
       u.email, 
       sp.plan_name, 
       us.end_date,
       DATEDIFF(us.end_date, CURDATE()) AS days_left
FROM user_subscriptions us
JOIN users u ON us.user_id = u.user_id
JOIN subscription_plans sp ON us.plan_id = sp.plan_id
WHERE us.subscription_status = 'ACTIVE'
  AND us.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
ORDER BY us.end_date ASC;

-- 4. Continue watching progress percentage per user
SELECT u.username, 
       m.title, 
       ROUND((wh.watched_seconds / (m.duration_minutes * 60)) * 100, 1) AS progress_pct,
       wh.last_watched_at
FROM watch_history wh
JOIN users u ON wh.user_id = u.user_id
JOIN movies m ON wh.movie_id = m.movie_id
WHERE wh.completed = FALSE
ORDER BY wh.last_watched_at DESC;

-- 5. Total revenue and active subscribers per plan
SELECT sp.plan_name, 
       COUNT(us.subscription_id) AS active_users, 
       SUM(sp.price) AS total_revenue
FROM subscription_plans sp
LEFT JOIN user_subscriptions us 
       ON sp.plan_id = us.plan_id AND us.subscription_status = 'ACTIVE'
GROUP BY sp.plan_id, sp.plan_name
ORDER BY total_revenue DESC;