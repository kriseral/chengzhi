package dev.chengzhi.acl;

import java.util.List;
import org.springframework.stereotype.Component;

import dev.chengzhi.config.AppProperties;

@Component
public class AclPolicy {

    private final String approvalPolicy;

    public AclPolicy(AppProperties properties) {
        this.approvalPolicy = properties.approval().policy();
    }

    public boolean canRead(AccessScope scope, String ownerId, List<DocumentAclEntity> grants) {
        if (scope.userId().equals(ownerId)) {
            return true;
        }
        for (DocumentAclEntity grant : grants) {
            boolean matched = switch (grant.getSubjectType()) {
                case "USER" -> grant.getSubjectId().equals(scope.userId());
                case "DEPARTMENT" -> grant.getSubjectId().equals(scope.departmentId());
                case "ROLE" -> scope.roles().contains(grant.getSubjectId());
                default -> false;
            };
            if (matched) {
                return true;
            }
        }
        return false;
    }

    public boolean canWrite(AccessScope scope, String ownerId, List<DocumentAclEntity> grants) {
        if (scope.userId().equals(ownerId)) {
            return true;
        }
        // 非所有者无法直接写入文档
        return false;
    }

    public boolean isApproved() {
        return "never".equals(approvalPolicy);
    }
}
