# 调用库
import numpy as np
import pandas as pd

# pre_days 预测天数
# mem_his_days 记忆天数
# 处理数据
def get_DATA(df, pre_days, mem_his_days, E):
    new_data = pd.DataFrame()
    # 对数据进行预处理
    df['everyDate'] = pd.to_datetime(df.everyDate, format='%Y/%m/%d')
    new_data[E] = df[E]
    new_data['endP'] = df['endP']
    new_data.index = df['everyDate']

    new_data.dropna(inplace=True)
    new_data.sort_index(inplace=True)

    new_data['label'] = new_data['endP'].shift(-pre_days)

    #     归一化
    from sklearn.preprocessing import StandardScaler
    scaler = StandardScaler()
    sca_x = scaler.fit_transform(new_data.iloc[:, :-1])

    # 创建X和Y的值
    from collections import deque
    deq = deque(maxlen=mem_his_days)
    X = []
    for i in sca_x:
        deq.append(list(i))
        if len(deq) == mem_his_days:
            X.append(list(deq))
    X_lately = X[-pre_days:]
    X = X[:-pre_days]

    y = new_data['label'].values[mem_his_days - 1:-pre_days]

    # # 将X和Y列表转换成数组
    X = np.array(X)
    y = np.array(y)

    return X, y, X_lately, new_data


# 均线能量计算
def LSTM_make_predict(fileName, pre_days, E_name, the_lstm_layers, the_dense_layers, the_units):
    df = pd.read_excel(fileName)

    the_mem_days = 3

    X, y, X_lately, new_data = get_DATA(df, pre_days, the_mem_days, E_name)

    X_list = X.tolist()

    # print(X_list)

    # # 构建模型
    split = int(len(X) * 0.9)
    X_train, X_test = X_list[:split], X_list[split:]
    y_train, y_test = y[:split], y[split:]

    X_train = np.array(X_train)
    X_test = np.array(X_test)
    y_train = np.array(y_train)
    y_test = np.array(y_test)

    from tensorflow.python.keras.models import Sequential
    from tensorflow.python.keras.layers import Dense, Dropout, LSTM
    #
    model = Sequential()
    # 第一层设了三个神经元
    model.add(LSTM(the_units, input_shape=X.shape[1:], activation='relu', return_sequences=True))
    model.add(Dropout(0.1))

    for i in range(the_lstm_layers):
        model.add(LSTM(the_units, activation='relu', return_sequences=True))
        model.add(Dropout(0.1))

    model.add(LSTM(the_units, activation='relu'))
    model.add(Dropout(0.1))

    # 全连接层
    for i in range(the_dense_layers):
        model.add(Dense(the_units, activation='relu'))
        model.add(Dropout(0.1))
    # 输出层
    model.add(Dense(1))
    # 编译模型
    model.compile(optimizer='adam',
                  loss='mse',
                  metrics=['mape'])
    # # 训练模型
    #
    model.fit(X_train, y_train, batch_size=32, epochs=50, validation_data=(X_test, y_test))
    #
    a = model.evaluate(X_test, y_test)
    # 精度损失
    loss = a[0]
    # 偏差精度
    mape = a[1]



    pre = model.predict(X_test)
    pre = pre.tolist()
    new_data_time = new_data.index[-len(y_test):]

    # 均方根误差，值越低越好
    Root_Mean_Squared_Error = np.sqrt(np.mean(np.power((np.array(y_test) - np.array(pre)), 2)))

    # 将结果以list方式返回：[[日期，预测值，真实值]] ,损失值，偏差值
    data_list = []
    for i in range(len(y_test)):
        data_temp_list = list()
        data_temp_list.append(new_data_time[i].strftime('%Y/%m/%d'))
        data_temp_list.append(round(pre[i][0],2))
        data_temp_list.append(y_test[i])

        data_list.append(data_temp_list)
    return data_list[-30:], round(loss,2), round(mape,2),round(Root_Mean_Squared_Error,2)
