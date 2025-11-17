export interface ApiMessage {
  message: string;
}

export interface ApiResponse<T> {
  status: number;
  messages: ApiMessage[];
  data: T;
}

export interface Environment {
  code: string;
  name: string;
  appserver?: string;
  dataserver?: string;
  status?: number;
}

export interface Project {
  code: string;
  name: string;
  status?: number;
  envs: Environment[];
}

export interface ModuleLogEntry {
  datetime: number;
  msg: string;
  progress: string | number;
  status: number;
}

export interface ModuleSummary {
  code: string;
  name: string;
  status: number;
  currentver?: string;
  datetime?: number;
  logs?: ModuleLogEntry[];
}

export interface PackageVersion {
  ver: string;
  patches?: PatchInfo[];
  desc?: string;
}

export interface PatchInfo {
  code: string;
  name: string;
  version?: string;
  checked?: boolean;
}

export interface ModuleDetailPayload {
  proj: PackageVersion[];
  prod: PackageVersion[];
}

export interface ModuleDetailState {
  module: ModuleSummary;
  projectPackages: PackageVersion[];
  productPackages: PackageVersion[];
}

export interface ModuleLogResponse {
  code: string;
  logs: ModuleLogEntry[];
}

export interface Tenant {
  tenantid: string;
  tenantname: string;
}

export interface MetadataButton {
  type: string;
  name: string;
  linkedType?: string[];
  listItemName?: string;
  hidesearch?: number;
}

export interface MetadataRecord {
  metaDataCode: string;
  metaDataName: string;
  metaDataType?: string;
  [key: string]: any;
}

export interface MetadataSearchResult {
  voList: MetadataRecord[];
  page: {
    pageNo: number;
    totalPages: number;
  };
}

export interface MetaAndTenantsResponse {
  meta: MetadataButton[];
  tenants: Tenant[];
}
