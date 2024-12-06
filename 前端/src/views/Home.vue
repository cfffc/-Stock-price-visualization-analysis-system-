<template>
  <div class="page-background" style="display: flex; height: 100vh; width: 100vw">
    <div class="left-nav-container">
      <div class="title">股票预测</div>

      <div class="left-nav">
        <el-menu v-if="data.userType === 'normal'" :collapse="false" :default-active="data.curMenu"
                 :unique-opened="true"
                 active-text-color="#d31010" background-color="#dcdcdc"
                 router style="border: none" text-color="#3A3939FF">
          <el-menu-item index="/index">
            <template #title>
              <el-icon>
                <HomeFilled/>
              </el-icon>
              系统首页
            </template>
          </el-menu-item>
          <el-menu-item index="/analyse">
            <template #title>
              <el-icon>
                <Histogram/>
              </el-icon>
              影响力分析
            </template>
          </el-menu-item>
          <el-menu-item index="/randomForest">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              随机森林
            </template>
          </el-menu-item>
          <el-menu-item index="/LSTM">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              LSTM
            </template>
          </el-menu-item>
          <el-menu-item index="/KNN">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              KNN
            </template>
          </el-menu-item>
          <el-menu-item index="/leastSquares">
            <template #title>
              <el-icon>
                <Tools/>
              </el-icon>
              线性回归
            </template>
          </el-menu-item>
        </el-menu>

        <el-menu v-if="data.userType === 'admin'" :collapse="false" :default-active="data.curMenu" :unique-opened="true"
                 active-text-color="#d31010" background-color="#dcdcdc"
                 router style="border: none" text-color="#3A3939FF">
          <el-menu-item index="/index">
            <template #title>
              <el-icon>
                <HomeFilled/>
              </el-icon>
              系统首页
            </template>
          </el-menu-item>
          <el-menu-item index="/analyse">
            <template #title>
              <el-icon>
                <Histogram/>
              </el-icon>
              影响力分析
            </template>
          </el-menu-item>
          <el-menu-item index="/randomForest">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              随机森林
            </template>
          </el-menu-item>
          <el-menu-item index="/LSTM">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              LSTM
            </template>
          </el-menu-item>
          <el-menu-item index="/KNN">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              KNN
            </template>
          </el-menu-item>
          <el-menu-item index="/leastSquares">
            <template #title>
              <el-icon>
                <Tools/>
              </el-icon>
              线性回归
            </template>
          </el-menu-item>
          <el-menu-item index="/stockManage">
            <template #title>
              <el-icon>
                <Management/>
              </el-icon>
              日级股票管理
            </template>
          </el-menu-item>
          <el-menu-item index="/stockMinuteManage">
            <template #title>
              <el-icon>
                <Management/>
              </el-icon>
              分钟级股票管理
            </template>
          </el-menu-item>
          <el-menu-item index="/userManage">
            <template #title>
              <el-icon>
                <User/>
              </el-icon>
              用户管理
            </template>
          </el-menu-item>
          <el-menu-item index="/LSTMManage">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              LSTM模型参数
            </template>
          </el-menu-item>
          <el-menu-item index="/randomForestManage">
            <template #title>
              <el-icon>
                <Promotion/>
              </el-icon>
              随机森林模型参数
            </template>
          </el-menu-item>
        </el-menu>
      </div>
    </div>

    <div class="main-content">
      <div class="header">
        <el-dropdown v-if="data.avatarUrl" class="avatar-dropdown">
          <img :src="data.avatarUrl" alt="" class="avatar"/>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="data.editAvatarUrlDialogVisible = true">更换头像</el-dropdown-item>
              <el-dropdown-item>
                <el-popconfirm title="确定退出登录吗?" @confirm="logout">
                  <template #reference>
                    退出登录
                  </template>
                </el-popconfirm>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div class="router-container">
        <router-view/>
      </div>
    </div>
  </div>

  <el-dialog v-model="data.editAvatarUrlDialogVisible" title="修改头像" width="60%">
    <el-form-item label="图片URL路径">
      <el-input v-model="data.editAvatarUrl" placeholder="图片路径(网络可访问到的)"/>
    </el-form-item>
    <template #footer>
      <span class="dialog-footer">
          <el-button @click="data.editAvatarUrlDialogVisible = false">Cancel</el-button>
          <el-button type="danger" @click="editAvatarUrl">Confirm</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import {onBeforeMount, onMounted, reactive} from 'vue';
import {useRouter} from 'vue-router';
import {getRequest, message, putRequest} from '@/utils/api';

export default {
  setup() {
    const data = reactive({
      curMenu: "",
      userType: "normal",
      avatarUrl: "",
      editAvatarUrlDialogVisible: false,
      editAvatarUrl: "",
    });
    const myRouter = useRouter();
    onBeforeMount(() => {
      data.curMenu = myRouter.currentRoute.value.path;
      data.avatarUrl = localStorage.getItem("avatarUrl") ? localStorage.getItem("avatarUrl") : "";
      data.userType = localStorage.getItem("userType") ? localStorage.getItem("userType") : "";
    })

    function logout() {
      getRequest("/user/logout?username=" + localStorage.getItem("username")).then((res) => {
        if (res.data.code === 200) {
          message(res.data.msg, "success");
          localStorage.removeItem("username");
          localStorage.removeItem("password");
          localStorage.removeItem("avatarUrl");
          localStorage.removeItem("token");
          localStorage.removeItem("userType");
          toPath("/login");
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function toPath(path) {
      myRouter.push({
        path: path
      })
    }

    function editAvatarUrl() {
      putRequest("/user/editAvatarUrl", {
        username: localStorage.getItem("username"),
        editAvatarUrl: data.editAvatarUrl
      }).then((res) => {
        if (res.data.code === 200) {
          message(res.data.msg, "success");
          data.avatarUrl = data.editAvatarUrl;
          localStorage.setItem("avatarUrl", data.avatarUrl);
          data.editAvatarUrlDialogVisible = false;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    onMounted(() => {
      resize();
      window.addEventListener("resize", resize);
    });

    function resize() {
      const defaultWidth = 1500;
      const currentWidth = document.documentElement.clientWidth;
      document.documentElement.style.fontSize = (16 * Number(window.devicePixelRatio.toFixed(2)) * currentWidth / defaultWidth) + "px";
    }

    return {
      data,
      logout,
      editAvatarUrl,
    };
  },
};
</script>

<style lang="scss" scoped>
.left-nav-container {
  width: 15rem;
  height: 100%;
  background-color: #dcdcdc;
  text-align: center;
  display: flex;
  flex-direction: column;

  .title {
    font-size: 1.2rem;
    color: white;
    width: 100%;
    height: 3.5rem;
    line-height: 3.5rem;
    background-color: #d31010;
    cursor: pointer;
  }

  .left-nav {
    flex: 1;
    overflow-x: hidden;
    overflow-y: auto;

    &::-webkit-scrollbar {
      width: 0;
      background-color: #3d3d3d;
    }

    &::-webkit-scrollbar-thumb {
      background-color: #b1b1b1;
    }

    &:hover::-webkit-scrollbar {
      width: 0.2rem;
    }
  }
}

.main-content {
  display: flex;
  flex-direction: column;
  flex: 1;

  .header {
    width: 100%;
    height: 3.5rem;
    background-color: #d31010;
    position: relative;

    .avatar-dropdown {
      position: absolute;
      right: 2rem;
      top: 0.25rem;
    }

    .avatar {
      width: 3rem;
      height: 3rem;
      border-radius: 50%;
      outline: none;
      cursor: pointer;
    }
  }

  .router-container {
    flex: 1;
    width: 100%;
    padding: 1rem;
    overflow-y: auto;
    overflow-x: hidden;

    &::-webkit-scrollbar {
      width: 0.5rem;
      background-color: #ffffff;
      border-radius: 0.5rem;
    }

    &::-webkit-scrollbar-thumb {
      background-color: #b1b1b1;
      border-radius: 0.5rem;
    }
  }
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