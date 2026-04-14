CREATE TABLE task (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      status VARCHAR(20) NOT NULL,
                      creator_id BIGINT NOT NULL,
                      approver_id BIGINT,
                      parent_id BIGINT,
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP
);

CREATE TABLE sub_task (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          parent_task_id BIGINT NOT NULL,
                          assignee_id BIGINT NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
);
