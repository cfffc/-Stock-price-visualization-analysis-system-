<template>
  <div >
    <el-input v-model="data.searchUsername" placeholder="输入用户名进行搜索"
              style="width: 12rem; margin-right: 1rem; margin-bottom: 1rem"
              @input="initData"/>
    <el-input v-model="data.searchTime" placeholder="输入创建时间进行搜索" style="width: 12rem; margin-bottom: 1rem;"
              @input="initData"/>

    <el-table :cell-style="{'text-align':'center'}" :data="data.tableData"
              :header-cell-style="{'text-align':'center'}" border>
      <el-table-column label="id" prop="id" sortable/>
      <el-table-column label="用户名" prop="username" sortable/>
      <el-table-column label="密码" prop="password" sortable/>
      <el-table-column label="用户类型" prop="userType" sortable/>
      <el-table-column label="用户头像">
        <template #default="scope">
          <img :src="scope.row.avatarUrl" alt="" style="width: 3rem; height: auto;"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" sortable/>
    </el-table>

    <el-pagination v-model:current-page="data.current" :page-size="data.pageSize" :total="data.total" background
                   layout="prev, pager, next" style="margin-top: 1rem; float: right" @current-change="initData"/>
  </div>

</template>

<script>
import {onBeforeMount, reactive} from "vue";
import {message, postRequest} from '@/utils/api';

export default {
  setup() {
    const data = reactive({
      tableData: [],
      searchUsername: "",
      searchTime: "",
      current: 1,
      total: 100,
      pageSize: 20,
    });
    onBeforeMount(() => {
      initData();
      document.title = "用户管理";
    });

    function initData() {
      postRequest("/user/list", {
        pageNum: data.current,
        searchUsername: data.searchUsername.trim(),
        searchTime: data.searchTime.trim()
      }).then((res) => {
        if (res.data.code === 200) {
          data.tableData = res.data.data["userList"];
          data.total = res.data.data.total;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    return {
      data,
      initData,
    };
  },
};
</script>

<style scoped>




</style>