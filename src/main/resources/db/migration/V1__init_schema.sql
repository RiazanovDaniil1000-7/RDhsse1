-- Создание таблицы для задач (Task)
CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    completed   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    due_date    DATE,
    priority    VARCHAR(50), -- Для хранения Enum (например, LOW, MEDIUM, HIGH)
    CONSTRAINT tasks_title_not_empty CHECK (length(title) > 0)
);

-- Создание таблицы для тегов (Set<String> tags в Task)
-- Используем отдельную таблицу для реализации связи "один ко многим" для строк
CREATE TABLE task_tags
(
    task_id  BIGINT       NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE,
    PRIMARY KEY (task_id, tag_name)
);

-- Создание таблицы для вложений (TaskAttachment)
CREATE TABLE task_attachments
(
    id               BIGSERIAL PRIMARY KEY,
    task_id          BIGINT       NOT NULL,
    file_name        VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    content_type     VARCHAR(100),
    file_size        BIGINT       NOT NULL,
    uploaded_at      TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attachment_task FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE
);

-- Индексы для оптимизации поиска
CREATE INDEX idx_task_attachments_task_id ON task_attachments (task_id);
CREATE INDEX idx_task_tags_task_id ON task_tags (task_id);