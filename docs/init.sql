CREATE TABLE admin_users (
    id bigint NOT NULL,
    username varchar(64) NOT NULL,
    password varchar(255) NOT NULL,
    nickname varchar(64) NOT NULL,
    status smallint NOT NULL DEFAULT 0,
    last_login_time timestamp,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0,
    CONSTRAINT pk_admin_users PRIMARY KEY (id)
);
COMMENT ON TABLE admin_users IS '管理员用户表';
COMMENT ON COLUMN admin_users.id IS '主键 ID';
COMMENT ON COLUMN admin_users.username IS '登录用户名';
COMMENT ON COLUMN admin_users.password IS '密码哈希';
COMMENT ON COLUMN admin_users.nickname IS '显示名称';
COMMENT ON COLUMN admin_users.status IS '状态：0-启用，1-禁用';
COMMENT ON COLUMN admin_users.last_login_time IS '最后登录时间';
COMMENT ON COLUMN admin_users.create_time IS '创建时间';
COMMENT ON COLUMN admin_users.create_by IS '创建人 ID';
COMMENT ON COLUMN admin_users.update_time IS '更新时间';
COMMENT ON COLUMN admin_users.update_by IS '更新人 ID';
COMMENT ON COLUMN admin_users.deleted IS '逻辑删除标记：0-未删除，1-已删除';

INSERT INTO admin_users (id, username, password, nickname, status)
VALUES (1, 'admin', '$2a$10$XZ4gi4F4F1rlQgFwHds0LOH/uTibvvrNYoaMF9sWxHgw0dpWnKu0m', '管理员', 0);

CREATE TABLE refresh_tokens (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    user_type varchar(20) NOT NULL,
    refresh_token varchar(255) NOT NULL,
    expires_time timestamp NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);
COMMENT ON TABLE refresh_tokens IS '刷新令牌表';
COMMENT ON COLUMN refresh_tokens.id IS '主键 ID';
COMMENT ON COLUMN refresh_tokens.user_id IS '用户 ID';
COMMENT ON COLUMN refresh_tokens.user_type IS '用户类型';
COMMENT ON COLUMN refresh_tokens.refresh_token IS '原始 Refresh Token';
COMMENT ON COLUMN refresh_tokens.expires_time IS '过期时间';
COMMENT ON COLUMN refresh_tokens.create_time IS '创建时间';
COMMENT ON COLUMN refresh_tokens.create_by IS '创建人 ID';
COMMENT ON COLUMN refresh_tokens.update_time IS '更新时间';
COMMENT ON COLUMN refresh_tokens.update_by IS '更新人 ID';
COMMENT ON COLUMN refresh_tokens.deleted IS '逻辑删除标记：0-未删除，1-已删除';

CREATE TABLE access_tokens (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    user_type varchar(20) NOT NULL,
    refresh_token_id bigint NOT NULL,
    access_token varchar(255) NOT NULL,
    expires_time timestamp NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0,
    CONSTRAINT pk_access_tokens PRIMARY KEY (id)
);
COMMENT ON TABLE access_tokens IS '访问令牌表';
COMMENT ON COLUMN access_tokens.id IS '主键 ID';
COMMENT ON COLUMN access_tokens.user_id IS '用户 ID';
COMMENT ON COLUMN access_tokens.user_type IS '用户类型';
COMMENT ON COLUMN access_tokens.refresh_token_id IS '刷新令牌 ID';
COMMENT ON COLUMN access_tokens.access_token IS '原始 Access Token';
COMMENT ON COLUMN access_tokens.expires_time IS '过期时间';
COMMENT ON COLUMN access_tokens.create_time IS '创建时间';
COMMENT ON COLUMN access_tokens.create_by IS '创建人 ID';
COMMENT ON COLUMN access_tokens.update_time IS '更新时间';
COMMENT ON COLUMN access_tokens.update_by IS '更新人 ID';
COMMENT ON COLUMN access_tokens.deleted IS '逻辑删除标记：0-未删除，1-已删除';

CREATE TABLE scheduled_tasks (
    id bigint NOT NULL,
    task_type varchar(32) NOT NULL,
    cron_expression varchar(128) NOT NULL,
    enabled smallint NOT NULL DEFAULT 1,
    xxl_job_id bigint,
    last_schedule_status varchar(32),
    last_schedule_time timestamp,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0,
    CONSTRAINT pk_scheduled_tasks PRIMARY KEY (id)
);
COMMENT ON TABLE scheduled_tasks IS '定时任务表';
COMMENT ON COLUMN scheduled_tasks.id IS '主键 ID';
COMMENT ON COLUMN scheduled_tasks.task_type IS '任务类型，例如 DAILY_CHECKIN';
COMMENT ON COLUMN scheduled_tasks.cron_expression IS 'Cron 表达式';
COMMENT ON COLUMN scheduled_tasks.enabled IS '是否启用：0-暂停，1-启用';
COMMENT ON COLUMN scheduled_tasks.xxl_job_id IS 'XXL-JOB 任务 ID';
COMMENT ON COLUMN scheduled_tasks.last_schedule_status IS '最近调度状态';
COMMENT ON COLUMN scheduled_tasks.last_schedule_time IS '最近调度时间';
COMMENT ON COLUMN scheduled_tasks.create_time IS '创建时间';
COMMENT ON COLUMN scheduled_tasks.create_by IS '创建人 ID';
COMMENT ON COLUMN scheduled_tasks.update_time IS '更新时间';
COMMENT ON COLUMN scheduled_tasks.update_by IS '更新人 ID';
COMMENT ON COLUMN scheduled_tasks.deleted IS '逻辑删除标记：0-未删除，1-已删除';

CREATE TABLE execution_records (
    id bigint NOT NULL,
    scheduled_task_id bigint NOT NULL,
    task_type varchar(32) NOT NULL,
    trigger_type varchar(16) NOT NULL,
    idempotency_key varchar(128) NOT NULL,
    result_type varchar(32) NOT NULL,
    summary varchar(512),
    xxl_job_log_id bigint,
    started_time timestamp NOT NULL,
    finished_time timestamp,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0,
    CONSTRAINT pk_execution_records PRIMARY KEY (id)
);
COMMENT ON TABLE execution_records IS '任务执行记录表';
COMMENT ON COLUMN execution_records.id IS '主键 ID';
COMMENT ON COLUMN execution_records.scheduled_task_id IS '定时任务 ID';
COMMENT ON COLUMN execution_records.task_type IS '任务类型';
COMMENT ON COLUMN execution_records.trigger_type IS '触发方式：SCHEDULED-调度，MANUAL-手动';
COMMENT ON COLUMN execution_records.idempotency_key IS '业务幂等键';
COMMENT ON COLUMN execution_records.result_type IS '执行结果：SUCCESS、ALREADY_COMPLETED、SESSION_EXPIRED、FAILED';
COMMENT ON COLUMN execution_records.summary IS '脱敏执行摘要';
COMMENT ON COLUMN execution_records.xxl_job_log_id IS 'XXL-JOB 执行日志 ID';
COMMENT ON COLUMN execution_records.started_time IS '开始执行时间';
COMMENT ON COLUMN execution_records.finished_time IS '结束执行时间';
COMMENT ON COLUMN execution_records.create_time IS '创建时间';
COMMENT ON COLUMN execution_records.create_by IS '创建人 ID';
COMMENT ON COLUMN execution_records.update_time IS '更新时间';
COMMENT ON COLUMN execution_records.update_by IS '更新人 ID';
COMMENT ON COLUMN execution_records.deleted IS '逻辑删除标记：0-未删除，1-已删除';
