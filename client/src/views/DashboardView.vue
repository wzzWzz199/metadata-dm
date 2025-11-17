<template>
  <div class="wrap clearfix" v-if="initialized">
    <div class="nav">
      <div class="topinfo">
        <div class="logo">海顿元数据服务</div>
      </div>
      <ProjectNav :projects="metadata.projects" v-model="selectedProject" />
    </div>
    <div class="pagecontent">
      <div class="current">
        <span class="name">{{ currentProject?.name || '' }}</span>
        <span class="info">{{ currentEnvironment?.name || '' }}</span>
        <div class="userinfo clearfix">
          <span id="usercode">{{ session.userCode }}</span>
          <span id="exit" class="exit" @click="handleLogout">
            <div class="iconfont">&#xe618;</div>
            <span>注销</span>
          </span>
        </div>
      </div>
      <EnvironmentTabs :environments="currentProject?.envs || []" v-model="selectedEnvironment" class="environmentnav" />
      <div class="contentwrap">
        <div class="btncontainer clearfix">
          <span id="exportbtn" :class="[{ selected: activeTab === 'export', hidetab: !canExport }]" @click="activateTab('export')">导出</span>
          <span id="upgradebtn" :class="{ selected: activeTab === 'upgrade' }" @click="activateTab('upgrade')">升级</span>
          <span id="advexportbtn" :class="[{ selected: activeTab === 'advanced', hidetab: !canExport }]" @click="activateTab('advanced')">高级导出</span>
        </div>
        <div id="exportcontainer" class="exportcontainer" :class="{ showexport: activeTab === 'export' && canExport }">
          <ExportPanel
            v-if="canExport"
            :tenants="metadata.exportTenants"
            :buttons="metadata.exportButtons"
            :result="metadata.exportResult"
            :selected-records="metadata.exportSelected"
            :model-value="selectedMetaType"
            :selected-tenant-id="selectedTenant"
            @update:model-value="handleMetaButtonChange"
            @update:selectedTenantId="handleTenantChange"
            @search="handleSearch"
            @add="metadata.addExportRecord"
            @remove="metadata.removeExportRecord"
            @export="handleExport"
            @app-export="handleAppExport"
            @page-change="handlePageChange"
          />
        </div>
        <div
          id="modulecontainer"
          class="modulecontainer"
          :class="{
            showmodule: activeTab === 'upgrade',
            showmoduledetail: showModuleDetail,
          }"
        >
          <div id="modulelist">
            <ModuleList
              :modules="metadata.modules"
              :selected-code="selectedModuleCode"
              @select="handleModuleSelect"
              @upgrade="handleModuleUpgrade"
            />
          </div>
          <div class="catalogue" id="moduledetail">
            <ModuleDetail
              v-if="metadata.moduleDetail"
              :detail="metadata.moduleDetail"
              :loading="metadata.moduleDetailLoading"
              @back="closeModuleDetail"
              @upgrade="handleDetailUpgrade"
            />
          </div>
        </div>
        <div id="advexportcontainer" class="advexportcontainer" :class="{ showadvexport: activeTab === 'advanced' && canExport }">
          <AdvancedExport v-if="canExport" @export="handleAdvExport" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import ProjectNav from '@/components/navigation/ProjectNav.vue';
import EnvironmentTabs from '@/components/navigation/EnvironmentTabs.vue';
import ModuleList from '@/components/modules/ModuleList.vue';
import ModuleDetail from '@/components/modules/ModuleDetail.vue';
import ExportPanel from '@/components/export/ExportPanel.vue';
import AdvancedExport from '@/components/export/AdvancedExport.vue';
import { useSessionStore } from '@/stores/session';
import { useMetadataStore } from '@/stores/metadata';
import api from '@/api/routes';
import type { ModuleDetailState, ModuleSummary } from '@/types/metadata';

const router = useRouter();
const session = useSessionStore();
const metadata = useMetadataStore();

const initialized = ref(false);
const activeTab = ref<'export' | 'upgrade' | 'advanced'>('upgrade');
const selectedModuleCode = ref<string | null>(null);
const selectedMetaType = ref<string | null>(null);
const selectedTenant = ref<string | null>(null);
let logTimer: number | null = null;

const selectedProject = computed({
  get: () => metadata.selectedProjectCode,
  set: (value: string) => metadata.setProject(value),
});

const selectedEnvironment = computed({
  get: () => metadata.selectedEnvironmentCode,
  set: (value: string) => metadata.setEnvironment(value),
});

const currentProject = computed(() => metadata.projects.find((p) => p.code === metadata.selectedProjectCode) || null);
const currentEnvironment = computed(() => currentProject.value?.envs?.find((env) => env.code === metadata.selectedEnvironmentCode) || null);
const canExport = computed(() => {
  const code = metadata.selectedEnvironmentCode?.toLowerCase();
  return code === 'dev' || code === 'test';
});
const showModuleDetail = computed(() => Boolean(metadata.moduleDetail));

async function bootstrap() {
  try {
    await api.getServerDate();
    await metadata.fetchProjects();
    await reloadEnvironmentData();
    initialized.value = true;
    logTimer = window.setInterval(() => metadata.fetchLogs(), 3000);
  } catch (error) {
    router.push({ name: 'login' });
  }
}

async function reloadEnvironmentData() {
  if (!metadata.selectedProjectCode || !metadata.selectedEnvironmentCode) return;
  try {
    selectedModuleCode.value = null;
    metadata.clearModuleDetail();
    await metadata.fetchModules();
    if (canExport.value) {
      await metadata.fetchExportMetadata();
      selectedTenant.value = metadata.exportSelectedTenant || metadata.exportTenants[0]?.tenantid || null;
    } else {
      selectedMetaType.value = null;
      selectedTenant.value = null;
      metadata.resetExportState();
    }
  } catch (error) {
    console.error(error);
  }
}

watch(
  () => [metadata.selectedProjectCode, metadata.selectedEnvironmentCode],
  () => {
    reloadEnvironmentData();
    if (!canExport.value && activeTab.value !== 'upgrade') {
      activeTab.value = 'upgrade';
    }
  },
);

watch(
  () => metadata.exportTenants,
  (tenants) => {
    if (tenants.length && !selectedTenant.value) {
      selectedTenant.value = tenants[0].tenantid;
      metadata.setSelectedTenant(tenants[0].tenantid);
    }
  },
  { immediate: true },
);

watch(
  () => metadata.exportSelectedTenant,
  (value) => {
    if (value) {
      selectedTenant.value = value;
    }
  },
);

onMounted(() => {
  bootstrap();
});

onBeforeUnmount(() => {
  if (logTimer) {
    clearInterval(logTimer);
    logTimer = null;
  }
});

function activateTab(tab: 'export' | 'upgrade' | 'advanced') {
  if ((tab === 'export' || tab === 'advanced') && !canExport.value) {
    return;
  }
  activeTab.value = tab;
}

async function handleLogout() {
  await api.logout();
  session.clear();
  router.push({ name: 'login' });
}

function handleModuleSelect(module: ModuleSummary) {
  selectedModuleCode.value = module.code;
  metadata.fetchModuleDetail(module);
}

function handleModuleUpgrade(module: ModuleSummary) {
  selectedModuleCode.value = module.code;
  metadata.fetchModuleDetail(module);
  activeTab.value = 'upgrade';
}

function closeModuleDetail() {
  metadata.clearModuleDetail();
}

async function handleDetailUpgrade(payload: {
  module: ModuleDetailState['module'];
  version: { ver: string } | null;
  productVersion: { ver: string } | null;
  fromProduct: boolean;
}) {
  if (!payload.version) {
    ElMessage.warning('请选择项目版本');
    return;
  }
  try {
    await metadata.upgradeModule(payload.module, {
      ver: payload.version.ver,
      prodver: payload.fromProduct ? payload.productVersion?.ver : undefined,
    });
  } catch (error) {
    console.error(error);
  }
}

function handleMetaButtonChange(value: string) {
  selectedMetaType.value = value;
  metadata.resetExportState();
  selectedTenant.value = metadata.exportSelectedTenant || metadata.exportTenants[0]?.tenantid || null;
}

function handleTenantChange(value: string) {
  selectedTenant.value = value;
  metadata.setSelectedTenant(value);
}

function handleSearch({ keyword }: { keyword: string }) {
  if (!selectedMetaType.value || !selectedTenant.value) {
    ElMessage.warning('请选择租户和类型');
    return;
  }
  metadata.searchMetadata({ metaType: selectedMetaType.value, tenantid: selectedTenant.value, keyword, page: 0 });
}

function handlePageChange(page: number) {
  if (!selectedMetaType.value || !selectedTenant.value) return;
  metadata.searchMetadata({ metaType: selectedMetaType.value, tenantid: selectedTenant.value, page });
}

function handleExport() {
  if (!selectedMetaType.value || !selectedTenant.value) {
    ElMessage.warning('请选择租户和类型');
    return;
  }
  metadata.exportMetadata(selectedMetaType.value, selectedTenant.value);
}

function handleAppExport() {
  selectedMetaType.value = 'ALL';
  metadata.loadAppExport();
}

function handleAdvExport(payload: { table: string; fields: string; where: string }) {
  metadata.advExport(payload.table, payload.fields, payload.where);
}
</script>
