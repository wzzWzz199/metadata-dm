import { describe, it, expect, beforeEach, vi } from 'vitest';

vi.mock('@/api/routes', () => {
  const upgradeModule = vi.fn().mockResolvedValue({ status: 3200, data: true, messages: [] });
  return {
    default: {
      upgradeModule,
      getProjects: vi.fn(),
      getModuleList: vi.fn(),
      getModuleListCache: vi.fn(),
      getVersionList: vi.fn(),
      getLogs: vi.fn(),
      getMetaAndTenants: vi.fn(),
      queryMetaData: vi.fn(),
      login: vi.fn(),
      logout: vi.fn(),
      getServerDate: vi.fn(),
    },
  };
});

import { setActivePinia, createPinia } from 'pinia';
import { useMetadataStore } from '@/stores/metadata';
import api from '@/api/routes';

describe('navigation flow', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  it('switches project/env and triggers upgrade', async () => {
    const store = useMetadataStore();
    store.projects = [
      {
        code: 'P1',
        name: '项目1',
        envs: [
          { code: 'dev', name: '开发' },
          { code: 'prod', name: '生产' },
        ],
      },
    ];
    store.setProject('P1');
    expect(store.selectedEnvironmentCode).toBe('dev');

    store.setEnvironment('prod');
    expect(store.selectedEnvironmentCode).toBe('prod');

    store.modules = [
      {
        code: 'M1',
        name: '模块1',
        status: 0,
        logs: [],
      },
    ];

    await store.upgradeModule(store.modules[0], { ver: '1.0.0' });
    expect((api as any).upgradeModule).toHaveBeenCalled();
    expect(store.modules[0].status).toBe(1);
  });
});
