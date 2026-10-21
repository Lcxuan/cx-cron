package com.cxcron.service.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
            String filename = UUID.randomUUID().toString();
            Path target = root.resolve(filename);
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
     * 解析已存储的脚本路径，确保指向存储目录中的文件
     */
    public Path resolve(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) throw new IllegalArgumentException("脚本不可用");
        try {
            Path root = getRoot().toRealPath();
            Path stored = Path.of(storedPath);
            Path script = (stored.isAbsolute() ? stored : root.resolve(stored)).toRealPath();
            if (!script.startsWith(root) || !Files.isRegularFile(script)) {
                throw new IllegalArgumentException("脚本不可用");
            }
            return script;
        } catch (IOException | InvalidPathException exception) {
            throw new IllegalArgumentException("脚本不可用");
        }
    }

    /**
     * 从上传文件名中提取不包含目录部分的原始文件名。
     */
    public String getOriginalFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) return "script";
        String filename = originalFilename.replace('\\', '/');
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        return filename.isBlank() ? "script" : filename;
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
