<template>
  <ul class="projectlist clearfix">
    <li
      v-for="project in projects"
      :key="project.code"
      class="project"
      :class="[{ selected: project.code === modelValue }, statusClass(project.status)]"
      @click="$emit('update:modelValue', project.code)"
    >
      <div class="detail">
        <span class="code">{{ project.code }}&nbsp;-&nbsp;</span>
        <span class="name">{{ project.name }}</span>
        <span class="status"></span>
      </div>
    </li>
  </ul>
</template>

<script setup lang="ts">
import type { Project } from '@/types/metadata';

defineProps<{ projects: Project[]; modelValue: string }>();

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
