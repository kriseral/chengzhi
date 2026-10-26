package dev.chengzhi.auth;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("app_users")
public class UserEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String username;

    private String passwordHash;

    private String departmentId;

    public UserEntity() {
    }

    public UserEntity(String id, String username, String passwordHash, String departmentId) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.departmentId = departmentId;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDepartmentId() { return departmentId; }
}
