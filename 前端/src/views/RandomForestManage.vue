<template>
  <div >
    <el-select v-model="data.stockId" filterable placeholder="股票名称" style="margin-right: 1rem; width: 10rem">
      <el-option
          v-for="item in data.stockNameList"
          :key="item.stockId"
          :label="item.stockName"
          :value="item.stockId"
      />
    </el-select>

    <el-input v-model="data.estimatorsCount" placeholder="estimatorsCount" style="width: 10rem; margin-right: 1rem;"/>
    <el-input v-model="data.randomStates" placeholder="randomStates" style="width: 10rem; margin-right: 1rem;"/>
    <el-input v-model="data.nJobs" placeholder="nJobs" style="width: 10rem; margin-right: 1rem;"/>

    <el-button type="danger" @click="predict">预测</el-button>
  </div>

  <el-table id="img-container" :cell-style="{'text-align':'center'}" :data="data.predictDataList"
            :header-cell-style="{'text-align':'center'}"
            border style="margin-top: 1rem;">
    <el-table-column label="ID" prop="id"/>
    <el-table-column label="estimatorsCount" prop="estimatorsCount" width="150"/>
    <el-table-column label="randomStates" prop="randomStates" width="140"/>
    <el-table-column label="nJobs" prop="njobs"/>
    <el-table-column label="平均绝对误差" prop="meanAbsoluteError"/>
    <el-table-column label="均方误差" prop="meanSquaredError"/>
    <el-table-column label="均方根误差" prop="rootMeanSquaredError"/>
    <el-table-column label="使用状态">
      <template #default="scope">
        <el-tag v-if="scope.row.useState === false" type="info">测试</el-tag>
        <el-tag v-if="scope.row.useState === true" type="success">使用中</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="预测时间" prop="createTime"/>
    <el-table-column label="操作">
      <template #default="scope">
        <el-popconfirm title="确认使用该参数吗?" @confirm="useArgument(scope.row)">
          <template #reference>
            <el-button size="small">使用参数</el-button>
          </template>
        </el-popconfirm>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import {onBeforeMount, reactive} from "vue";
import {getRequest, message, postRequest} from "@/utils/api";

export default {
  setup() {
    const data = reactive({
      estimatorsCount: "",
      randomStates: "",
      nJobs: "",
      stockId: "",
      stockNameList: [],
      predictDataList: []
    });
    onBeforeMount(() => {
      getRequest("/stockName/listAll").then((res) => {
        if (res.data.code === 200) {
          data.stockNameList = res.data.data;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
      initData();
      document.title = "随机森林参数管理";
    });

    function initData() {
      getRequest("/randomForestArgument/list").then((res) => {
        if (res.data.code === 200) {
          data.predictDataList = res.data.data;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function predict() {
      if (data.stockId === "") {
        message("请选择需要预测的股票", "info");
        return;
      }
      if (data.estimatorsCount.trim() === "" || data.randomStates.trim() === "" || data.nJobs.trim() === "") {
        message("请先填入参数", "info");
        return;
      }

      let stockName = "";
      data.stockNameList.forEach(item => {
        if (item["stockId"] === data.stockId) {
          stockName = item["stockName"];
        }
      });
      postRequest("/stockData/randomForestManage", {
        stockName: stockName,
        predictType: "S",
        preDays: "5",
        estimatorsCount: data.estimatorsCount,
        randomStates: data.randomStates,
        nJobs: data.nJobs,
      }).then((res) => {
        if (res.data.code === 200) {
          postRequest("/randomForestArgument/add", {
            estimatorsCount: data.estimatorsCount,
            randomStates: data.randomStates,
            nJobs: data.nJobs,
            meanAbsoluteError: res.data.data[1],
            meanSquaredError: res.data.data[2],
            rootMeanSquaredError: res.data.data[3],
          }).then((addRes) => {
            if (addRes.data.code === 200) {
              initData();
            } else if (addRes.data.code === 500) {
              message(addRes.data.msg, "error");
            }
          });
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function useArgument(row) {
      postRequest("/randomForestArgument/useArgument", {
        id: row.id
      }).then((res) => {
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
      predict,
      useArgument,
    };
  },
};
</script>

<style scoped>




</style>