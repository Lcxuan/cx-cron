package com.cxcron.service.execution;

import com.cxcron.config.TaskExecutionProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class ScriptCommandExecutor {

    private static final Set<String> SHELLS = Set.of("shell", "shell.exe", "cmd", "cmd.exe", "powershell", "powershell.exe", "sh");

    private static final int OUTPUT_BUFFER_SIZE = 8192;
    
    private static final long OUTPUT_READER_JOIN_TIMEOUT_SECONDS = 5;

    @Resource
    private TaskExecutionProperties taskExecutionProperties;

    public List<String> command(String runCommand, Path script) {
        Path scriptPath = script.toAbsolutePath().normalize();
        if (!script.isAbsolute() || !Files.isRegularFile(scriptPath)) throw new IllegalArgumentException("脚本路径无效");
        List<String> command = Arrays.stream(runCommand.trim().split("\\s+"))
                .filter(value -> !value.isBlank())
                .toList();
        if (command.isEmpty() || SHELLS.contains(command.get(0).toLowerCase())) {
            throw new IllegalArgumentException("运行命令不允许使用 shell");
        }
        if (command.stream().noneMatch(value -> value.contains("{script}"))) {
            throw new IllegalArgumentException("运行命令必须包含 {script} 占位符");
        }
        return command.stream().map(value -> value.replace("{script}", scriptPath.toString())).toList();
    }

    public ExecutionResult execute(String runCommand, Path script) {
        try {
            Process process = new ProcessBuilder(command(runCommand, script)).redirectErrorStream(true).start();
            LimitedByteArrayOutputStream output = new LimitedByteArrayOutputStream(
                    taskExecutionProperties.getMaxLogBytes());
            Thread outputReader = new Thread(() -> {
                try (var stream = process.getInputStream()) {
                    byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
                    int bytesRead;
                    while ((bytesRead = stream.read(buffer)) != -1) output.write(buffer, 0, bytesRead);
                } catch (IOException ignored) {
                }
            });
            outputReader.setDaemon(true);
            outputReader.start();

            try {
                String summary;
                if (!process.waitFor(5, TimeUnit.MINUTES)) {
                    process.destroyForcibly();
                    process.waitFor();
                    summary = "执行超时";
                } else {
                    summary = process.exitValue() == 0 ? "执行成功" : "执行失败，退出状态：" + process.exitValue();
                }
                waitForOutputReader(outputReader, process);
                return toExecutionResult(summary, output);
            } catch (InterruptedException exception) {
                process.destroyForcibly();
                waitForOutputReader(outputReader, process);
                Thread.currentThread().interrupt();
                return toExecutionResult("执行被中断", output);
            }
        } catch (IOException | IllegalArgumentException exception) {
            return new ExecutionResult("执行失败：" + exception.getMessage(), "", 0L, false);
        }
    }

    /**
     * 等待输出读取线程结束，避免子进程持有管道导致无限阻塞。
     */
    private void waitForOutputReader(Thread outputReader, Process process) {
        try {
            outputReader.join(TimeUnit.SECONDS.toMillis(OUTPUT_READER_JOIN_TIMEOUT_SECONDS));
            if (outputReader.isAlive()) {
                try {
                    process.getInputStream().close();
                } catch (IOException ignored) {
                }
                outputReader.interrupt();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 将截断后的输出和元数据组装为执行结果。
     */
    private ExecutionResult toExecutionResult(String summary, LimitedByteArrayOutputStream output) {
        String executionLog = output.toString(StandardCharsets.UTF_8);
        if (output.isTruncated()) {
            executionLog += System.lineSeparator()
                    + "...[日志已截断，原始大小 " + output.getTotalBytes() + " 字节]";
        }
        return new ExecutionResult(summary, executionLog, output.getTotalBytes(), output.isTruncated());
    }

    public record ExecutionResult(String summary, String executionLog, long logSize, boolean logTruncated) {
    }

    /**
     * 限制最大写入字节数的输出流，超出部分继续消费但不再保存。
     */
    private static final class LimitedByteArrayOutputStream extends ByteArrayOutputStream {

        private final int maxBytes;
        private long totalBytes;
        private boolean truncated;

        private LimitedByteArrayOutputStream(int maxBytes) {
            this.maxBytes = Math.max(0, maxBytes);
        }

        @Override
        public synchronized void write(byte[] bytes, int offset, int length) {
            totalBytes += length;
            int remaining = maxBytes - size();
            if (remaining > 0) {
                super.write(bytes, offset, Math.min(length, remaining));
            }
            if (length > remaining) {
                truncated = true;
            }
        }

        @Override
        public synchronized void write(int value) {
            totalBytes++;
            if (size() < maxBytes) {
                super.write(value);
            } else {
                truncated = true;
            }
        }

        private long getTotalBytes() {
            return totalBytes;
        }

        private boolean isTruncated() {
            return truncated;
        }
    }

}
