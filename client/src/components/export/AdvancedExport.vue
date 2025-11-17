<template>
  <div class="advcontent clearfix">
    <div class="textwrap">
      <div>
        <el-input v-model="table" placeholder="请输入表" />
      </div>
    </div>
    <div class="textwrap">
      <div>
        <el-input v-model="fields" placeholder="请输入查询字段" />
      </div>
    </div>
    <div class="textwrap">
      <div>
        <el-input v-model="where" placeholder="请输入where条件" />
      </div>
    </div>
    <div class="exportbtn" @click="submit">导出</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const emit = defineEmits<{
  (e: 'export', payload: { table: string; fields: string; where: string }): void;
}>();

const table = ref('');
const fields = ref('');
const where = ref('');

function submit() {
  emit('export', { table: table.value, fields: fields.value, where: where.value });
}
</script>
