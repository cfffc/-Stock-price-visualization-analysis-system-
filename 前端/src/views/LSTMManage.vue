<template>
  <div  >
    <el-select v-model="data.stockId" filterable placeholder="股票名称" style="margin-right: 1rem; width: 10rem">
      <el-option
          v-for="item in data.stockNameList"
          :key="item.stockId"
          :label="item.stockName"
          :value="item.stockId"
      />
    </el-select>

    <el-input v-model="data.lstmLayers" placeholder="lstmLayers" style="width: 10rem; margin-right: 1rem;"/>
    <el-input v-model="data.denseLayers" placeholder="denseLayers" style="width: 10rem; margin-right: 1rem;"/>
    <el-input v-model="data.units" placeholder="units" style="width: 10rem; margin-right: 1rem;"/>

    <el-button type="danger" @click="predict">预测</el-button>
  </div>

  <el-table id="img-container" :cell-style="{'text-align':'center'}" :data="data.predictDataList"
            :header-cell-style="{'text-align':'center'}"
            border style="margin-top: 1rem;">
    <el-table-column label="ID" prop="id"/>
    <el-table-column label="lstmLayers" prop="lstmLayers"/>
    <el-table-column label="denseLayers" prop="denseLayers"/>
    <el-table-column label="units" prop="units"/>
    <el-table-column label="损失精度" prop="loss"/>
    <el-table-column label="偏差精度" prop="mape"/>
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
import {ElLoading} from "element-plus";

export default {
  setup() {
    const data = reactive({
      lstmLayers: "",
      denseLayers: "",
      units: "",
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
      document.title = "LSTM参数管理";
    });

    function initData() {
      getRequest("/lstmArgument/list").then((res) => {
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
      if (data.lstmLayers.trim() === "" || data.denseLayers.trim() === "" || data.units.trim() === "") {
        message("请先填入参数", "info");
        return;
      }
      const loadingInstance = ElLoading.service({text: '数据加载中，请稍候', background: 'rgba(0, 0, 0, 0.7)'});

      let stockName = "";
      data.stockNameList.forEach(item => {
        if (item["stockId"] === data.stockId) {
          stockName = item["stockName"];
        }
      });
      postRequest("/stockData/lstmManage", {
        stockName: stockName,
        predictType: "S",
        preDays: "5",
        lstmLayers: data.lstmLayers,
        denseLayers: data.denseLayers,
        units: data.units,
      }).then((res) => {
        loadingInstance.close();

        if (res.data.code === 200) {
          postRequest("/lstmArgument/add", {
            lstmLayers: data.lstmLayers,
            denseLayers: data.denseLayers,
            units: data.units,
            loss: res.data.data[1],
            mape: res.data.data[2],
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
      postRequest("/lstmArgument/useArgument", {
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