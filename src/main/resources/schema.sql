create DATABASE IF NOT EXISTS mock_db;

use mock_db;

CREATE TABLE IF NOT EXISTS endpoints (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    path VARCHAR(512) UNIQUE NOT NULL,
    status_code INT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM'
);


CREATE TABLE IF NOT EXISTS headers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    endpoints_id BIGINT NOT NULL,
    name VARCHAR(255),
    value VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    FOREIGN KEY (endpoints_id) REFERENCES endpoints(id)
);


CREATE TABLE IF NOT EXISTS responses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    endpoints_id BIGINT NOT NULL,
    method VARCHAR(10),
    content_type VARCHAR(255),
    body TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    FOREIGN KEY (endpoints_id) REFERENCES endpoints(id)
);