package dev.chengzhi.acl;

import java.util.Set;

public record AccessScope(String userId, String departmentId, Set<String> roles) {
}
