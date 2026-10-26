package dev.chengzhi.acl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DocumentAclMapper extends BaseMapper<DocumentAclEntity> {

    @Select("""
            select id, document_id, subject_type, subject_id
            from document_acl
            where document_id = #{documentId}
            """)
    List<DocumentAclEntity> findByDocumentId(String documentId);

    @Delete("delete from document_acl where document_id = #{documentId}")
    int deleteByDocumentId(String documentId);
}
