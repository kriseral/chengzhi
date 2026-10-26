package dev.chengzhi.acl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AclPolicyTest {

    private final AclPolicy policy = new AclPolicy();
    private final AccessScope student = new AccessScope("usr-student", "dept-student", Set.of("STUDENT"));

    @Test
    void ownerCanAlwaysRead() {
        assertThat(policy.canRead(student, "usr-student", List.of())).isTrue();
    }

    @Test
    void departmentAndRoleGrantsAreChecked() {
        DocumentAclEntity department = new DocumentAclEntity("acl-1", "doc-1", "DEPARTMENT", "dept-student");
        DocumentAclEntity role = new DocumentAclEntity("acl-2", "doc-2", "ROLE", "HR");

        assertThat(policy.canRead(student, "usr-hr", List.of(department))).isTrue();
        assertThat(policy.canRead(student, "usr-hr", List.of(role))).isFalse();
    }
}
