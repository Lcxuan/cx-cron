-- 1. 统一管理员状态设置为：0-禁用，1-启用
UPDATE admin_users SET status = 1 WHERE deleted = 0;

-- 新管理员默认启用
ALTER TABLE admin_users ALTER COLUMN status SET DEFAULT 1;
COMMENT ON COLUMN admin_users.status IS '状态：0-禁用，1-启用';

-- 2. 执行记录增加日志上限相关字段
-- log_size：原始日志字节数；log_truncated：日志是否因超过上限被截断
ALTER TABLE execution_records ADD COLUMN log_size bigint NOT NULL DEFAULT 0;
ALTER TABLE execution_records ADD COLUMN log_truncated boolean NOT NULL DEFAULT false;

-- 回填历史记录的原始日志字节数，历史日志视为未截断
UPDATE execution_records SET log_size = COALESCE(octet_length(execution_log), 0);

COMMENT ON COLUMN execution_records.execution_log IS '命令输出日志，超过上限时截断，可能包含敏感内容';
COMMENT ON COLUMN execution_records.log_size IS '日志原始字节数';
COMMENT ON COLUMN execution_records.log_truncated IS '日志是否因超过上限被截断';
