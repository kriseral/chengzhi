package dev.chengzhi.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    @Select("""
            select id, username, password_hash, department_id
            from app_users
            where username = #{username}
            """)
    UserEntity findByUsername(String username);
}
