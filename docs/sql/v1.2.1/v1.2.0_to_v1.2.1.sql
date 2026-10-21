COMMENT ON COLUMN execution_records.result_type IS '执行结果：SUCCESS-执行成功，FAILED-执行失败，SKIPPED_CONCURRENT-因已有执行实例而跳过';

ALTER TABLE scheduled_tasks ADD COLUMN IF NOT EXISTS script_filename varchar(255);
COMMENT ON COLUMN scheduled_tasks.script_filename IS '上传脚本的原始文件名';

UPDATE scheduled_tasks
SET script_filename = regexp_replace(
        script_path,
        '^.*/[[:xdigit:]]{8}-[[:xdigit:]]{4}-[[:xdigit:]]{4}-[[:xdigit:]]{4}-[[:xdigit:]]{12}-',
        ''
    )
WHERE script_filename IS NULL
  AND script_path ~ '/[[:xdigit:]]{8}-[[:xdigit:]]{4}-[[:xdigit:]]{4}-[[:xdigit:]]{4}-[[:xdigit:]]{12}-[^/]+$';