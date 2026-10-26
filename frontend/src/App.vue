<script setup>
import { computed } from "vue";
import ConsoleLayout from "./layouts/ConsoleLayout.vue";
import LoginView from "./modules/auth/LoginView.vue";
import DocumentManager from "./modules/documents/DocumentManager.vue";
import QuestionPanel from "./modules/qa/QuestionPanel.vue";
import { useSession } from "./modules/auth/session";

const { session } = useSession();
const authenticated = computed(() => Boolean(session.value));
</script>

<template>
  <LoginView v-if="!authenticated" />
  <ConsoleLayout v-else>
    <el-row :gutter="16">
      <el-col :xs="24" :lg="14"><DocumentManager /></el-col>
      <el-col :xs="24" :lg="10"><QuestionPanel /></el-col>
    </el-row>
  </ConsoleLayout>
</template>
