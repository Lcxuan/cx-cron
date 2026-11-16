UPDATE admin_users SET status = 1 WHERE deleted = 0;

ALTER TABLE admin_users ALTER COLUMN status SET DEFAULT 1;
COMMENT ON COLUMN admin_users.status IS '状态：0-禁用，1-启用';