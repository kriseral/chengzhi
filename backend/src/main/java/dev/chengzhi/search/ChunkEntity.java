package dev.chengzhi.search;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("document_chunks")
public class ChunkEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String documentId;

    private int documentVersion;

    private int chunkIndex;

    private String content;

    public ChunkEntity() {
    }

    public ChunkEntity(String id, String documentId, int documentVersion, int chunkIndex, String content) {
        this.id = id;
        this.documentId = documentId;
        this.documentVersion = documentVersion;
        this.chunkIndex = chunkIndex;
        this.content = content;
    }

    public String getId() { return id; }
    public String getDocumentId() { return documentId; }
    public int getDocumentVersion() { return documentVersion; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }
}
