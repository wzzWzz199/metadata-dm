<template>
  <div>
    <ul class="modulelist clearfix">
      <li
        v-for="module in modules"
        :key="module.code"
        class="module"
        :class="[
          statusClass(module.status),
          { selected: module.code === selectedCode },
        ]"
        @click="$emit('select', module)"
      >
        <div>
          <div>
            <div class="content">
              <div class="detail">
                <span class="code">{{ module.code }}</span>
                <span class="name">{{ module.name }}</span>
              </div>
              <div class="btn" @click.stop="$emit('upgrade', module)">升级</div>
              <span class="status"></span>
            </div>
            <div class="log clearfix">
              <span class="version">{{ module.currentver }}</span>
              <span class="info">{{ latestInfo(module) }}</span>
            </div>
            <div class="progress">
              <div :style="{ width: progressWidth(module) }"></div>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import type { ModuleSummary } from '@/types/metadata';
import { formatTimestamp } from '@/utils/date';

defineProps<{ modules: ModuleSummary[]; selectedCode: string | null }>();

defineEmits<{
  (e: 'select', module: ModuleSummary): void;
  (e: 'upgrade', module: ModuleSummary): void;
}>();

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

function progressWidth(module: ModuleSummary) {
  if (module.status === 3) return '100%';
  const logs = module.logs || [];
  const latest = logs[logs.length - 1];
  if (!latest) return '0%';
  const progress = Number(latest.progress) || 0;
  return `${Math.min(progress, 100)}%`;
}

function latestInfo(module: ModuleSummary) {
  const logs = module.logs || [];
  const latest = logs[logs.length - 1];
  if (module.status === 1 || module.status === 2) {
    return latest?.msg || '';
  }
  if (module.datetime) {
    return formatTimestamp(module.datetime);
  }
  return '';
}
</script>
