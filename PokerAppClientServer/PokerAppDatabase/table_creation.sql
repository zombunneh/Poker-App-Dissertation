CREATE TABLE users (
user_id INT UNIQUE AUTO_INCREMENT,
google_user_id VARCHAR(36) UNIQUE,
guest_user_id VARCHAR(36) UNIQUE,
user_name VARCHAR(30) NOT NULL,
last_login TIMESTAMP NOT NULL,
currency INT NOT NULL,
PRIMARY KEY (user_id)
);

CREATE TABLE details (
detail_id INT NOT NULL AUTO_INCREMENT,
user_id INT UNIQUE,
hands_played INT,
hands_won INT,
win_rate INT,
max_winnings INT,
max_chips INT,
PRIMARY KEY (detail_id),
FOREIGN KEY (user_id) REFERENCES users(user_id)
);
