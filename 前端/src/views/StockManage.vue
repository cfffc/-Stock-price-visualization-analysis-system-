<template>
  <div style="display: flex">
    <el-upload :auto-upload="false" :limit="100" :on-change="add" :show-file-list="false" accept=".txt"
               action="#">
      <el-button style="flex-basis: fit-content; margin-right: 1rem" type="danger">添加日级股票数据</el-button>
    </el-upload>

    <el-input v-model="data.searchStockName" placeholder="输入股票名称进行搜索"
              style="width: 12rem; margin-right: 1rem; margin-bottom: 1rem; flex-basis: fit-content"
              @input="initData"/>
    <el-input v-model="data.searchTime" placeholder="输入创建时间进行搜索"
              style="width: 12rem; margin-bottom: 1rem; flex-basis: fit-content"
              @input="initData"/>
  </div>

  <el-table :cell-style="{'text-align':'center'}" :data="data.tableData" :header-cell-style="{'text-align':'center'}"
            border style="width: 100%;">
    <el-table-column label="id" prop="id" sortable/>
    <el-table-column label="股票编号" prop="stockId" sortable/>
    <el-table-column label="股票名称" prop="stockName" sortable/>
    <el-table-column label="创建时间" prop="createTime" sortable/>
    <el-table-column label="操作">
      <template #default="scope">
        <el-popconfirm title="确认导出该股票数据吗?" @confirm="exportData(scope.row)">
          <template #reference>
            <el-button size="small">导出数据</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm title="确认删除该股票数据吗?" @confirm="deleteStock(scope.row)">
          <template #reference>
            <el-button size="small" type="danger">删除股票</el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-table-column>
  </el-table>

  <el-pagination v-model:current-page="data.current" :page-size="data.pageSize" :total="data.total" background
                 layout="prev, pager, next" style="margin-top: 1rem; float: right" @current-change="initData"/>
</template>

<script>
import {onBeforeMount, reactive} from "vue";
import {deleteRequest, downloadFileToLocal, getRequest, message, postRequest} from '@/utils/api';

export default {
  setup() {
    const data = reactive({
      tableData: [],
      searchStockName: "",
      searchTime: "",
      current: 1,
      total: 100,
      pageSize: 20,
    });
    onBeforeMount(() => {
      initData();
      document.title = "日级股票数据管理";
    });

    function initData() {
      postRequest("/stockName/list", {
        pageNum: data.current,
        searchStockName: data.searchStockName.trim(),
        searchTime: data.searchTime.trim()
      }).then((res) => {
        if (res.data.code === 200) {
          data.tableData = res.data.data.stockNameList;
          data.total = res.data.data.total;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function add(file) {
      let formData = new FormData();
      formData.append("file", file.raw);
      postRequest("/stockData/add", formData).then((res) => {
        if (res.data.code === 200) {
          message(res.data.msg, "success");
          initData();
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function exportData(row) {
      getRequest("/stockName/exportData?stockId=" + row.stockId).then((res) => {
        if (res.data.code === 500) {
          message(res.data.msg, "error");
        } else {
          downloadFileToLocal(res.data, row.stockName + ".txt");
        }
      });
    }

    function deleteStock(row) {
      deleteRequest("/stockName/deleteStock?stockId=" + row.stockId).then((res) => {
        if (res.data.code === 200) {
          message(res.data.msg, "success");
          initData();
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    return {
      data,
      initData,
      add,
      exportData,
      deleteStock,
    };
  },
};
</script>

<style scoped>




</style>