<script setup>
import { reactive, ref } from "vue";
import { Lock, User } from "@element-plus/icons-vue";
import { knowledgeApi } from "../../api/knowledge";
import { useSession } from "./session";

const emit = defineEmits(["success"]);
const { save } = useSession();
const loading = ref(false);
const error = ref("");
const form = reactive({ username: "student", password: "student123" });

async function submit() {
  loading.value = true;
  error.value = "";
  try {
    const result = await knowledgeApi.login(form);
    save(result);
    emit("success");
  } catch (caught) {
    error.value = caught.message;
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <main class="login-page">
    <el-card class="login-card" shadow="never">
      <p class="eyebrow">权限约束知识问答</p>
      <h1>澄知</h1>
      <p class="intro">回答只来自当前账号可见的文档，并保留可核对的引用来源。</p>
      <el-form label-position="top" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password :prefix-icon="Lock" />
        </el-form-item>
        <el-alert v-if="error" :title="error" type="error" :closable="false" />
        <el-button type="primary" size="large" :loading="loading" @click="submit">登录</el-button>
      </el-form>
      <div class="account-list">
        <el-tag>student / student123</el-tag>
        <el-tag type="success">hr / hr123456</el-tag>
      </div>
    </el-card>
  </main>
</template>
