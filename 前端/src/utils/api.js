import axios from "axios";
import {ElMessage} from 'element-plus';
import * as echarts from 'echarts';
import {nextTick} from "vue";

axios.defaults.baseURL = "http://127.0.0.1:10031";

export function message(msg, type, duration = 1000) {
    return ElMessage({
        message: msg,
        showClose: true,
        type: type,
        center: true,
        duration: duration
    })
}

export const getRequest = (url, params) => {
    return axios({
        method: 'get',
        url: url,
        params: params,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const postRequest = (url, data) => {
    return axios({
        method: 'post',
        url: url,
        data: data,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const putRequest = (url, data) => {
    return axios({
        method: 'put',
        url: url,
        data: data,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const deleteRequest = (url, data) => {
    return axios({
        method: 'delete',
        url: url,
        data: data,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const downloadFileToLocal = (content, filename) => {
    const eleLink = document.createElement('a');
    eleLink.download = filename;
    eleLink.style.display = 'none';
    const blob = new Blob([content]);
    eleLink.href = URL.createObjectURL(blob);
    document.body.appendChild(eleLink);
    eleLink.click();
    document.body.removeChild(eleLink);
}

export const formatTime = (time) => {
    let year = time.getFullYear();
    let month = time.getMonth() + 1;
    let today = time.getDate();
    return year + '/' + fillZero(month) + '/' + fillZero(today);
}

function fillZero(str) {
    let realNum;
    if (str < 10) {
        realNum = '0' + str;
    } else {
        realNum = str;
    }
    return realNum;
}

let myChart;

function drawChart(id, title, xData, seriesData, legendData) {
    const chartDom = document.getElementById(id);
    if (myChart) {
        myChart.dispose();
    }
    myChart = echarts.init(chartDom);
    myChart.clear();

    const option = {
        title: {
            text: title,
        },
        tooltip: {
            trigger: "axis",
        },
        legend: {
            data: legendData
        },
        grid: {
            left: "3%",
            right: "4%",
            bottom: "0",
            containLabel: true,
        },
        toolbox: {
            feature: {
                saveAsImage: {},
            },
        },
        xAxis: {
            name: "日期",
            type: "category",
            data: xData,
        },
        yAxis: {
            name: "价格",
            type: "value",
            min: (value) => {
                return Math.floor(value.min);
            },
            max: (value) => {
                return Math.ceil(value.max);
            }
        },
        series: seriesData
    };

    option && myChart.setOption(option);

    window.addEventListener("resize", function () {
        myChart.resize();
    });
}

export const fillData = (title, xData, yData, nameList) => {
    const seriesData = [];
    for (let i = 0; i < nameList.length; i++) {
        seriesData.push({
            name: yData[i].name,
            type: "line",
            data: yData[i].data,
            smooth: true,
        })
    }
    drawChart("chart", title, xData, seriesData, nameList);
}

export const drawPieChart = (id, title, seriesData) => {
    const chartDom = document.getElementById(id);
    if (myChart) {
        myChart.dispose();
    }
    myChart = echarts.init(chartDom);
    myChart.clear();

    const option = {
        title: {
            text: title,
        },
        tooltip: {
            trigger: 'item'
        },
        legend: {
            top: '5%',
            left: 'center'
        },
        series: [
            {
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 10,
                    borderColor: '#fff',
                    borderWidth: 2
                },
                label: {
                    show: false,
                    position: 'center'
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 20,
                        fontWeight: 'bold'
                    }
                },
                labelLine: {
                    show: false
                },
                data: seriesData
            }
        ]
    };

    option && myChart.setOption(option);

    window.addEventListener("resize", function () {
        myChart.resize();
    });
}