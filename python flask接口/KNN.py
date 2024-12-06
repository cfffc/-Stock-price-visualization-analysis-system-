from sklearn import neighbors
from sklearn.model_selection import GridSearchCV
from datetime import datetime
import numpy as np
import pandas as pd


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


def KNN_make_predict(fileName, pre_days, E_name):
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
    # 自变量为日期，能量
    # 因变量为收盘价
    X = df[['year', 'month', 'day', 'week', 'ydays', 'isMstart', 'isYstart', 'isYend', E_name]]
    X = X[:-pre_days]
    y = df['endP'].shift(-pre_days)
    y = y[:-pre_days]

    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    scal_X = scaler.fit_transform(X.iloc[:, :])

    X_len = scal_X.shape[0]
    split = int(X_len * 0.8)
    X_train, X_test = scal_X[:split], scal_X[split:]
    y_train, y_test = y[:split], y[split:]

    # 使用gridsearch查找最佳参数
    params = {'n_neighbors': [2, 3, 4, 5, 6, 7, 8, 9]}
    knn = neighbors.KNeighborsRegressor()
    model = GridSearchCV(knn, params, cv=5)
    # 拟合模型并进行预测
    model.fit(X_train, y_train)
    preds_list = model.predict(X_test)

    date_list = df.index[-len(preds_list):]

    realEndP = y_test.tolist()

    # 均方根误差，值越低越好
    Root_Mean_Squared_Error = np.sqrt(np.mean(np.power((np.array(y_test) - np.array(preds_list)), 2)))
    # 以[[日期，预测值，真实值]]返回
    data_list = []
    for i in range(len(realEndP)):
        temp_list = []
        temp_list.append(date_list[i])
        temp_list.append(round(preds_list[i], 2))
        temp_list.append(realEndP[i])

        data_list.append(temp_list)

    return data_list[-30:], round(Root_Mean_Squared_Error, 2)
