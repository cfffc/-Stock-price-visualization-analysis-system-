<template>
  <div class="page-background">
    <el-form :model="registerForm" class="register-container">
      <h3 class="register-title">系统注册</h3>
      <el-form-item>
        <el-input v-model="registerForm.username" placeholder="请输入用户名" size="large" type="text"
                  @blur="verifyUsername"></el-input>
      </el-form-item>

      <el-form-item>
        <el-input v-model="registerForm.password" placeholder="请输入密码" show-password size="large"
                  @blur="verifyPassword"></el-input>
      </el-form-item>

      <el-form-item>
        <el-input v-model="registerForm.repeatPassword" placeholder="请再次输入密码" show-password
                  size="large" @blur="verifyRepeatPassword"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button size="large" style="width: 100%" type="danger" @click="register">注册</el-button>
      </el-form-item>

      <el-form-item>
        <el-link :underline="false" size="large" type="danger" @click="toLogin">登录</el-link>
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
    const registerForm = reactive({
      username: '',
      password: '',
      repeatPassword: '',
    });
    onBeforeMount(() => {
      document.title = "注册";
    })

    function register() {
      if (verifyRepeatPassword()) {
        if (verifyPassword() && verifyUsername()) {
          postRequest('/user/register', {
            username: registerForm.username,
            password: registerForm.password
          }).then(res => {
            if (res.data.code === 200) {
              router.push({
                path: "/login"
              });
              message(res.data.msg, 'success');
            } else {
              message(res.data.msg, 'error');
            }
          })
        } else {
          message('密码与用户名要求6-10位,由字母数字下划线组成,请重新输入', 'error')
        }
      } else {
        message('两次密码不一致，请重新输入', 'error');
      }
    }

    let reg = /^\w{6,10}$/

    function verifyPassword() {
      if (registerForm.password.match(reg)) {
        return true
      } else {
        message('密码要求6-10位,由字母数字下划线组成,请重新输入', 'error')
        return false
      }
    }

    function verifyRepeatPassword() {
      if (registerForm.repeatPassword && registerForm.password === registerForm.repeatPassword) {
        return true
      } else {
        message('两次密码需要一致且不为空', 'error')
        return false
      }
    }

    function verifyUsername() {
      if (registerForm.username.match(reg)) {
        return true
      } else {
        message('用户名要求6-10位,由字母数字下划线组成,请重新输入', 'error')
        return false
      }
    }

    function toLogin() {
      router.push({
        path: "/login"
      });
    }

    return {
      registerForm,
      register,
      toLogin,
      verifyPassword,
      verifyRepeatPassword,
      verifyUsername
    }
  }
}
</script>

<style scoped>
.register-container {
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

.register-title {
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