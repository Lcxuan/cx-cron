ALTER TABLE scheduled_tasks ADD COLUMN IF NOT EXISTS email_notification_policy varchar(20) NOT NULL DEFAULT 'ALL';
COMMENT ON COLUMN scheduled_tasks.email_notification_policy IS '邮件通知策略：OFF、FAILURE、ALL';

CREATE TABLE IF NOT EXISTS system_email_config (
    id bigint NOT NULL,
    enabled boolean NOT NULL DEFAULT false,
    recipient varchar(255),
    from_address varchar(255),
    smtp_host varchar(255),
    smtp_port integer,
    smtp_username varchar(255),
    smtp_password_ciphertext text,
    smtp_protocol varchar(10),
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0
);
COMMENT ON TABLE system_email_config IS '系统邮件配置表';
COMMENT ON COLUMN system_email_config.id IS '主键 ID';
COMMENT ON COLUMN system_email_config.enabled IS '是否启用邮件通知';
COMMENT ON COLUMN system_email_config.recipient IS '全局通知收件人';
COMMENT ON COLUMN system_email_config.from_address IS '邮件发件地址';
COMMENT ON COLUMN system_email_config.smtp_host IS 'SMTP 服务器地址';
COMMENT ON COLUMN system_email_config.smtp_port IS 'SMTP 服务器端口';
COMMENT ON COLUMN system_email_config.smtp_username IS 'SMTP 用户名';
COMMENT ON COLUMN system_email_config.smtp_password_ciphertext IS 'AES-GCM 加密后的 SMTP 密码';
COMMENT ON COLUMN system_email_config.smtp_protocol IS 'SMTP 协议：smtp 或 smtps';
COMMENT ON COLUMN system_email_config.create_time IS '创建时间';
COMMENT ON COLUMN system_email_config.create_by IS '创建人 ID';
COMMENT ON COLUMN system_email_config.update_time IS '更新时间';
COMMENT ON COLUMN system_email_config.update_by IS '更新人 ID';
COMMENT ON COLUMN system_email_config.deleted IS '逻辑删除标记：0-未删除，1-已删除';

CREATE TABLE IF NOT EXISTS task_email_notifications (
    id bigint NOT NULL,
    execution_record_id bigint NOT NULL,
    scheduled_task_id bigint NOT NULL,
    task_name varchar(255),
    cron_expression varchar(100),
    recipient varchar(255),
    subject varchar(500),
    html_body text,
    status varchar(20) NOT NULL,
    error_message varchar(1000),
    sent_time timestamp,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by bigint,
    update_time timestamp,
    update_by bigint,
    deleted smallint NOT NULL DEFAULT 0
);
ALTER TABLE task_email_notifications ADD COLUMN IF NOT EXISTS subject varchar(500);
ALTER TABLE task_email_notifications ADD COLUMN IF NOT EXISTS html_body text;
COMMENT ON TABLE task_email_notifications IS '任务邮件通知记录表';
COMMENT ON COLUMN task_email_notifications.id IS '主键 ID';
COMMENT ON COLUMN task_email_notifications.execution_record_id IS '任务执行记录 ID';
COMMENT ON COLUMN task_email_notifications.scheduled_task_id IS '定时任务 ID';
COMMENT ON COLUMN task_email_notifications.task_name IS '任务名称';
COMMENT ON COLUMN task_email_notifications.cron_expression IS 'Cron 表达式';
COMMENT ON COLUMN task_email_notifications.recipient IS '通知收件人';
COMMENT ON COLUMN task_email_notifications.subject IS '邮件主题快照';
COMMENT ON COLUMN task_email_notifications.html_body IS '邮件 HTML 正文快照';
COMMENT ON COLUMN task_email_notifications.status IS '通知状态：PENDING、SENT、FAILED、SKIPPED';
COMMENT ON COLUMN task_email_notifications.error_message IS '发送失败或跳过原因';
COMMENT ON COLUMN task_email_notifications.sent_time IS '发送成功时间';
COMMENT ON COLUMN task_email_notifications.create_time IS '创建时间';
COMMENT ON COLUMN task_email_notifications.create_by IS '创建人 ID';
COMMENT ON COLUMN task_email_notifications.update_time IS '更新时间';
COMMENT ON COLUMN task_email_notifications.update_by IS '更新人 ID';
COMMENT ON COLUMN task_email_notifications.deleted IS '逻辑删除标记：0-未删除，1-已删除';

CREATE TABLE IF NOT EXISTS system_menus (
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
INSERT INTO system_menus (id, parent_id, name, path, component, component_name, type, icon, sort, enabled, visible, keep_alive, always_show) VALUES (2, 0, '邮件配置', '/system/email-notification', 'system/email-notification/index', 'EmailNotification', 'MENU', 'lucide:mail', 2, 1, 0, 0, 0);
INSERT INTO system_menus (id, parent_id, name, path, component, component_name, type, icon, sort, enabled, visible, keep_alive, always_show) VALUES (3, 0, '邮件日志', '/system/email-logs', 'system/email-logs/index', 'EmailLogs', 'MENU', 'lucide:mail-search', 3, 1, 0, 0, 0);
