package com.cxcron.service.email.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.cxcron.common.utils.EmailPasswordEncryptor;
import com.cxcron.entity.ExecutionRecordDO;
import com.cxcron.entity.ScheduledTaskDO;
import com.cxcron.entity.SystemEmailConfigDO;
import com.cxcron.entity.TaskEmailNotificationDO;
import com.cxcron.enums.EmailNotificationPolicyEnum;
import com.cxcron.enums.EmailNotificationStatusEnum;
import com.cxcron.mapper.TaskEmailNotificationMapper;
import com.cxcron.service.email.SystemEmailConfigService;
import com.cxcron.service.email.TaskEmailNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Service
@RequiredArgsConstructor
public class TaskEmailNotificationServiceImpl implements TaskEmailNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(TaskEmailNotificationServiceImpl.class);

    private final TaskEmailNotificationMapper notificationMapper;

    private final SystemEmailConfigService configService;

    private final EmailPasswordEncryptor encryptor;

    @Qualifier("taskEmailExecutor") 
    private final Executor executor;

    /**
     * 根据任务通知策略创建通知记录，并异步发送邮件。
     *
     * @param task 已执行的定时任务
     * @param record 本次执行记录
     */
    @Override
    public void notifyAfterExecution(ScheduledTaskDO task, ExecutionRecordDO record) {
        // 解析任务通知策略；策略缺失时由枚举使用默认值 ALL。
        EmailNotificationPolicyEnum policy;
        try {
            policy = EmailNotificationPolicyEnum.resolve(task.getEmailNotificationPolicy());
        } catch (Exception e) {
            return;
        }
        if (!policy.shouldNotify(record.getResultType())) return;

        // 初始化通知记录的任务和执行记录关联信息。
        TaskEmailNotificationDO notification = new TaskEmailNotificationDO();
        notification.setExecutionRecordId(record.getId());
        notification.setScheduledTaskId(task.getId());
        notification.setTaskName(task.getName());
        notification.setCronExpression(task.getCronExpression());
        // 获取全局邮件配置；未启用或读取失败时记录相应通知状态。
        SystemEmailConfigDO config;
        try {
            config = configService.requireEnabled();
        } catch (com.cxcron.enums.exception.BusinessException e) {
            if (e.getErrorCode() == com.cxcron.enums.exception.BusinessErrorCodeConstants.EMAIL_CONFIG_DISABLED) {
                notification.setStatus(EmailNotificationStatusEnum.SKIPPED.name());
                notification.setErrorMessage("全局邮件通知未启用");
            } else {
                notification.setStatus(EmailNotificationStatusEnum.FAILED.name());
                notification.setErrorMessage("邮件配置无效");
            }
            notificationMapper.insert(notification);
            return;
        } catch (Exception e) {
            notification.setStatus(EmailNotificationStatusEnum.FAILED.name());
            notification.setErrorMessage("邮件配置读取失败");
            notificationMapper.insert(notification);
            return;
        }
        notification.setRecipient(config.getRecipient());
        String result = "SUCCESS".equals(record.getResultType()) ? "成功" : "失败";
        notification.setSubject("[cx-cron] 任务执行" + result + "：" + task.getName());
        notification.setHtmlBody(buildHtmlBody(task, record));
        notification.setStatus(EmailNotificationStatusEnum.PENDING.name());
        // 发送异步邮件
        try {
            notificationMapper.insert(notification);
            executor.execute(() -> send(notification, task, record, config));
        } catch (RejectedExecutionException e) {
            notification.setStatus(EmailNotificationStatusEnum.FAILED.name());
            notification.setErrorMessage("邮件发送队列繁忙");
            notificationMapper.updateById(notification);
        } catch (Exception e) {
            log.warn("无法创建任务邮件通知记录", e);
        }
    }

    /**
     * 向全局配置的收件人发送测试邮件。
     */
    @Override
    public void sendTest() {
        // 测试邮件只发送给全局配置的收件人。
        SystemEmailConfigDO config = configService.requireEnabled();
        sendMessage(config, "[cx-cron] 邮件通知测试", "<html><body><p>发送时间：" + LocalDateTime.now() + "</p><p>SMTP 配置验证成功</p></body></html>");
    }

    /**
     * 发送任务通知邮件并更新通知记录状态。
     *
     * @param notification 邮件通知记录
     * @param task 关联任务
     * @param record 关联执行记录
     * @param config 全局邮件配置
     */
    private void send(TaskEmailNotificationDO notification, ScheduledTaskDO task, ExecutionRecordDO record, SystemEmailConfigDO config) {
        // 根据执行记录生成邮件内容，并按发送结果更新通知记录。
        try {
            sendMessage(config, notification.getSubject(), notification.getHtmlBody());
            notification.setStatus(EmailNotificationStatusEnum.SENT.name());
            notification.setSentTime(LocalDateTime.now());
        } catch (Exception e) {
            log.warn("任务邮件发送失败: taskId={}", task.getId(), e);
            notification.setStatus(EmailNotificationStatusEnum.FAILED.name());
            notification.setErrorMessage("SMTP 邮件发送失败：" + e.getClass().getSimpleName());
        }
        notificationMapper.updateById(notification);
    }

    /**
     * 根据任务和执行记录生成 HTML 邮件正文，并对动态文本进行转义。
     *
     * @param task 定时任务
     * @param record 任务执行记录
     * @return HTML 格式的邮件正文
     */
    private static String buildHtmlBody(ScheduledTaskDO task, ExecutionRecordDO record) {
        String summary = record.getSummary() == null ? "" : record.getSummary();
        boolean success = "SUCCESS".equals(record.getResultType());
        String result = success ? "执行成功" : "执行失败";
        String resultColor = success ? "#15803d" : "#b91c1c";
        return "<html><body style=\"margin:0;padding:24px;background:#f1f5f9;font-family:Arial,'Microsoft YaHei',sans-serif;color:#1e293b;\">"
                + "<div style=\"max-width:680px;margin:0 auto;background:#ffffff;border:1px solid #e2e8f0;border-radius:12px;overflow:hidden;\">"
                + "<div style=\"padding:24px 28px;background:#0f172a;color:#ffffff;\">"
                + "<div style=\"font-size:13px;color:#cbd5e1;\">cx-cron 任务通知</div>"
                + "<h2 style=\"margin:8px 0 0;font-size:22px;\">任务执行结果</h2></div>"
                + "<div style=\"padding:24px 28px;\"><div style=\"display:inline-block;margin-bottom:20px;padding:7px 12px;border-radius:20px;background:"
                + (success ? "#dcfce7" : "#fee2e2") + ";color:" + resultColor + ";font-weight:600;\">"
                + result + "</div>"
                + "<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">"
                + htmlRow("任务名称", HtmlUtils.htmlEscape(task.getName()))
                + htmlRow("任务 ID", String.valueOf(task.getId()))
                + htmlRow("触发方式", "MANUAL".equals(record.getTriggerType()) ? "手动" : "定时调度")
                + htmlRow("开始时间", record.getStartedTime() == null ? "null" : LocalDateTimeUtil.format(record.getStartedTime(), "yyyy-MM-dd HH:mm:ss"))
                + htmlRow("结束时间", record.getFinishedTime() == null ? "null" : LocalDateTimeUtil.format(record.getFinishedTime(), "yyyy-MM-dd HH:mm:ss"))
                + "</table><h3 style=\"margin:24px 0 10px;font-size:15px;\">执行摘要</h3>"
                + "<pre style=\"margin:0;padding:16px;background:#f8fafc;border:1px solid #e2e8f0;border-radius:8px;white-space:pre-wrap;word-break:break-word;font:13px/1.6 Consolas,monospace;color:#334155;\">"
                + HtmlUtils.htmlEscape(summary) + "</pre></div></div></body></html>";
    }

    private static String htmlRow(String label, String value) {
        return "<tr><th style=\"width:120px;padding:11px 12px;border-bottom:1px solid #e2e8f0;text-align:left;color:#64748b;font-weight:normal;\">"
                + label + "</th><td style=\"padding:11px 12px;border-bottom:1px solid #e2e8f0;\">"
                + value + "</td></tr>";
    }

    /**
     * 使用全局 SMTP 配置发送 HTML 邮件。
     *
     * @param config 全局邮件配置
     * @param subject 邮件主题
     * @param html 邮件 HTML 正文
     */
    private void sendMessage(SystemEmailConfigDO config, String subject, String html) {
        if (!encryptor.isAvailable()) throw new IllegalStateException("未配置邮件密码加密密钥");
        if (!StringUtils.hasText(config.getSmtpPasswordCiphertext())) throw new IllegalStateException("邮件密码未配置");
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getSmtpHost()); 
        sender.setPort(config.getSmtpPort()); 
        sender.setUsername(config.getSmtpUsername());
        sender.setPassword(encryptor.decrypt(config.getSmtpPasswordCiphertext()));
        String protocol = config.getSmtpProtocol().toLowerCase();
        sender.setProtocol(protocol); 
        sender.setDefaultEncoding("UTF-8");
        Properties properties = sender.getJavaMailProperties();
        properties.put("mail." + protocol + ".auth", "true");
        if ("smtp".equals(protocol)){
            properties.put("mail.smtp.starttls.enable", "true");
        } else {
            properties.put("mail.smtps.ssl.enable", "true");
        }
        try {
            MimeMessageHelper message = new MimeMessageHelper(sender.createMimeMessage(), true, "UTF-8");
            message.setFrom(StringUtils.hasText(config.getFromAddress()) ? config.getFromAddress() : config.getSmtpUsername());
            message.setTo(config.getRecipient());
            message.setSubject(subject);
            message.setText(html, true);
            sender.send(message.getMimeMessage());
        } catch (MessagingException e) {
            throw new IllegalStateException("无法创建 HTML 邮件", e);
        }
    }
}
