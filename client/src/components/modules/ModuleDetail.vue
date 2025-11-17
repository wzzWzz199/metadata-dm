<template>
  <div v-if="detail" class="moduledetail" :class="statusClass(detail.module.status)">
    <div class="container clearfix">
      <div class="libcontent clearfix">
        <span class="code">{{ detail.module.code }}</span>
        <span class="info">{{ detail.module.name }}</span>
      </div>
      <div class="versioninfo">
        <div class="clearfix">
          <span>版本号：</span>
          <span>{{ detail.module.currentver || '-' }}</span>
        </div>
        <span>更新日期：{{ detail.module.datetime ? formatTimestamp(detail.module.datetime) : '-' }}</span>
      </div>
      <div class="btn" @click="handleUpgrade">升级</div>
      <div class="backbtn" title="返回" @click="$emit('back')">
        <span class="iconfont icon-houtui"></span>
      </div>
      <div class="progresswrap clearfix">
        <div class="progressvalue">{{ progressText }}</div>
        <div class="progress">
          <div class="progressbar" :style="{ width: progressWidth }"></div>
        </div>
      </div>
    </div>
    <div class="upgradelog" :class="{ loading }">
      <div class="container clearfix">
        <div class="packwrap" :class="{ showprod: upgradeFromProd }">
          <div class="packagelist">
            <h4>项目版本</h4>
            <ul class="packageul clearfix">
              <li
                v-for="pack in detail.projectPackages"
                :key="pack.ver"
                class="package"
                :class="{ selected: pack.ver === selectedPackage }"
                @click="selectedPackage = pack.ver"
              >
                {{ pack.ver }}
              </li>
            </ul>
          </div>
          <div class="prodwrap" :class="{ hideprodwrap: !detail.productPackages.length }">
            <div class="checkwrap" @click="toggleUpgradeFromProd">
              <span class="checkbtn" :class="{ checked: upgradeFromProd }"></span>
              <span>从产品版本升级</span>
            </div>
            <div class="prodlist">
              <ul class="packageul clearfix">
                <li
                  v-for="pack in detail.productPackages"
                  :key="pack.ver"
                  class="package"
                  :class="{ selected: pack.ver === selectedProdPackage }"
                  @click="selectedProdPackage = pack.ver"
                >
                  {{ pack.ver }}
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div class="directory">
          <div class="title">补丁</div>
          <div class="patchlist">
            <div v-if="patches.length === 0" class="empty">暂无补丁</div>
            <ul v-else>
              <li v-for="patch in patches" :key="patch.code">
                <span class="code">{{ patch.code }}</span>
                <span class="name">{{ patch.name }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="log">
        <div v-for="log in detail.module.logs || []" :key="log.datetime" class="logline">
          <span>{{ formatTimestamp(log.datetime, { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }) }}</span>
          <span>{{ log.msg }}</span>
        </div>
      </div>
      <div class="loading" v-if="loading">
        <div><i></i><span>加载中...</span></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { ModuleDetailState, PackageVersion, PatchInfo } from '@/types/metadata';
import { formatTimestamp } from '@/utils/date';

const props = defineProps<{ detail: ModuleDetailState | null; loading: boolean }>();
const emit = defineEmits<{
  (e: 'back'): void;
  (e: 'upgrade', payload: { module: ModuleDetailState['module']; version: PackageVersion | null; productVersion: PackageVersion | null; fromProduct: boolean }): void;
}>();

const upgradeFromProd = ref(false);
const selectedPackage = ref<string>('');
const selectedProdPackage = ref<string>('');

watch(
  () => props.detail,
  (detail) => {
    if (detail) {
      selectedPackage.value = detail.projectPackages[0]?.ver || '';
      selectedProdPackage.value = detail.productPackages[0]?.ver || '';
      upgradeFromProd.value = false;
    }
  },
  { immediate: true },
);

const activePackage = computed(() => {
  if (!props.detail) return null;
  return props.detail.projectPackages.find((pack) => pack.ver === selectedPackage.value) || null;
});

const activeProductPackage = computed(() => {
  if (!props.detail) return null;
  return props.detail.productPackages.find((pack) => pack.ver === selectedProdPackage.value) || null;
});

const patches = computed<PatchInfo[]>(() => activePackage.value?.patches || []);

const progress = computed(() => {
  if (props.detail?.module.status === 3) {
    return 100;
  }
  const logs = props.detail?.module.logs || [];
  const latest = logs[logs.length - 1];
  return latest ? Math.min(Number(latest.progress) || 0, 100) : 0;
});

const progressWidth = computed(() => `${progress.value}%`);
const progressText = computed(() => `${progress.value.toFixed(1)}%`);

function toggleUpgradeFromProd() {
  upgradeFromProd.value = !upgradeFromProd.value;
}

function handleUpgrade() {
  if (!props.detail) return;
  if (props.loading) return;
  emit('upgrade', {
    module: props.detail.module,
    version: activePackage.value,
    productVersion: activeProductPackage.value,
    fromProduct: upgradeFromProd.value,
  });
}

function statusClass(status?: number) {
  switch (status) {
    case 1:
      return 'upgradding showlog';
    case 2:
      return 'upgraderror showlog';
    case 3:
      return 'upgraded';
    default:
      return 'notupgraded';
  }
}
</script>

<style scoped>
.patchlist .empty {
  padding: 12px;
  color: #999;
}
.logline {
  display: flex;
  gap: 8px;
  line-height: 1.6;
}
</style>
