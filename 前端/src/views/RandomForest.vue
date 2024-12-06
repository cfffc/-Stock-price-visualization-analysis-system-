<template>
  <div  style="display: flex; flex-direction: column; height: 100%; width: 100%">
    <div style="height: fit-content">
      <el-row>
        <el-select v-model="data.stockId" filterable placeholder="股票名称" style="margin-right: 1rem; width: 10rem">
          <el-option
              v-for="item in data.stockNameList"
              :key="item.stockId"
              :label="item.stockName"
              :value="item.stockId"
          />
        </el-select>

        <el-select v-model="data.preDays" filterable placeholder="预测间隔" style="margin-right: 1rem; width: 10rem">
          <el-option label="3" value="3"/>
          <el-option label="5" value="5"/>
          <el-option label="7" value="7"/>
          <el-option label="10" value="10"/>
        </el-select>

        <el-button type="danger" @click="predict('S')">短期均线预测</el-button>
        <el-button type="danger" @click="predict('M')">中期均线预测</el-button>
        <el-button type="danger" @click="predict('L')">长期均线预测</el-button>
        <el-button type="danger" @click="predict('SM')">短中期均线预测</el-button>
        <el-button type="danger" @click="predict('ML')">中长期均线预测</el-button>
        <el-button type="danger" @click="predict('SML')">三线预测</el-button>
      </el-row>

      <el-row style="margin-top: 1rem">
        <el-input v-model="data.meanAbsoluteError" placeholder="平均绝对误差" readonly
                  style="margin-right: 1rem; width: 12rem;"/>
        <el-input v-model="data.meanSquaredError" placeholder="均方误差" readonly
                  style="margin-right: 1rem; width: 12rem;"/>
        <el-input v-model="data.rootMeanSquaredError" placeholder="均方根误差" readonly
                  style="margin-right: 1rem; width: 12rem;"/>
      </el-row>
    </div>

    <div style="flex: 1; padding: 1rem 0;">
      <div id="chart" class="single-chart"></div>
    </div>
  </div>
</template>

<script>
import {onBeforeMount, reactive} from "vue";
import {fillData, getRequest, message, postRequest} from "@/utils/api";

export default {
  setup() {
    const data = reactive({
      stockId: "",
      preDays: "",
      stockNameList: [],
      stockDataList: [],
      meanAbsoluteError: "",
      meanSquaredError: "",
      rootMeanSquaredError: "",
    });
    onBeforeMount(() => {
      getRequest("/stockName/listAll").then((res) => {
        if (res.data.code === 200) {
          data.stockNameList = res.data.data;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
      document.title = "随机森林算法";
    });

    function predict(type) {
      if (data.stockId === "") {
        message("请选择需要预测的股票", "info");
        return;
      }
      if (data.preDays === "") {
        message("请先选择预测时长", "info");
        return;
      }

      let predictType = "S";
      switch (type) {
        case "S":
          predictType = "S";
          break;
        case "M":
          predictType = "M";
          break;
        case "L":
          predictType = "L";
          break;
        case "SM":
          predictType = "SM";
          break;
        case "ML":
          predictType = "ML";
          break;
        case "SML":
          predictType = "SML";
          break;
      }

      let stockName = "";
      data.stockNameList.forEach(item => {
        if (item["stockId"] === data.stockId) {
          stockName = item["stockName"];
        }
      });
      postRequest("/stockData/randomForest", {
        stockName: stockName,
        predictType: predictType,
        preDays: data.preDays
      }).then((res) => {
        if (res.data.code === 200) {
          data.stockDataList = res.data.data[0];
          data.meanAbsoluteError = "平均绝对误差：" + res.data.data[1];
          data.meanSquaredError = "均方误差：" + res.data.data[2];
          data.rootMeanSquaredError = "均方根误差：" + res.data.data[3];
          const xData = [];
          const yData = [];
          const nameList = ["originEndPrice", "predictEndPrice"];
          for (let i = 0; i < nameList.length; i++) {
            yData.push({
              name: nameList[i],
              data: []
            })
          }
          data.stockDataList.forEach(item => {
            xData.push(item[0]);
            yData[0].data.push(item[1]);
            yData[1].data.push(item[2]);
          });
          fillData(stockName + "股票收盘价预测情况", xData, yData, nameList);
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    return {
      data,
      predict,
    };
  },
};
</script>

<style scoped>
.single-chart {
  height: 100%;
  width: 100%;
}




</style>