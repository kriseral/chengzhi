package dev.chengzhi.document;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;

@TableName("documents")
public class DocumentEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String title;

    private String ownerId;

    private int version;

    private Instant updatedAt;

    public DocumentEntity() {
    }

    public DocumentEntity(String id, String title, String ownerId, int version, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.ownerId = ownerId;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public void publish(String title, int version, Instant updatedAt) {
        this.title = title;
        this.version = version;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOwnerId() { return ownerId; }
    public int getVersion() { return version; }
    public Instant getUpdatedAt() { return updatedAt; }
}
