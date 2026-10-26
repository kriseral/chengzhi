<script setup>
import { onMounted, reactive, ref } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { knowledgeApi } from "../../api/knowledge";
import { useSession } from "../auth/session";

const { session } = useSession();
const documents = ref([]);
const loading = ref(false);
const saving = ref(false);
const form = reactive(emptyForm());

function emptyForm() {
  return {
    id: "",
    title: "",
    content: "",
    grants: [{ subjectType: "ROLE", subjectId: "STUDENT" }]
  };
}

async function loadDocuments() {
  loading.value = true;
  try {
    documents.value = await knowledgeApi.listDocuments(session.value.token);
  } finally {
    loading.value = false;
  }
}

function editDocument(document) {
  form.id = document.id;
  form.title = document.title;
  form.content = "";
  form.grants = document.grants.map((grant) => ({ ...grant }));
}

function resetForm() {
  Object.assign(form, emptyForm());
}

function addGrant() {
  form.grants.push({ subjectType: "DEPARTMENT", subjectId: "" });
}

function removeGrant(index) {
  form.grants.splice(index, 1);
}

async function saveDocument() {
  saving.value = true;
  try {
    const payload = { title: form.title, content: form.content, grants: form.grants };
    if (form.id) {
      await knowledgeApi.updateDocument(session.value.token, form.id, payload);
    } else {
      await knowledgeApi.createDocument(session.value.token, payload);
    }
    ElMessage.success(form.id ? "新版本已发布" : "文档已保存");
    resetForm();
    await loadDocuments();
  } catch (caught) {
    ElMessage.error(caught.message);
  } finally {
    saving.value = false;
  }
}

onMounted(loadDocuments);
</script>

<template>
  <div class="module-stack">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <strong>{{ form.id ? "发布文档新版本" : "新建文档" }}</strong>
          <el-button v-if="form.id" link type="primary" @click="resetForm">取消编辑</el-button>
        </div>
      </template>
      <el-form label-position="top" @submit.prevent="saveDocument">
        <el-form-item label="标题">
          <el-input v-model="form.title" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="form.content" type="textarea" :rows="7" />
        </el-form-item>
        <el-form-item label="授权">
          <div class="grant-list">
            <div v-for="(grant, index) in form.grants" :key="index" class="grant-row">
              <el-select v-model="grant.subjectType">
                <el-option label="用户" value="USER" />
                <el-option label="部门" value="DEPARTMENT" />
                <el-option label="角色" value="ROLE" />
              </el-select>
              <el-input v-model="grant.subjectId" placeholder="主体 ID" />
              <el-button :disabled="form.grants.length === 1" @click="removeGrant(index)">移除</el-button>
            </div>
            <el-button :icon="Plus" @click="addGrant">添加授权</el-button>
          </div>
        </el-form-item>
        <el-button type="primary" :loading="saving" :disabled="!form.title.trim() || !form.content.trim()" @click="saveDocument">
          {{ form.id ? "发布新版本" : "保存文档" }}
        </el-button>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <template #header><strong>可见文档</strong></template>
      <el-table v-loading="loading" :data="documents" empty-text="当前没有可见文档">
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column label="授权" min-width="180">
          <template #default="{ row }">
            <el-tag v-for="grant in row.grants" :key="grant.subjectType + grant.subjectId" class="grant-tag">
              {{ grant.subjectType }} · {{ grant.subjectId }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110">
          <template #default="{ row }">
            <el-button link type="primary" @click="editDocument(row)">更新</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>
