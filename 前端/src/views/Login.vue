<template>
  <div class="page-background">
    <el-form :model="loginForm" class="login-container">
      <h3 class="login-title">系统登录</h3>
      <el-form-item>
        <el-input v-model="loginForm.username" placeholder="请输入用户名" size="large" type="text"></el-input>
      </el-form-item>

      <el-form-item>
        <el-input v-model="loginForm.password" placeholder="请输入密码" show-password size="large"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button size="large" style="width: 100%" type="danger" @click="login">登录</el-button>
      </el-form-item>

      <el-form-item>
        <el-link :underline="false" type="danger" @click="toRegister">注册</el-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {message, postRequest} from '@/utils/api'
import {onBeforeMount, reactive} from 'vue'
import router from '@/router';

export default {
  components: {},
  setup() {
    const loginForm = reactive({
      username: '',
      password: ''
    });
    onBeforeMount(() => {
      document.title = "登录";
    })

    function login() {
      if (loginForm.username === "" || loginForm.password === "") {
        message("请输入用户名与密码后登录", "info");
        return;
      }
      postRequest("/user/login", {
        username: loginForm.username,
        password: loginForm.password,
      }).then((res) => {
        if (res.data.code === 200) {
          message(res.data.msg, "success");
          const user = res.data.data;
          localStorage.setItem("username", user.username);
          localStorage.setItem("password", user.password);
          localStorage.setItem("avatarUrl", user.avatarUrl);
          localStorage.setItem("token", user.token);
          localStorage.setItem("userType", user.userType);
          router.push({
            path: "/home"
          });
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function toRegister() {
      router.push({
        path: "/register"
      });
    }

    return {
      loginForm,
      login,
      toRegister,
    }
  }
};
</script>

<style scoped>
.login-container {
  border-radius: 1rem;
  background-clip: padding-box;
  width: 24rem;
  padding: 1rem 1.5rem 0.5rem;
  background: #fff;
  border: 0.1rem solid #eeeaea;
  box-shadow: 0 0 1rem #cac6c6;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translateX(-50%) translateY(-80%);
}

.login-title {
  margin: 0 auto 1rem auto;
  text-align: center;
  color: #b31010;
  font-size: 1.5rem;
}

.page-background {
  background-image: url("../BG.jpg");
  background-size: cover; /* 背景图片覆盖整个元素 */
  background-position: center; /* 背景图片居中 */
  background-repeat: no-repeat; /* 不重复背景图片 */
  height: 100vh; /* 可以根据需要设置高度 */
  width: 100%; /* 可以根据需要设置宽度 */

}



</style>