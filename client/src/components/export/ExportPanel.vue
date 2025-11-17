<template>
  <div class="export" :class="{ selected: !!modelValue }">
    <div class="tenant">
      <el-select
        v-model="tenant"
        placeholder="请选择租户"
        size="small"
      >
        <el-option v-for="tenantItem in tenants" :key="tenantItem.tenantid" :label="tenantItem.tenantname" :value="tenantItem.tenantid" />
      </el-select>
    </div>
    <div class="btnwrap clearfix">
      <div>
        <button
          v-for="btn in buttons"
          :key="btn.type"
          :class="['btnitem', { selected: btn.type === modelValue }]"
          @click="$emit('update:modelValue', btn.type)"
        >
          {{ btn.name }}
        </button>
      </div>
      <div class="appbtn" @click="$emit('app-export')">应用导出</div>
    </div>
    <div class="exportmodule">
      <div v-if="modelValue" class="formwrap">
        <div class="searchbar">
          <el-input v-model="keyword" placeholder="请输入编码或名称" size="small" @keyup.enter="search" />
          <el-button size="small" type="primary" @click="search">查询</el-button>
          <el-button size="small" type="success" :disabled="!selectedRecords.length" @click="$emit('export')">
            导出
          </el-button>
        </div>
        <div class="tables">
          <div class="table original">
            <div class="title">查询结果</div>
            <el-empty v-if="!result?.voList?.length" description="暂无数据" />
            <table v-else>
              <thead>
                <tr>
                  <th>编码</th>
                  <th>名称</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in result?.voList" :key="record.metaDataCode">
                  <td>{{ record.metaDataCode }}</td>
                  <td>{{ record.metaDataName }}</td>
                  <td>
                    <el-button size="small" text type="primary" @click="$emit('add', record)">添加</el-button>
                  </td>
                </tr>
              </tbody>
            </table>
            <el-pagination
              v-if="result?.page?.totalPages > 1"
              :page-count="result.page.totalPages"
              layout="prev, pager, next"
              :current-page="result.page.pageNo || 1"
              @current-change="pageChange"
              small
            />
          </div>
          <div class="table linked">
            <div class="title">导出列表</div>
            <el-empty v-if="!selectedRecords.length" description="尚未选择" />
            <table v-else>
              <thead>
                <tr>
                  <th>编码</th>
                  <th>名称</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in selectedRecords" :key="record.metaDataCode">
                  <td>{{ record.metaDataCode }}</td>
                  <td>{{ record.metaDataName }}</td>
                  <td>
                    <el-button size="small" text type="danger" @click="$emit('remove', record.metaDataCode)">移除</el-button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div v-else class="placeholder">请选择一个元数据类型以开始导出</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import type { MetadataButton, MetadataRecord, MetadataSearchResult, Tenant } from '@/types/metadata';

const props = defineProps<{
  tenants: Tenant[];
  buttons: MetadataButton[];
  result: MetadataSearchResult | null;
  selectedRecords: MetadataRecord[];
  modelValue: string | null;
  selectedTenantId: string | null;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'update:selectedTenantId', value: string): void;
  (e: 'search', payload: { keyword: string }): void;
  (e: 'add', record: MetadataRecord): void;
  (e: 'remove', code: string): void;
  (e: 'export'): void;
  (e: 'page-change', page: number): void;
  (e: 'app-export'): void;
}>();

const keyword = ref('');
const tenant = ref(props.selectedTenantId || '');

watch(
  () => props.selectedTenantId,
  (value) => {
    tenant.value = value || '';
  },
);

watch(
  () => tenant.value,
  (value) => {
    if (value && value !== props.selectedTenantId) {
      emit('update:selectedTenantId', value);
    }
  },
);

function search() {
  emit('search', { keyword: keyword.value });
}

function pageChange(page: number) {
  emit('page-change', page - 1);
}
</script>

<style scoped>
.btnwrap .btnitem {
  border: none;
  background: transparent;
  padding: 8px 12px;
  cursor: pointer;
}
.btnwrap .btnitem.selected {
  color: #1b7fff;
}
.tables {
  display: flex;
  gap: 16px;
}
.table {
  flex: 1;
}
.table table {
  width: 100%;
  border-collapse: collapse;
}
.table th,
.table td {
  border-bottom: 1px solid #eee;
  padding: 8px;
}
.placeholder {
  text-align: center;
  padding: 40px 0;
  color: #999;
}
</style>
