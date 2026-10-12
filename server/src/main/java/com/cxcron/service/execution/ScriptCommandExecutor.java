package com.cxcron.service.execution;

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
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Thread outputReader = new Thread(() -> {
                try (var stream = process.getInputStream()) {
                    byte[] buffer = new byte[8192];
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
                outputReader.join();
                return new ExecutionResult(summary, output.toString(StandardCharsets.UTF_8));
            } catch (InterruptedException exception) {
                process.destroyForcibly();
                try {
                    outputReader.join();
                } catch (InterruptedException ignored) {
                }
                Thread.currentThread().interrupt();
                return new ExecutionResult("执行被中断", output.toString(StandardCharsets.UTF_8));
            }
        } catch (IOException | IllegalArgumentException exception) {
            return new ExecutionResult("执行失败：" + exception.getMessage(), "");
        }
    }

    public record ExecutionResult(String summary, String executionLog) {
    }

}
