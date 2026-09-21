package com.cxcron.service.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class ScriptStorage {

    @Value("${task.script.storage-path:uploads/scripts}")
    private String storagePath;

    /**
     * 保存上传脚本并返回绝对路径。
     *
     * @param script 上传的脚本文件
     * @return 保存后的脚本路径
     */
    public String save(MultipartFile script) {
        try {
            Path root = getRoot();
            Files.createDirectories(root);
            String filename = UUID.randomUUID() + "-" + Path.of(script.getOriginalFilename()).getFileName();
            Path target = root.resolve(filename).normalize();
            Files.copy(script.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return target.toString();
        } catch (IOException exception) {
            throw new IllegalStateException("脚本保存失败", exception);
        }
    }

    /**
     * 删除脚本目录中的脚本。
     *
     * @param scriptPath 脚本绝对路径
     */
    public void delete(String scriptPath) {
        if (scriptPath == null || scriptPath.isBlank()) return;
        try {
            Path path = Path.of(scriptPath).toAbsolutePath().normalize();
            if (path.startsWith(getRoot())) Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new IllegalStateException("脚本删除失败", exception);
        }
    }

    /**
     * 获取脚本根目录绝对路径。
     *
     * @return 脚本根目录
     */
    private Path getRoot() {
        return Path.of(storagePath).toAbsolutePath().normalize();
    }
}
