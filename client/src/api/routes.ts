import { request } from './http';
import type {
  ApiResponse,
  Project,
  ModuleSummary,
  ModuleDetailPayload,
  ModuleLogResponse,
  MetaAndTenantsResponse,
  MetadataSearchResult,
} from '@/types/metadata';

const API_BASE = import.meta.env.VITE_API_BASE || '/metadata';

function withApi(path: string) {
  if (path.startsWith('/')) {
    return `${API_BASE}${path}`;
  }
  return `${API_BASE}/${path}`;
}

export default {
  login(payload: { usercode: string; password: string }) {
    return request<ApiResponse<boolean>>({
      url: '/login',
      method: 'POST',
      data: payload,
    });
  },
  logout() {
    return request<ApiResponse<boolean>>({
      url: '/logout',
      method: 'GET',
    });
  },
  getServerDate() {
    return request<ApiResponse<string>>({
      url: withApi('/COMMON/getServerDate'),
      method: 'GET',
    });
  },
  getProjects() {
    return request<ApiResponse<Project[]>>({
      url: withApi('/COMMON/getProjects'),
      method: 'GET',
    });
  },
  getModuleList(params: { project: string; env: string }) {
    return request<ApiResponse<ModuleSummary[]>>({
      url: withApi('/UPGRADE/getModuleList'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
  getModuleListCache(params: { project: string; env: string }) {
    return request<ApiResponse<ModuleSummary[]>>({
      url: withApi('/UPGRADE/getModuleListCache'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
  getVersionList(params: { project: string; env: string; modulecode: string }) {
    return request<ApiResponse<{ proj: ModuleDetailPayload['proj']; prod: ModuleDetailPayload['prod'] }>>({
      url: withApi('/UPGRADE/getVersionList'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
  getLogs(params: { project: string; env: string; datetime?: number }) {
    return request<ApiResponse<ModuleLogResponse[]>>({
      url: withApi('/UPGRADE/getLogs'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
  upgradeModule(params: { project: string; env: string; modulecode: string; version: string; prover?: string }) {
    return request<ApiResponse<boolean>>({
      url: withApi('/UPGRADE/upgrade'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
  getMetaAndTenants(params: { project: string; env: string }) {
    return request<ApiResponse<MetaAndTenantsResponse>>({
      url: withApi('/export/getMetaAndTenants'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
  queryMetaData(params: {
    project: string;
    env: string;
    metaType: string;
    tenantid: string;
    metaDataCode?: string;
    page?: number;
    rows?: number;
  }) {
    return request<ApiResponse<MetadataSearchResult>>({
      url: withApi('/export/queryMetaData'),
      method: 'GET',
      params: { ...params, t: Math.random() },
    });
  },
};
