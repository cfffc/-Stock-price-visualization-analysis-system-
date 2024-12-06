import numpy as np
import pandas as pd
from numpy.linalg import inv  # 矩阵求逆
from numpy import dot  # 矩阵点乘


# 短期均线能量计算
def least_squares_make_predict_short(fileName):
    df = pd.read_excel(fileName)  # 读入数据

    # 设置自变量和因变量
    # 自变量为10线、20线、排列金叉死叉、斜率、距离
    # 因变量为价格的变化量
    X = df[['MA10', 'MA20', 'XL10', 'XL20', 'S1020']]
    y = df[['endP']]
    X_len = X.shape[0]
    split = int(X_len * 0.9)
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    # 训练模型
    a = dot(dot(inv(np.dot(X_train.T, X_train)), X_train.T), y_train)  # 最小二乘法求解公式

    # 处理索引（因为测试集是一个切片，索引从切掉的地方计数，因此需要重置索引）
    y_test_index = list()
    X_test_index = list()
    for i in range(len(y_test)):
        y_test_index.append(i)
        X_test_index.append(i)
    y_test.index = y_test_index
    X_test.index = X_test_index

    date = df[['everyDate']]
    dateList = date[split:]

    # 使用测试集
    y_predict = list()
    for i in range(len(y_test)):
        predict_y = a[0] * X_test['MA10'][i] + a[1] * X_test['MA20'][i] + a[2] * X_test['XL10'][i] + a[3] * \
                    X_test['XL20'][i] + a[4] * X_test['S1020'][i]
        y_predict.append(predict_y)

    result = pd.DataFrame()
    result['实际结果'] = y_test
    result['预测结果'] = y_predict

    dataList = []
    y_testList = y_test.values.tolist()
    dateList = dateList.values.tolist()
    for i in range(len(y_predict)):
        tempList = [dateList[i][0], str(round(y_predict[i][0], 2)), str(y_testList[i][0])]
        dataList.append(tempList)
    return dataList


# 中期均线能量
def least_squares_make_predict_mid(fileName):
    df = pd.read_excel(fileName)  # 读入数据

    # 设置自变量和因变量
    # 自变量为10线、20线、排列金叉死叉、斜率、距离
    # 因变量为价格的变化量
    X = df[['MA30', 'MA60', 'XL30', 'XL60', 'S3060']]
    y = df[['endP']]
    X_len = X.shape[0]
    split = int(X_len * 0.9)
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    # 训练模型
    a = dot(dot(inv(np.dot(X_train.T, X_train)), X_train.T), y_train)  # 最小二乘法求解公式

    # 处理索引（因为测试集是一个切片，索引从切掉的地方计数，因此需要重置索引）
    y_test_index = list()
    X_test_index = list()
    for i in range(len(y_test)):
        y_test_index.append(i)
        X_test_index.append(i)
    y_test.index = y_test_index
    X_test.index = X_test_index

    date = df[['everyDate']]
    dateList = date[split:]

    # 使用测试集
    y_predict = list()
    for i in range(len(y_test)):
        predict_y = a[0] * X_test['MA30'][i] + a[1] * X_test['MA60'][i] + a[2] * X_test['XL30'][i] + a[3] * \
                    X_test['XL60'][i] + a[4] * X_test['S3060'][i]
        y_predict.append(predict_y)

    result = pd.DataFrame()
    result['实际结果'] = y_test
    result['预测结果'] = y_predict

    dataList = []
    y_testList = y_test.values.tolist()
    dateList = dateList.values.tolist()
    for i in range(len(y_predict)):
        tempList = [dateList[i][0], str(round(y_predict[i][0], 2)), str(y_testList[i][0])]
        dataList.append(tempList)
    return dataList


# 长期均线能量
def least_squares_make_predict_long(fileName):
    df = pd.read_excel(fileName)  # 读入数据

    # 设置自变量和因变量
    # 自变量为10线、20线、排列金叉死叉、斜率、距离
    # 因变量为价格的变化量
    X = df[['MA120', 'MA250', 'XL120', 'XL250', 'S120250']]
    y = df[['endP']]
    X_len = X.shape[0]
    split = int(X_len * 0.9)
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    # 训练模型
    a = dot(dot(inv(np.dot(X_train.T, X_train)), X_train.T), y_train)  # 最小二乘法求解公式

    # 处理索引（因为测试集是一个切片，索引从切掉的地方计数，因此需要重置索引）
    y_test_index = list()
    X_test_index = list()
    for i in range(len(y_test)):
        y_test_index.append(i)
        X_test_index.append(i)
    y_test.index = y_test_index
    X_test.index = X_test_index

    date = df[['everyDate']]
    dateList = date[split:]

    # 使用测试集
    y_predict = list()
    for i in range(len(y_test)):
        predict_y = a[0] * X_test['MA120'][i] + a[1] * X_test['MA250'][i] + a[2] * X_test['XL120'][i] + a[3] * \
                    X_test['XL250'][i] + a[4] * X_test['S120250'][i]
        y_predict.append(predict_y)

    result = pd.DataFrame()
    result['实际结果'] = y_test
    result['预测结果'] = y_predict

    dataList = []
    y_testList = y_test.values.tolist()
    dateList = dateList.values.tolist()
    for i in range(len(y_predict)):
        tempList = [dateList[i][0], str(round(y_predict[i][0], 2)), str(y_testList[i][0])]
        dataList.append(tempList)
    return dataList


# 短中期均线能量
def least_squares_make_predict_short_mid(fileName):
    df = pd.read_excel(fileName)  # 读入数据

    # 设置自变量和因变量
    # 自变量为10线、20线、排列金叉死叉、斜率、距离
    # 因变量为价格的变化量
    X = df[['MA10', 'MA20', 'XL10', 'XL20', 'S1020', 'MA30', 'MA60', 'XL30', 'XL60', 'S3060']]
    y = df[['endP']]
    X_len = X.shape[0]
    split = int(X_len * 0.9)
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    date = df[['everyDate']]
    dateList = date[split:]

    # 训练模型
    a = dot(dot(inv(np.dot(X_train.T, X_train)), X_train.T), y_train)  # 最小二乘法求解公式

    # 处理索引（因为测试集是一个切片，索引从切掉的地方计数，因此需要重置索引）
    y_test_index = list()
    X_test_index = list()
    for i in range(len(y_test)):
        y_test_index.append(i)
        X_test_index.append(i)
    y_test.index = y_test_index
    X_test.index = X_test_index

    # 使用测试集
    y_predict = list()
    for i in range(len(y_test)):
        predict_y = a[0] * X_test['MA10'][i] + a[1] * X_test['MA20'][i] + a[2] * X_test['XL10'][i] + a[3] * \
                    X_test['XL20'][i] + a[4] * X_test['S1020'][i] + a[5] * X_test['MA30'][i] + a[6] * \
                    X_test['MA60'][i] \
                    + a[7] * X_test['XL30'][i] + a[8] * X_test['XL60'][i] + a[9] * X_test['S3060'][i]
        y_predict.append(predict_y)

    result = pd.DataFrame()
    result['实际结果'] = y_test
    result['预测结果'] = y_predict

    dataList = []
    y_testList = y_test.values.tolist()
    dateList = dateList.values.tolist()
    for i in range(len(y_predict)):
        tempList = [dateList[i][0], str(round(y_predict[i][0], 2)), str(y_testList[i][0])]
        dataList.append(tempList)
    return dataList


# 中长期均线能量
def least_squares_make_predict_mid_long(fileName):
    df = pd.read_excel(fileName)  # 读入数据

    # 设置自变量和因变量
    # 自变量为10线、20线、排列金叉死叉、斜率、距离
    # 因变量为价格的变化量
    X = df[['MA30', 'MA60', 'XL30', 'XL60', 'S3060', 'MA120', 'MA250', 'XL120', 'XL250', 'S120250']]
    y = df[['endP']]

    X_len = X.shape[0]
    split = int(X_len * 0.9)
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    date = df[['everyDate']]
    dateList = date[split:]

    # 训练模型
    a = dot(dot(inv(np.dot(X_train.T, X_train)), X_train.T), y_train)  # 最小二乘法求解公式

    # 处理索引（因为测试集是一个切片，索引从切掉的地方计数，因此需要重置索引）
    y_test_index = list()
    X_test_index = list()
    for i in range(len(y_test)):
        y_test_index.append(i)
        X_test_index.append(i)
    y_test.index = y_test_index
    X_test.index = X_test_index

    # 使用测试集
    y_predict = list()
    for i in range(len(y_test)):
        predict_y = a[0] * X_test['MA30'][i] + a[1] * X_test['MA60'][i] + a[2] * X_test['XL30'][i] + a[3] * \
                    X_test['XL60'][i] + a[4] * X_test['S3060'][i] + a[5] * X_test['MA120'][i] + a[6] * \
                    X_test['MA250'][i] \
                    + a[7] * X_test['XL120'][i] + a[8] * X_test['XL250'][i] + a[9] * X_test['S120250'][i]
        y_predict.append(predict_y)

    result = pd.DataFrame()
    result['实际结果'] = y_test
    result['预测结果'] = y_predict

    dataList = []
    y_testList = y_test.values.tolist()
    dateList = dateList.values.tolist()
    for i in range(len(y_predict)):
        tempList = [dateList[i][0], str(round(y_predict[i][0], 2)), str(y_testList[i][0])]
        dataList.append(tempList)
    return dataList


# 三期均线能量
def least_squares_make_predict_short_mid_long(fileName):
    df = pd.read_excel(fileName)  # 读入数据

    # 设置自变量和因变量
    # 自变量为10线、20线、排列金叉死叉、斜率、距离
    # 因变量为价格的变化量
    X = df[['MA10', 'MA20', 'XL10', 'XL20', 'S1020', 'MA30', 'MA60', 'XL30', 'XL60', 'S3060', 'MA120', 'MA250',
            'XL120', 'XL250', 'S120250']]
    y = df[['endP']]
    date = df[['everyDate']]
    X_len = X.shape[0]
    split = int(X_len * 0.9)
    X_train, X_test = X[:split], X[split:]
    y_train, y_test = y[:split], y[split:]

    dateList = date[split:]

    # 训练模型
    a = dot(dot(inv(np.dot(X_train.T, X_train)), X_train.T), y_train)  # 最小二乘法求解公式

    # 处理索引（因为测试集是一个切片，索引从切掉的地方计数，因此需要重置索引）
    y_test_index = list()
    X_test_index = list()
    for i in range(len(y_test)):
        y_test_index.append(i)
        X_test_index.append(i)
    y_test.index = y_test_index
    X_test.index = X_test_index

    # 使用测试集
    y_predict = list()
    for i in range(len(y_test)):
        predict_y = a[0] * X_test['MA10'][i] + a[1] * X_test['MA20'][i] + a[2] * X_test['XL10'][i] + a[3] * \
                    X_test['XL20'][i] + a[4] * X_test['S1020'][i] + a[5] * X_test['MA30'][i] + a[6] * \
                    X_test['MA60'][i] \
                    + a[7] * X_test['XL30'][i] + a[8] * X_test['XL60'][i] + a[9] * X_test['S3060'][i] \
                    + a[10] * X_test['MA120'][i] + a[11] * X_test['MA250'][i] + a[12] * X_test['XL120'][i] + a[13] * \
                    X_test['XL250'][i] + a[14] * X_test['S120250'][i]
        y_predict.append(predict_y)

    result = pd.DataFrame()
    result['实际结果'] = y_test
    result['预测结果'] = y_predict

    dataList = []
    y_testList = y_test.values.tolist()
    dateList = dateList.values.tolist()
    for i in range(len(y_predict)):
        tempList = [dateList[i][0], str(round(y_predict[i][0], 2)), str(y_testList[i][0])]
        dataList.append(tempList)
    return dataList
