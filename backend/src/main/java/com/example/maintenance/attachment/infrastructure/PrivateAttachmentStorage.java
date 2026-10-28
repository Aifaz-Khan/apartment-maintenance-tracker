package com.example.maintenance.attachment.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Component
public class PrivateAttachmentStorage {
    private final Path root;

    public PrivateAttachmentStorage(@Value("${app.attachments.storage-path:./var/private-attachments}") String path) {
        this.root = Paths.get(path).toAbsolutePath().normalize();
    }

    public void store(String key, InputStream input) throws IOException {
        Files.createDirectories(root);
        try { Files.setPosixFilePermissions(root, java.nio.file.attribute.PosixFilePermissions.fromString("rwx------")); }
        catch (UnsupportedOperationException ignored) { }
        Path target = resolve(key);
        Path temporary = Files.createTempFile(root, ".upload-", ".tmp");
        try {
            Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            try { Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temporary, target); }
        } finally { Files.deleteIfExists(temporary); }
    }

    public byte[] read(String key) throws IOException { return Files.readAllBytes(resolve(key)); }
    public void delete(String key) throws IOException { Files.deleteIfExists(resolve(key)); }

    private Path resolve(String key) {
        Path target = root.resolve(key).normalize();
        if (!target.getParent().equals(root)) throw new IllegalArgumentException("Invalid storage key.");
        return target;
    }
}
