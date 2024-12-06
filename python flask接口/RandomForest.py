from datetime import datetime
import numpy as np

import pandas as pd

from sklearn.ensemble import RandomForestRegressor


# 返回列表顺序为：年月日，周几，一年的第几天，是否是月初，是否是月末
def get_date_features(date):
    date_features_list = list()
    date = datetime.strptime(date, '%Y/%m/%d')
    date = datetime.date(date)

    # 获取年月日
    year = date.year
    month = date.month
    day = date.day
    date_features_list.append(year)
    date_features_list.append(month)
    date_features_list.append(day)
    # 一周中的第几天
    week = date.isoweekday()
    date_features_list.append(week)
    # 一年中的第几天
    # 把日期类型dd转为time.struct_time
    date_stru = date.timetuple()
    day_of_year = date_stru.tm_yday
    date_features_list.append(day_of_year)
    # 是否是月初
    if (day == 1):
        is_month_start = 1
    else:
        is_month_start = -1
    date_features_list.append(is_month_start)
    # 是否是年初
    if (day == 1 and month == 1):
        is_year_start = 1
    else:
        is_year_start = -1
    date_features_list.append(is_year_start)
    # 是否是年末
    if (day_of_year == 366 or (day_of_year == 365 and year % 4 != 0)):
        is_year_end = 1
    else:
        is_year_end = -1
    date_features_list.append(is_year_end)

    return date_features_list


# 均线能量预测
# estimators_count4   随机森林树的数目
# pre_days            预测天数
# E_name              能量名称
# random_states       控制构建树时样本的随机抽样
# n_jobs              并行运行的Job数目。
def random_forest_make_predict(fileName, pre_days, E_name, estimators_count, random_states, n_jobs):
    df = pd.read_excel(fileName)

    # 将要用来训练的日期的特征保存在df中
    model_days_year_list = list()
    model_days_month_list = list()
    model_days_day_list = list()
    model_days_week_list = list()
    model_days_ydays_list = list()
    model_days_isMstart_list = list()
    model_days_isYstart_list = list()
    model_days_isYend_list = list()

    for i in range(len(df)):
        date_model = df['everyDate'][i]
        day_features_list = get_date_features(date_model)
        model_days_year_list.append(day_features_list[0])
        model_days_month_list.append(day_features_list[1])
        model_days_day_list.append(day_features_list[2])
        model_days_week_list.append(day_features_list[3])
        model_days_ydays_list.append(day_features_list[4])
        model_days_isMstart_list.append(day_features_list[5])
        model_days_isYstart_list.append(day_features_list[6])
        model_days_isYend_list.append(day_features_list[7])

    df['year'] = model_days_year_list
    df['month'] = model_days_month_list
    df['day'] = model_days_day_list
    df['week'] = model_days_week_list
    df['ydays'] = model_days_ydays_list
    df['isMstart'] = model_days_isMstart_list
    df['isYstart'] = model_days_isYstart_list
    df['isYend'] = model_days_isYend_list

    df.insert(df.shape[1], 'Date', list(df['everyDate']))
    df.index = df['Date']

    # 设置自变量和因变量
    # 自变量为时间的特征
    # 因变量为短期均线能量
    # X归一化
    X1 = df[['year', 'month', 'day', 'week', 'ydays', 'isMstart', 'isYstart', 'isYend']]
    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    X1 = scaler.fit_transform(X1.iloc[:, :])
    y1 = df[[E_name]]

    # 创建训练集和测试集
    X1_len = X1.shape[0]
    split = int(X1_len * 0.8)
    X1_train, X1_test = X1[:split], X1[split:]
    y1_train, y1_test = y1[:split], y1[split:]

    # 创建模型
    model1 = RandomForestRegressor(n_estimators=estimators_count, random_state=random_states, bootstrap=True,
                                   oob_score=False, n_jobs=n_jobs)
    model1.fit(X1_train, y1_train)

    # 用测试集测试准确性
    E_S_predict = pd.DataFrame()
    pre_ES_list = model1.predict(X1_test)
    E_S_predict[E_name] = pre_ES_list

    # 创建第二个模型
    # 自变量为能量
    # 因变量为收盘价
    # 创建预测值

    X2 = df[['year', 'month', 'day', 'week', 'ydays', 'isMstart', 'isYstart', 'isYend', E_name]]

    X2_totest = df[['year', 'month', 'day', 'week', 'ydays', 'isMstart', 'isYstart', 'isYend']]

    X2_len = X2.shape[0]
    split = int(X2_len * 0.8)
    X2_totest = X2_totest[split:]
    X2_totest.insert(X2_totest.shape[1], E_name, pre_ES_list)

    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    X2 = scaler.fit_transform(X2.iloc[:, :])
    X2_totest = scaler.fit_transform(X2_totest.iloc[:, :])

    y2 = df[['endP']].shift(-pre_days)
    y2_test = y2['endP'][split - pre_days:-pre_days]

    # 创建训练集和测试集
    X2_train = X2[:split]
    y2_train = y2[:split]

    # 训练第二个模型
    model2 = RandomForestRegressor(n_estimators=estimators_count, random_state=random_states, bootstrap=True,
                                   oob_score=False, n_jobs=n_jobs)
    model2.fit(X2_train, y2_train)
    # 预测出收盘价
    endP_predict = pd.DataFrame()
    pre_endP_list = model2.predict(X2_totest)
    endP_predict['endP'] = pre_endP_list
    #
    # print(endP_predict['endP'])

    date_list = df.index[-len(pre_endP_list):]

    # 把收盘价pd转成列表
    real_endP_list = y2_test.tolist()

    from sklearn import metrics

    # 平均绝对误差
    Mean_Absolute_Error = metrics.mean_absolute_error(real_endP_list, pre_endP_list)
    # 均方误差
    Mean_Squared_Error = metrics.mean_squared_error(real_endP_list, pre_endP_list)
    # 均方根误差
    Root_Mean_Squared_Error = np.sqrt(metrics.mean_squared_error(real_endP_list, pre_endP_list))

    # 以[[日期，预测值，真实值]]的列表形式返回
    data_list = []
    for i in range(len(y2_test)):
        data_temp = []
        data_temp.append(date_list[i])
        data_temp.append(round(pre_endP_list[i], 2))
        data_temp.append(real_endP_list[i])

        data_list.append(data_temp)

    # 返回列表，平均绝对误差，均方误差，均方根误差
    return data_list, round(Mean_Absolute_Error, 2), round(Mean_Squared_Error, 2), round(Root_Mean_Squared_Error, 2)
