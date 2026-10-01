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
    name varchar(128) NOT NULL,
    cron_expression varchar(128) NOT NULL,
    script_path varchar(512) NOT NULL,
    run_command varchar(512) NOT NULL,
    enabled smallint NOT NULL DEFAULT 1,
    quartz_job_name varchar(128),
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
COMMENT ON COLUMN scheduled_tasks.cron_expression IS 'Cron 表达式';
COMMENT ON COLUMN scheduled_tasks.enabled IS '是否启用：0-暂停，1-启用';
COMMENT ON COLUMN scheduled_tasks.quartz_job_name IS 'Quartz Job 名称';
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
    trigger_type varchar(16) NOT NULL,
    idempotency_key varchar(128) NOT NULL,
    result_type varchar(32) NOT NULL,
    summary varchar(512),
    quartz_fire_instance_id varchar(128),
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
COMMENT ON COLUMN execution_records.trigger_type IS '触发方式：SCHEDULED-调度，MANUAL-手动';
COMMENT ON COLUMN execution_records.idempotency_key IS '业务幂等键';
COMMENT ON COLUMN execution_records.result_type IS '执行结果：SUCCESS、ALREADY_COMPLETED、SESSION_EXPIRED、FAILED';
COMMENT ON COLUMN execution_records.summary IS '脱敏执行摘要';
COMMENT ON COLUMN execution_records.quartz_fire_instance_id IS 'Quartz 触发实例 ID';
COMMENT ON COLUMN execution_records.started_time IS '开始执行时间';
COMMENT ON COLUMN execution_records.finished_time IS '结束执行时间';
COMMENT ON COLUMN execution_records.create_time IS '创建时间';
COMMENT ON COLUMN execution_records.create_by IS '创建人 ID';
COMMENT ON COLUMN execution_records.update_time IS '更新时间';
COMMENT ON COLUMN execution_records.update_by IS '更新人 ID';
COMMENT ON COLUMN execution_records.deleted IS '逻辑删除标记：0-未删除，1-已删除';

CREATE TABLE system_menus (
    id bigint NOT NULL,
    parent_id bigint NOT NULL DEFAULT 0,
    name varchar(100) NOT NULL,
    path varchar(200) NOT NULL,
    component varchar(200),
    component_name varchar(100),
    type varchar(20) NOT NULL,
    icon varchar(100),
    sort integer NOT NULL DEFAULT 0,
    enabled smallint NOT NULL DEFAULT 1,
    visible smallint NOT NULL DEFAULT 0,
    keep_alive smallint NOT NULL DEFAULT 0,
    always_show smallint NOT NULL DEFAULT 0,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0,
    CONSTRAINT pk_system_menus PRIMARY KEY (id)
);
COMMENT ON TABLE system_menus IS '系统菜单表';
COMMENT ON COLUMN system_menus.id IS '菜单 ID';
COMMENT ON COLUMN system_menus.parent_id IS '父菜单 ID，0 表示顶级';
COMMENT ON COLUMN system_menus.name IS '菜单名称';
COMMENT ON COLUMN system_menus.path IS '路由路径';
COMMENT ON COLUMN system_menus.component IS '页面路径，相对 src/views 且不含扩展名';
COMMENT ON COLUMN system_menus.component_name IS '页面路由名称';
COMMENT ON COLUMN system_menus.type IS '菜单类型：DIRECTORY、MENU';
COMMENT ON COLUMN system_menus.icon IS '菜单图标';
COMMENT ON COLUMN system_menus.sort IS '显示顺序，数值越小越靠前';
COMMENT ON COLUMN system_menus.enabled IS '是否启用：0-否，1-是';
COMMENT ON COLUMN system_menus.visible IS '是否隐藏侧栏：0-否，1-是';
COMMENT ON COLUMN system_menus.keep_alive IS '是否缓存：0-否，1-是';
COMMENT ON COLUMN system_menus.always_show IS '是否始终显示父级：0-否，1-是';
COMMENT ON COLUMN system_menus.create_time IS '创建时间';
COMMENT ON COLUMN system_menus.create_by IS '创建人 ID';
COMMENT ON COLUMN system_menus.update_time IS '更新时间';
COMMENT ON COLUMN system_menus.update_by IS '更新人 ID';
COMMENT ON COLUMN system_menus.deleted IS '逻辑删除标记：0-未删除，1-已删除';

INSERT INTO system_menus (id, parent_id, name, path, component, component_name, type, icon, sort, enabled, visible, keep_alive, always_show) VALUES (1, 0, '任务管理', '/task', 'task/index', 'Task', 'MENU', 'lucide:list-todo', 1, 1, 0, 0, 0);

CREATE TABLE qrtz_job_details (
    sched_name varchar(120) NOT NULL,
    job_name varchar(200) NOT NULL,
    job_group varchar(200) NOT NULL,
    description varchar(250),
    job_class_name varchar(250) NOT NULL,
    is_durable boolean NOT NULL,
    is_nonconcurrent boolean NOT NULL,
    is_update_data boolean NOT NULL,
    requests_recovery boolean NOT NULL,
    job_data bytea,
    CONSTRAINT pk_qrtz_job_details PRIMARY KEY (sched_name, job_name, job_group)
);

CREATE TABLE qrtz_triggers (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    job_name varchar(200) NOT NULL,
    job_group varchar(200) NOT NULL,
    description varchar(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state varchar(16) NOT NULL,
    trigger_type varchar(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name varchar(200),
    misfire_instr smallint,
    job_data bytea,
    CONSTRAINT pk_qrtz_triggers PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_triggers_job_details FOREIGN KEY (sched_name, job_name, job_group)
        REFERENCES qrtz_job_details (sched_name, job_name, job_group)
);

CREATE TABLE qrtz_simple_triggers (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL,
    CONSTRAINT pk_qrtz_simple_triggers PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_simple_triggers_triggers FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_cron_triggers (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    cron_expression varchar(120) NOT NULL,
    time_zone_id varchar(80),
    CONSTRAINT pk_qrtz_cron_triggers PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_cron_triggers_triggers FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_simprop_triggers (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    str_prop_1 varchar(512),
    str_prop_2 varchar(512),
    str_prop_3 varchar(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean,
    CONSTRAINT pk_qrtz_simprop_triggers PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_simprop_triggers_triggers FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_blob_triggers (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    blob_data bytea,
    CONSTRAINT pk_qrtz_blob_triggers PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_blob_triggers_triggers FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES qrtz_triggers (sched_name, trigger_name, trigger_group)
);

CREATE TABLE qrtz_calendars (
    sched_name varchar(120) NOT NULL,
    calendar_name varchar(200) NOT NULL,
    calendar bytea NOT NULL,
    CONSTRAINT pk_qrtz_calendars PRIMARY KEY (sched_name, calendar_name)
);

CREATE TABLE qrtz_paused_trigger_grps (
    sched_name varchar(120) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    CONSTRAINT pk_qrtz_paused_trigger_grps PRIMARY KEY (sched_name, trigger_group)
);

CREATE TABLE qrtz_fired_triggers (
    sched_name varchar(120) NOT NULL,
    entry_id varchar(95) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    instance_name varchar(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state varchar(16) NOT NULL,
    job_name varchar(200),
    job_group varchar(200),
    is_nonconcurrent boolean,
    requests_recovery boolean,
    CONSTRAINT pk_qrtz_fired_triggers PRIMARY KEY (sched_name, entry_id)
);

CREATE TABLE qrtz_scheduler_state (
    sched_name varchar(120) NOT NULL,
    instance_name varchar(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL,
    CONSTRAINT pk_qrtz_scheduler_state PRIMARY KEY (sched_name, instance_name)
);

CREATE TABLE qrtz_locks (
    sched_name varchar(120) NOT NULL,
    lock_name varchar(40) NOT NULL,
    CONSTRAINT pk_qrtz_locks PRIMARY KEY (sched_name, lock_name)
);

CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details (sched_name, requests_recovery);
CREATE INDEX idx_qrtz_j_grp ON qrtz_job_details (sched_name, job_group);
CREATE INDEX idx_qrtz_t_j ON qrtz_triggers (sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_t_jg ON qrtz_triggers (sched_name, job_group);
CREATE INDEX idx_qrtz_t_c ON qrtz_triggers (sched_name, calendar_name);
CREATE INDEX idx_qrtz_t_g ON qrtz_triggers (sched_name, trigger_group);
CREATE INDEX idx_qrtz_t_state ON qrtz_triggers (sched_name, trigger_state);
CREATE INDEX idx_qrtz_t_n_state ON qrtz_triggers (sched_name, trigger_name, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_n_g_state ON qrtz_triggers (sched_name, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_next_fire_time ON qrtz_triggers (sched_name, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st ON qrtz_triggers (sched_name, trigger_state, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_misfire ON qrtz_triggers (sched_name, misfire_instr, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st_misfire ON qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_state);
CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers (sched_name, instance_name);
CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON qrtz_fired_triggers (sched_name, instance_name, requests_recovery);
CREATE INDEX idx_qrtz_ft_j_g ON qrtz_fired_triggers (sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_ft_jg ON qrtz_fired_triggers (sched_name, job_group);
CREATE INDEX idx_qrtz_ft_t_g ON qrtz_fired_triggers (sched_name, trigger_name, trigger_group);
CREATE INDEX idx_qrtz_ft_tg ON qrtz_fired_triggers (sched_name, trigger_group);

INSERT INTO qrtz_locks (sched_name, lock_name) VALUES
    ('cxCronScheduler', 'TRIGGER_ACCESS'),
    ('cxCronScheduler', 'JOB_ACCESS'),
    ('cxCronScheduler', 'CALENDAR_ACCESS'),
    ('cxCronScheduler', 'STATE_ACCESS'),
    ('cxCronScheduler', 'MISFIRE_ACCESS');
