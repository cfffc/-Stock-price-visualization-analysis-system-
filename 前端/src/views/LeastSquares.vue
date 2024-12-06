<template>
  <div  style="display: flex; flex-direction: column; height: 100%; width: 100%">
    <div style="height: fit-content">
      <el-select v-model="data.stockId" filterable placeholder="股票名称" style="margin-right: 1rem; width: 10rem">
        <el-option
            v-for="item in data.stockNameList"
            :key="item.stockId"
            :label="item.stockName"
            :value="item.stockId"
        />
      </el-select>

      <el-button type="danger" @click="predict('S')">短期均线预测</el-button>
      <el-button type="danger" @click="predict('M')">中期均线预测</el-button>
      <el-button type="danger" @click="predict('L')">长期均线预测</el-button>
      <el-button type="danger" @click="predict('SM')">短中期均线预测</el-button>
      <el-button type="danger" @click="predict('ML')">中长期均线预测</el-button>
      <el-button type="danger" @click="predict('SML')">三线预测</el-button>
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
      stockNameList: [],
      stockDataList: [],
    });
    onBeforeMount(() => {
      getRequest("/stockName/listAll").then((res) => {
        if (res.data.code === 200) {
          data.stockNameList = res.data.data;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
      document.title = "最小二乘法";
    });

    function predict(type) {
      if (data.stockId === "") {
        message("请选择需要预测的股票", "info");
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
      postRequest("/stockData/leastSquares", {
        stockName: stockName,
        predictType: predictType
      }).then((res) => {
        if (res.data.code === 200) {
          data.stockDataList = res.data.data;
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
            xData.push(item["stockDate"]);
            yData[0].data.push(item["originPrice"]);
            yData[1].data.push(item["predictPrice"]);
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