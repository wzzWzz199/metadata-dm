import { defineStore } from 'pinia';

const USER_CODE_KEY = 'metadata-dm:usercode';

export const useSessionStore = defineStore('session', {
  state: () => ({
    userCode: localStorage.getItem(USER_CODE_KEY) || '',
    token: '',
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.userCode),
  },
  actions: {
    setUser(code: string) {
      this.userCode = code;
      localStorage.setItem(USER_CODE_KEY, code);
    },
    clear() {
      this.userCode = '';
      this.token = '';
      localStorage.removeItem(USER_CODE_KEY);
    },
  },
});
