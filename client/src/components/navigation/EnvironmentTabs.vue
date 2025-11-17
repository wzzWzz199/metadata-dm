<template>
  <ul class="clusterlist clearfix">
    <li
      v-for="env in environments"
      :key="env.code"
      class="cluster"
      :class="[{ selected: env.code === modelValue }, statusClass(env.status)]"
      @click="$emit('update:modelValue', env.code)"
    >
      <div>
        <div class="detail">
          <span class="name">{{ env.name }}</span>
          <div class="proinfowrap clearfix">
            <span :title="env.appserver || '-'">应用服务器: {{ env.appserver || '-' }}</span>
            <span :title="env.dataserver || '-'">数据库: {{ env.dataserver || '-' }}</span>
          </div>
          <span class="status"></span>
        </div>
      </div>
    </li>
  </ul>
</template>

<script setup lang="ts">
import type { Environment } from '@/types/metadata';

defineProps<{ environments: Environment[]; modelValue: string }>();

defineEmits<{ (e: 'update:modelValue', value: string): void }>();

function statusClass(status?: number) {
  switch (status) {
    case 1:
      return 'upgradding';
    case 2:
      return 'upgraderror';
    case 3:
      return 'upgraded';
    default:
      return 'notupgraded';
  }
}
</script>
