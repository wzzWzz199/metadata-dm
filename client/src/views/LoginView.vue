<template>
  <div class="login">
    <div class="login_title">
      <span>登录</span>
    </div>
    <div class="login_fields">
      <div class="login_fields__user">
        <div class="icon">
          <el-icon :size="22">
            <UserFilled />
          </el-icon>
        </div>
        <input v-model="form.usercode" placeholder="用户名" maxlength="16" type="text" autocomplete="off" />
      </div>
      <div class="login_fields__password">
        <div class="icon">
          <el-icon :size="22">
            <Lock />
          </el-icon>
        </div>
        <input v-model="form.password" placeholder="密码" maxlength="16" type="password" autocomplete="off" />
      </div>
      <div class="login_fields__submit">
        <input type="button" value="登录" :disabled="submitting" @click="handleSubmit" />
      </div>
    </div>
    <div class="success" v-if="successMessage">{{ successMessage }}</div>
    <div class="disclaimer">
      <p>欢迎登录海顿元数据管理系统</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Lock, UserFilled } from '@element-plus/icons-vue';
import api from '@/api/routes';
import { useSessionStore } from '@/stores/session';

const router = useRouter();
const session = useSessionStore();
const form = reactive({ usercode: session.userCode || '', password: '' });
const submitting = ref(false);
const successMessage = ref('');
const LOGIN_BODY_CLASS = 'login-page';

onMounted(() => {
  document.body.classList.add(LOGIN_BODY_CLASS);
});

onBeforeUnmount(() => {
  document.body.classList.remove(LOGIN_BODY_CLASS);
});

async function handleSubmit() {
  if (!form.usercode || !form.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }
  submitting.value = true;
  try {
    const response = await api.login({ usercode: form.usercode, password: form.password });
    if (response.status !== 3200 || !response.data) {
      ElMessage.error(response.messages?.[0]?.message || '登录失败');
      return;
    }
    successMessage.value = '登录成功';
    session.setUser(form.usercode);
    setTimeout(() => {
      router.push({ name: 'dashboard' });
    }, 1000);
  } finally {
    submitting.value = false;
  }
}
</script>

<style>
@import '@/assets/styles/login/default.css';
@import '@/assets/styles/login/demo.css';
@import '@/assets/styles/login/styles.css';
@import '@/assets/styles/login/loaders.css';
</style>
