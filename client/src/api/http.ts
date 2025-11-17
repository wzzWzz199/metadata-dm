import axios from 'axios';
import type { AxiosRequestConfig } from 'axios';
import { ElLoading, ElMessage } from 'element-plus';
import router from '@/router';
import { useSessionStore } from '@/stores/session';
import { pinia } from '@/stores';

const backendBase = import.meta.env.VITE_BACKEND_URL || '';

const http = axios.create({
  baseURL: backendBase,
  withCredentials: true,
  timeout: 60000,
});

let loadingCount = 0;
let loading: ReturnType<typeof ElLoading.service> | null = null;

function startLoading() {
  loadingCount += 1;
  if (!loading) {
    loading = ElLoading.service({ fullscreen: true, lock: true, text: '加载中...' });
  }
}

function endLoading() {
  loadingCount = Math.max(loadingCount - 1, 0);
  if (loadingCount === 0 && loading) {
    loading.close();
    loading = null;
  }
}

http.interceptors.request.use(
  (config) => {
    startLoading();
    return config;
  },
  (error) => {
    endLoading();
    return Promise.reject(error);
  },
);

http.interceptors.response.use(
  (response) => {
    endLoading();
    return response.data;
  },
  (error) => {
    endLoading();
    const status = error.response?.status;
    if (status === 401) {
      const session = useSessionStore(pinia);
      session.clear();
      router.push({ name: 'login' });
    } else {
      const message =
        error.response?.data?.messages?.[0]?.message || error.message || '请求失败';
      ElMessage.error(message);
    }
    return Promise.reject(error);
  },
);

export function request<T>(config: AxiosRequestConfig) {
  return http.request<T>(config);
}

export default http;
