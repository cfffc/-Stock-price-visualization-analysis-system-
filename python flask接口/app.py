import json

from flask import Flask, request

from GenerateMAData import generateMAData
from GenerateMinuteMAData import generateMinuteMAData
from NewCal import addStock
from LeastSquares import *
from RandomForest import *
from LSTM import *
from KNN import *
from calMinute import addStockMinute
from EnergyStats import energy_state_predict

app = Flask(__name__)


@app.route('/addStock', methods=['post'])
def addStockInterface():
    stockName = request.json.get('stockName')
    resPath = request.json.get('dataDir')
    listOfLines = request.json.get('text').split("\n")
    listOfLines = listOfLines[0:len(listOfLines) - 1]
    addStock(stockName, resPath, listOfLines)
    res = {
        "code": 200,
        "msg": "数据保存成功",
        "data": {}
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/addStockMinute', methods=['post'])
def addStockMinuteInterface():
    stockName = request.json.get('stockName')
    resPath = request.json.get('dataDir')
    listOfLines = request.json.get('text').split("\n")
    listOfLines = listOfLines[0:len(listOfLines) - 1]
    addStockMinute(stockName, resPath, listOfLines)
    res = {
        "code": 200,
        "msg": "数据保存成功",
        "data": {}
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/generateMAData', methods=['post'])
def generateMADataInterface():
    resPath = request.json.get('dataDir')
    stockName = request.json.get('stockName')
    startDate = request.json.get('startDate')
    endDate = request.json.get('endDate')
    MAList = generateMAData(resPath, stockName, startDate, endDate)
    res = {
        "code": 200,
        "msg": "均线数据获取成功",
        "data": MAList
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/generateMinuteMAData', methods=['post'])
def generateMinuteMADataInterface():
    resPath = request.json.get('dataDir')
    stockName = request.json.get('stockName')
    startDate = request.json.get('startDate')
    endDate = request.json.get('endDate')
    MAList = generateMinuteMAData(resPath, stockName, startDate, endDate)
    res = {
        "code": 200,
        "msg": "均线数据获取成功",
        "data": MAList
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/leastSquares', methods=['post'])
def leastSquaresInterface():
    predictType = request.json.get('predictType')
    fileName = request.json.get('fileName')
    dataList = []
    if predictType == "S":
        dataList = least_squares_make_predict_short(fileName)
    elif predictType == "M":
        dataList = least_squares_make_predict_mid(fileName)
    elif predictType == "L":
        dataList = least_squares_make_predict_long(fileName)
    elif predictType == "SM":
        dataList = least_squares_make_predict_short_mid(fileName)
    elif predictType == "ML":
        dataList = least_squares_make_predict_mid_long(fileName)
    elif predictType == "SML":
        dataList = least_squares_make_predict_short_mid_long(fileName)
    res = {
        "code": 200,
        "msg": "最小二乘法数据获取成功",
        "data": dataList
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/randomForest', methods=['post'])
def randomForestInterface():
    predictType = request.json.get('predictType')
    fileName = request.json.get('fileName')
    preDays = request.json.get('preDays')
    estimatorsCount = request.json.get('estimatorsCount')
    randomStates = request.json.get('randomStates')
    nJobs = request.json.get('nJobs')
    resData = {}
    if predictType == "S":
        resData = random_forest_make_predict(fileName, preDays, "E_S", estimatorsCount, randomStates, nJobs)
    elif predictType == "M":
        resData = random_forest_make_predict(fileName, preDays, "E_M", estimatorsCount, randomStates, nJobs)
    elif predictType == "L":
        resData = random_forest_make_predict(fileName, preDays, "E_L", estimatorsCount, randomStates, nJobs)
    elif predictType == "SM":
        resData = random_forest_make_predict(fileName, preDays, "E_SM", estimatorsCount, randomStates, nJobs)
    elif predictType == "ML":
        resData = random_forest_make_predict(fileName, preDays, "E_ML", estimatorsCount, randomStates, nJobs)
    elif predictType == "SML":
        resData = random_forest_make_predict(fileName, preDays, "E_SML", estimatorsCount, randomStates, nJobs)
    res = {
        "code": 200,
        "msg": "随机森林数据获取成功",
        "data": resData
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/lstm', methods=['post'])
def lstmInterface():
    predictType = request.json.get('predictType')
    fileName = request.json.get('fileName')
    preDays = request.json.get('preDays')
    lstmLayers = request.json.get('lstmLayers')
    denseLayers = request.json.get('denseLayers')
    units = request.json.get('units')
    resData = {}
    if predictType == "S":
        resData = LSTM_make_predict(fileName, preDays, "E_S", lstmLayers, denseLayers, units)
    elif predictType == "M":
        resData = LSTM_make_predict(fileName, preDays, "E_M", lstmLayers, denseLayers, units)
    elif predictType == "L":
        resData = LSTM_make_predict(fileName, preDays, "E_L", lstmLayers, denseLayers, units)
    elif predictType == "SM":
        resData = LSTM_make_predict(fileName, preDays, "E_SM", lstmLayers, denseLayers, units)
    elif predictType == "ML":
        resData = LSTM_make_predict(fileName, preDays, "E_ML", lstmLayers, denseLayers, units)
    elif predictType == "SML":
        resData = LSTM_make_predict(fileName, preDays, "E_SML", lstmLayers, denseLayers, units)
    res = {
        "code": 200,
        "msg": "LSTM数据获取成功",
        "data": resData
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/knn', methods=['post'])
def knnInterface():
    predictType = request.json.get('predictType')
    fileName = request.json.get('fileName')
    preDays = request.json.get('preDays')
    resData = {}
    if predictType == "S":
        resData = KNN_make_predict(fileName, preDays, "E_S")
    elif predictType == "M":
        resData = KNN_make_predict(fileName, preDays, "E_M")
    elif predictType == "L":
        resData = KNN_make_predict(fileName, preDays, "E_L")
    elif predictType == "SM":
        resData = KNN_make_predict(fileName, preDays, "E_SM")
    elif predictType == "ML":
        resData = KNN_make_predict(fileName, preDays, "E_ML")
    elif predictType == "SML":
        resData = KNN_make_predict(fileName, preDays, "E_SML")
    res = {
        "code": 200,
        "msg": "KNN数据获取成功",
        "data": resData
    }
    resJson = json.dumps(res)
    return resJson


@app.route('/energyStatePredict', methods=['post'])
def energyStatePredictInterface():
    fileNameDay = request.json.get('fileNameDay')
    fileNameMin = request.json.get('fileNameMin')
    dayCounts = request.json.get('dayCounts')
    preDay = request.json.get('preDay')
    dataListForES, dataListForEM = energy_state_predict(fileNameDay, fileNameMin, dayCounts, preDay)
    resData = {
        "dataListForES": dataListForES,
        "dataListForEM": dataListForEM
    }
    res = {
        "code": 200,
        "msg": "均线能量分析数据获取成功",
        "data": resData
    }
    resJson = json.dumps(res)
    return resJson


if __name__ == '__main__':
    app.run(port=5000, host='0.0.0.0')
