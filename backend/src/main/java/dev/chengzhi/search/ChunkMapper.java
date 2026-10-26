package dev.chengzhi.search;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.chengzhi.acl.AccessScope;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ChunkMapper extends BaseMapper<ChunkEntity> {

    @Delete("delete from document_chunks where document_id = #{documentId}")
    int deleteByDocumentId(String documentId);

    List<RetrievedChunk> searchAccessible(
            @Param("scope") AccessScope scope,
            @Param("query") String query,
            @Param("limit") int limit);

    List<RetrievedChunk> searchAccessibleByVector(
            @Param("scope") AccessScope scope,
            @Param("vector") String vector,
            @Param("limit") int limit);

    @org.apache.ibatis.annotations.Update("""
            update document_chunks
            set embedding = #{embedding}::real[]
            where id = #{id}
            """)
    int updateEmbedding(@Param("id") String id, @Param("embedding") String embedding);

    @Select("""
            select count(*)
            from document_chunks
            where document_id = #{documentId} and document_version = #{version}
            """)
    int countCurrent(@Param("documentId") String documentId, @Param("version") int version);
}
