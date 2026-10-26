package dev.chengzhi.auth;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserRoleMapper {

    @Select("select role_name from user_roles where user_id = #{userId}")
    List<String> findRoleNames(String userId);

    @Insert("insert into user_roles(user_id, role_name) values(#{userId}, #{roleName})")
    int insertRole(@Param("userId") String userId, @Param("roleName") String roleName);
}
