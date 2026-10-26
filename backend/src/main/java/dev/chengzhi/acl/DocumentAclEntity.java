package dev.chengzhi.acl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("document_acl")
public class DocumentAclEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String documentId;

    private String subjectType;

    private String subjectId;

    public DocumentAclEntity() {
    }

    public DocumentAclEntity(String id, String documentId, String subjectType, String subjectId) {
        this.id = id;
        this.documentId = documentId;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
    }

    public String getDocumentId() { return documentId; }
    public String getSubjectType() { return subjectType; }
    public String getSubjectId() { return subjectId; }
}
