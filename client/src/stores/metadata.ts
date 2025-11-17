import { defineStore } from 'pinia';
import type {
  Project,
  Environment,
  ModuleSummary,
  ModuleDetailState,
  MetadataButton,
  Tenant,
  MetadataSearchResult,
  MetadataRecord,
} from '@/types/metadata';
import api from '@/api/routes';
import { downloadWithForm } from '@/utils/download';
import { apiUrl } from '@/utils/url';

const STATUS_OK = 3200;

export const useMetadataStore = defineStore('metadata', {
  state: () => ({
    projects: [] as Project[],
    selectedProjectCode: '' as string,
    selectedEnvironmentCode: '' as string,
    modules: [] as ModuleSummary[],
    moduleDetail: null as ModuleDetailState | null,
    moduleDetailLoading: false,
    exportButtons: [] as MetadataButton[],
    exportTenants: [] as Tenant[],
    exportResult: null as MetadataSearchResult | null,
    exportSelected: [] as MetadataRecord[],
    exportSelectedTenant: '' as string,
    exportLoading: false,
    searchTerm: '',
    exportPage: 0,
    exportTotalPages: 0,
    lastLogTimestamp: 0,
  }),
  getters: {
    selectedProject(state): Project | null {
      return state.projects.find((project) => project.code === state.selectedProjectCode) || null;
    },
    environments(state): Environment[] {
      return state.selectedProject?.envs || [];
    },
    selectedEnvironment(state): Environment | null {
      return state.environments.find((env) => env.code === state.selectedEnvironmentCode) || null;
    },
    selectedModule(state): ModuleSummary | null {
      if (!state.moduleDetail) return null;
      return state.moduleDetail.module;
    },
  },
  actions: {
    async fetchProjects() {
      const response = await api.getProjects();
      if (response.status !== STATUS_OK) {
        throw new Error(response.messages?.[0]?.message || '加载项目失败');
      }
      this.projects = response.data;
      if (!this.selectedProjectCode && this.projects.length) {
        this.setProject(this.projects[0].code);
      }
    },
    setProject(code: string) {
      this.selectedProjectCode = code;
      const project = this.projects.find((p) => p.code === code);
      this.selectedEnvironmentCode = project?.envs?.[0]?.code || '';
      this.moduleDetail = null;
    },
    setEnvironment(code: string) {
      this.selectedEnvironmentCode = code;
      this.moduleDetail = null;
    },
    async fetchModules() {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const response = await api.getModuleList({
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
      });
      if (response.status !== STATUS_OK) {
        throw new Error(response.messages?.[0]?.message || '加载模块失败');
      }
      const modules = response.data || [];
      this.modules = modules.map((module) => ({
        ...module,
        logs: module.logs || [],
      }));
      this.lastLogTimestamp = this.getLatestLogTimestamp();
    },
    async refreshModulesFromCache() {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const response = await api.getModuleListCache({
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
      });
      if (response.status !== STATUS_OK) return;
      const incoming = response.data || [];
      const merged = this.modules.map((module) => {
        const updated = incoming.find((item) => item.code === module.code);
        if (!updated) return module;
        return {
          ...module,
          status: updated.status,
          datetime: updated.datetime,
        };
      });
      incoming.forEach((item) => {
        if (!merged.find((module) => module.code === item.code)) {
          merged.push({ ...item, logs: [] });
        }
      });
      this.modules = merged;
    },
    async fetchModuleDetail(module: ModuleSummary) {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      this.moduleDetailLoading = true;
      try {
        const response = await api.getVersionList({
          project: this.selectedProjectCode,
          env: this.selectedEnvironmentCode,
          modulecode: module.code,
        });
        if (response.status !== STATUS_OK) {
          throw new Error(response.messages?.[0]?.message || '加载模块详情失败');
        }
        this.moduleDetail = {
          module,
          projectPackages: response.data.proj || [],
          productPackages: response.data.prod || [],
        };
      } finally {
        this.moduleDetailLoading = false;
      }
    },
    clearModuleDetail() {
      this.moduleDetail = null;
    },
    async fetchLogs() {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const response = await api.getLogs({
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
        datetime: this.lastLogTimestamp,
      });
      if (response.status !== STATUS_OK) return;
      const logs = response.data || [];
      if (!logs.length) return;
      const updatedModules: ModuleSummary[] = this.modules.map((module) => {
        const entry = logs.find((item) => item.code === module.code);
        if (!entry || !entry.logs?.length) return module;
        const mergedLogs = [...(module.logs || []), ...entry.logs];
        const latestLog = mergedLogs[mergedLogs.length - 1];
        return {
          ...module,
          logs: mergedLogs,
          status: latestLog.status ?? module.status,
          datetime: latestLog.datetime || module.datetime,
        };
      });
      this.modules = updatedModules;
      const detailModule = this.moduleDetail?.module;
      if (detailModule) {
        const fresh = updatedModules.find((item) => item.code === detailModule.code);
        if (fresh && this.moduleDetail) {
          this.moduleDetail = { ...this.moduleDetail, module: fresh };
        }
      }
      this.lastLogTimestamp = this.getLatestLogTimestamp();
    },
    getLatestLogTimestamp() {
      return this.modules.reduce((max, module) => {
        const last = module.logs?.[module.logs.length - 1];
        return last ? Math.max(max, Number(last.datetime)) : max;
      }, 0);
    },
    async upgradeModule(module: ModuleSummary, payload: { ver: string; prodver?: string }) {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const response = await api.upgradeModule({
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
        modulecode: module.code,
        version: payload.ver,
        prover: payload.prodver || '',
      });
      if (response.status !== STATUS_OK) {
        throw new Error(response.messages?.[0]?.message || '升级失败');
      }
      this.modules = this.modules.map((item) =>
        item.code === module.code
          ? { ...item, status: 1, logs: [], datetime: Date.now() }
          : item,
      );
      if (this.moduleDetail?.module.code === module.code) {
        this.moduleDetail = {
          ...this.moduleDetail,
          module: { ...this.moduleDetail.module, status: 1, logs: [] },
        };
      }
    },
    async fetchExportMetadata() {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const response = await api.getMetaAndTenants({
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
      });
      if (response.status !== STATUS_OK) {
        throw new Error(response.messages?.[0]?.message || '加载导出配置失败');
      }
      this.exportButtons = response.data.meta || [];
      this.exportTenants = response.data.tenants || [];
      this.exportSelectedTenant = this.exportTenants[0]?.tenantid || '';
    },
    setSelectedTenant(value: string) {
      this.exportSelectedTenant = value;
    },
    setExportSelection(records: MetadataRecord[]) {
      this.exportSelected = records;
    },
    addExportRecord(record: MetadataRecord) {
      if (this.exportSelected.find((item) => item.metaDataCode === record.metaDataCode)) {
        return;
      }
      this.exportSelected = [...this.exportSelected, record];
    },
    removeExportRecord(code: string) {
      this.exportSelected = this.exportSelected.filter((item) => item.metaDataCode !== code);
    },
    resetExportState() {
      this.exportResult = null;
      this.exportSelected = [];
      this.exportSelectedTenant = this.exportTenants[0]?.tenantid || '';
    },
    async searchMetadata(params: { metaType: string; tenantid: string; keyword?: string; page?: number }) {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      this.exportLoading = true;
      try {
        const response = await api.queryMetaData({
          project: this.selectedProjectCode,
          env: this.selectedEnvironmentCode,
          metaType: params.metaType,
          tenantid: params.tenantid,
          metaDataCode: params.keyword || '',
          page: params.page ?? 0,
          rows: 12,
        });
        if (response.status !== STATUS_OK) {
          throw new Error(response.messages?.[0]?.message || '查询失败');
        }
        this.exportResult = response.data;
        this.exportPage = (response.data.page?.pageNo || 1) - 1;
        this.exportTotalPages = response.data.page?.totalPages || 0;
      } finally {
        this.exportLoading = false;
      }
    },
    async loadAppExport() {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const response = await api.getModuleList({
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
      });
      if (response.status !== STATUS_OK) {
        throw new Error(response.messages?.[0]?.message || '加载模块失败');
      }
      const moduledata = (response.data || []).map((item) => ({
        metaDataCode: item.code,
        metaDataName: item.name,
        metaDataType: 'ALL',
        isappexport: 1,
      }));
      this.exportResult = {
        voList: moduledata,
        page: { pageNo: 1, totalPages: 1 },
      };
    },
    async exportMetadata(metaType: string, tenantid: string) {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      const payload = {
        project: this.selectedProjectCode,
        env: this.selectedEnvironmentCode,
        metaType,
        tenantid,
      };
      downloadWithForm(apiUrl('/export/exportMetaData'), {
        project: payload.project,
        env: payload.env,
        metaType: payload.metaType,
        tenantid: payload.tenantid,
        data: JSON.stringify(this.exportSelected),
      });
    },
    async advExport(table: string, fields: string, where: string) {
      if (!this.selectedProjectCode || !this.selectedEnvironmentCode) return;
      downloadWithForm(
        apiUrl('/export/exportFieldMetaData'),
        {
          project: this.selectedProjectCode,
          env: this.selectedEnvironmentCode,
          table,
          fields,
          where,
        },
        'GET',
      );
    },
  },
});
