import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '@/views/LoginView.vue';
import DashboardView from '@/views/DashboardView.vue';
import { useSessionStore } from '@/stores/session';
import { pinia } from '@/stores';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/',
    },
  ],
});

router.beforeEach((to, _from, next) => {
  const session = useSessionStore(pinia);
  if (to.meta.requiresAuth && !session.isAuthenticated) {
    next({ name: 'login' });
    return;
  }
  if (to.name === 'login' && session.isAuthenticated) {
    next({ name: 'dashboard' });
    return;
  }
  next();
});

export default router;
