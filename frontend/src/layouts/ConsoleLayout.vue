<script setup>
import { SwitchButton } from "@element-plus/icons-vue";
import { useSession } from "../modules/auth/session";

const emit = defineEmits(["logout"]);
const { session, clear } = useSession();

function logout() {
  clear();
  emit("logout");
}
</script>

<template>
  <el-container class="console-shell">
    <el-header class="console-header">
      <div>
        <p class="eyebrow">Chengzhi Console</p>
        <strong>澄知知识工作台</strong>
      </div>
      <div class="header-actions">
        <el-tag effect="plain">{{ session?.username }}</el-tag>
        <el-tag v-for="role in session?.roles || []" :key="role" type="info">{{ role }}</el-tag>
        <el-button :icon="SwitchButton" @click="logout">退出</el-button>
      </div>
    </el-header>
    <el-main>
      <slot />
    </el-main>
  </el-container>
</template>
