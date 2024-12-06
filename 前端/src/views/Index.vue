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

        <el-date-picker
            v-model="data.selectTime"
            end-placeholder="End date"
            format="YYYY/MM/DD"
            range-separator="To"
            start-placeholder="Start date"
            style="margin-right: 1rem"
            type="daterange"
        />

        <el-button type="danger" @click="drawBasicInfo(1)">图示基本数据</el-button>
        <el-button type="danger" @click="drawAverageInfo(1)">图示均线数据</el-button>
        <el-button type="danger" @click="drawBasicInfo(2)">表格显示基本数据</el-button>
        <el-button type="danger" @click="drawAverageInfo(3)">表格显示均线数据</el-button>
      </el-row>
    </div>

    <div style="flex: 1; padding: 1rem 0;">
      <div v-if="data.showMode === 1" id="chart" class="single-chart"></div>

      <el-table v-if="data.showMode === 2" id="img-container" :cell-style="{'text-align':'center'}"
                :data="data.stockDataList"
                :header-cell-style="{'text-align':'center'}" border>
        <el-table-column label="日期" prop="stockDate"/>
        <el-table-column label="开盘价" prop="startPrice"/>
        <el-table-column label="最低价" prop="lowPrice"/>
        <el-table-column label="最高价" prop="highPrice"/>
        <el-table-column label="收盘价" prop="endPrice"/>
      </el-table>

      <el-table v-if="data.showMode === 3" id="img-container" :cell-style="{'text-align':'center'}"
                :data="data.averageDataList"
                :header-cell-style="{'text-align':'center'}" border>
        <el-table-column label="日期" prop="stockDate"/>
        <el-table-column label="MA5" prop="m5"/>
        <el-table-column label="MA10" prop="m10"/>
        <el-table-column label="MA20" prop="m20"/>
        <el-table-column label="MA30" prop="m30"/>
        <el-table-column label="MA60" prop="m60"/>
        <el-table-column label="MA120" prop="m120"/>
        <el-table-column label="MA250" prop="m250"/>
      </el-table>
    </div>
  </div>
</template>

<script>
import {onBeforeMount, reactive} from "vue";
import {fillData, formatTime, getRequest, message, postRequest} from "@/utils/api";

export default {
  setup() {
    const data = reactive({
      stockId: "",
      stockNameList: [],
      selectTime: [],
      stockDataList: [],
      averageDataList: [],
      showMode: null,
    });
    onBeforeMount(() => {
      getRequest("/stockName/listAll").then((res) => {
        if (res.data.code === 200) {
          data.stockNameList = res.data.data;
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
      document.title = "系统首页";
    });

    function drawBasicInfo(showMode) {
      if (data.stockId === "") {
        message("请选择需要预测的股票", "info");
        return;
      }
      if (data.selectTime === []) {
        message("请选择需要预测的日期", "info");
        return;
      }
      if (formatTime(data.selectTime[1]) > formatTime(new Date())) {
        message("日期超出范围，请重新选择", "info");
        return;
      }
      data.showMode = showMode;
      let stockName = "";
      data.stockNameList.forEach(item => {
        if (item["stockId"] === data.stockId) {
          stockName = item["stockName"];
        }
      });
      postRequest("/stockData/getBasicData", {
        stockId: data.stockId,
        startDate: formatTime(data.selectTime[0]),
        endDate: formatTime(data.selectTime[1])
      }).then((res) => {
        if (res.data.code === 200) {
          data.stockDataList = res.data.data;
          const xData = [];
          const yData = [];
          const nameList = ["startPrice", "highPrice", "lowPrice", "endPrice"];
          for (let i = 0; i < nameList.length; i++) {
            yData.push({
              name: nameList[i],
              data: []
            })
          }
          data.stockDataList.forEach(item => {
            xData.push(item["stockDate"]);
            yData[0].data.push(item["startPrice"]);
            yData[1].data.push(item["highPrice"]);
            yData[2].data.push(item["lowPrice"]);
            yData[3].data.push(item["endPrice"]);
          });
          if (showMode === 1) {
            fillData(stockName + "股票基本数据情况", xData, yData, nameList);
          }
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    function drawAverageInfo(showMode) {
      if (data.stockId === "") {
        message("请选择需要预测的股票", "info");
        return;
      }
      if (data.selectTime === []) {
        message("请选择需要预测的日期", "info");
        return;
      }
      data.showMode = showMode;

      let stockName = "";
      data.stockNameList.forEach(item => {
        if (item["stockId"] === data.stockId) {
          stockName = item["stockName"];
        }
      });
      postRequest("/stockData/getAverageData", {
        stockName: stockName,
        startDate: formatTime(data.selectTime[0]),
        endDate: formatTime(data.selectTime[1])
      }).then((res) => {
        if (res.data.code === 200) {
          data.averageDataList = res.data.data;
          const xData = [];
          const yData = [];
          const nameList = ["MA5", "MA10", "MA20", "MA30", "MA60", "MA120", "MA250"];
          for (let i = 0; i < 7; i++) {
            yData.push({
              name: nameList[i],
              data: []
            })
          }
          data.averageDataList.forEach(item => {
            xData.push(item["stockDate"]);
            yData[0].data.push(item["m5"]);
            yData[1].data.push(item["m10"]);
            yData[2].data.push(item["m20"]);
            yData[3].data.push(item["m30"]);
            yData[4].data.push(item["m60"]);
            yData[5].data.push(item["m120"]);
            yData[6].data.push(item["m250"]);
          });
          if (showMode === 1) {
            fillData(stockName + "股票均线数据情况", xData, yData, nameList);
          }
        } else if (res.data.code === 500) {
          message(res.data.msg, "error");
        }
      });
    }

    return {
      data,
      drawBasicInfo,
      drawAverageInfo,
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