<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { knowledgeApi } from "../../api/knowledge";
import { useSession } from "../auth/session";

const { session } = useSession();
const question = ref("");
const loading = ref(false);
const answer = ref(null);

async function ask() {
  loading.value = true;
  try {
    answer.value = await knowledgeApi.ask(session.value.token, question.value);
  } catch (caught) {
    ElMessage.error(caught.message);
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <el-card class="question-panel" shadow="never">
    <template #header><strong>提问</strong></template>
    <el-input v-model="question" type="textarea" :rows="5" placeholder="例如：进入实验室前必须做什么？" />
    <div class="ask-action">
      <el-button type="primary" :loading="loading" :disabled="!question.trim()" @click="ask">获取带来源的回答</el-button>
    </div>

    <el-empty v-if="!answer" description="回答和引用会显示在这里" />
    <template v-else>
      <el-alert :title="answer.refused ? '证据不足，已拒绝回答' : '回答已通过引用校验'" :type="answer.refused ? 'warning' : 'success'" :closable="false" />
      <p class="answer-text">{{ answer.answer }}</p>
      <el-collapse v-if="answer.evidence.length">
        <el-collapse-item v-for="item in answer.evidence" :key="item.citationId" :title="item.title + ' · 片段 ' + (item.chunkIndex + 1)" :name="item.citationId">
          <p>{{ item.content }}</p>
          <el-text type="info">引用 {{ item.citationId }}</el-text>
        </el-collapse-item>
      </el-collapse>
    </template>
  </el-card>
</template>
