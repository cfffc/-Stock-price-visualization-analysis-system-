# 调用库
import pandas as pd


# 传进来的数据要求：日线的日期一定要涵盖三十分钟线的日期，比如：日线的数据是：2019.01.01-2023.01.01，三十分钟线的日期范围只能在这里面
# fileNameDay：日线的路径
# fileNameMin：三十分钟线的路径
# dayCounts:统计天数
# preDay:对比天数
# 返回：两个列表：
# 第一个列表是关于短期均线能量的统计数据：[[状态互逆的天数，状态互逆时第几天收盘价上涨的天数，状态互逆时第几天收盘价下降的天数]，
#                                   [状态相同的天数，状态相同时第几天收盘价上涨的天数，状态相同时第几天收盘价下降的天数]]
#
# 第二个列表是关于中期均线能量的统计数据：[[状态互逆的天数，状态互逆时第几天收盘价上涨的天数，状态互逆时第几天收盘价下降的天数]，
# #                                   [状态相同的天数，状态相同时第几天收盘价上涨的天数，状态相同时第几天收盘价下降的天数]]
def energy_state_predict(fileNameDay, fileNameMin, dayCounts, preDay):
    dfDay = pd.read_excel(fileNameDay)
    dfMin = pd.read_excel(fileNameMin)

    dayCounts += preDay

    # 获得时间为15.00的所有数据的索引
    indexList = []
    minList = dfMin['minute'].tolist()
    for i in range(len(dfMin)):
        if minList[i] == 1500:
            indexList.append(i)

    #    获得日期、短期均线能量、中期均线能量,收盘价的列表
    dateList = []
    ESList = []
    EMList = []
    endPList = []

    for i in indexList:
        dateList.append(dfMin['everyDate'][i])
        ESList.append(dfMin['E_S'][i])
        EMList.append(dfMin['E_M'][i])
        endPList.append(dfMin['endP'][i])
    # 创建一个新的df,把五分钟的数据放进去
    dataForMin = pd.DataFrame()
    dataForMin['everyDate'] = dateList
    dataForMin['E_S'] = ESList
    dataForMin['E_M'] = EMList
    dataForMin['endP'] = endPList

    for i in range(len(dfDay)):
        if dfDay['everyDate'][i] == dataForMin['everyDate'][0]:
            splitStart = i
        if dfDay['everyDate'][i] == dataForMin['everyDate'][len(dataForMin) - 1]:
            splitEnd = i
            break

    # 将dfDay切片一下  重新创建一个
    dataForDay = dfDay[splitStart:splitEnd + 1]
    # 处理切片后的索引,使其从0开始,这个时候 30分钟线和日线的日期与索引都相互对应
    dataForDayIndex = list()
    for i in range(len(dataForDay)):
        dataForDayIndex.append(i)
    dataForDay.index = dataForDayIndex

    needDataToMin = dataForMin[-dayCounts:]
    needDataToDay = dataForDay[-dayCounts:]

    dataForIndex = list()
    for i in range(dayCounts):
        dataForIndex.append(i)
    needDataToMin.index = dataForIndex
    needDataToDay.index = dataForIndex

    # 记录短期均线能量和中期均线能量状态互逆的数量
    oppositeCountsForES = 0
    oppositeCountsForEM = 0
    # 记录短期均线能量和中期均线能量状态相同的数量
    sameCountsForES = 0
    sameCountsForEM = 0
    # 记录短期均线能量和中期均线能量状态互逆时，股价上涨的天数的数量
    oppositeUpCountsForES = 0
    oppositeUpCountsForEM = 0
    oppositeDownCountsForES = 0
    oppositeDownCountsForEM = 0
    # 记录短期均线能量和中期均线能量状态相同时，股价下降的天数的数量
    sameUpCountsForES = 0
    sameUpCountsForEM = 0
    sameDownCountsForES = 0
    sameDownCountsForEM = 0

    # 遍历每个日期
    for i in range(dayCounts - preDay):
        # 短期均线能量状态相同时
        if (needDataToMin['E_S'][i] >= 0 and needDataToDay['E_S'][i] >= 0) or (
                needDataToMin['E_S'][i] < 0 and needDataToDay['E_S'][i] < 0):
            # 记录短期均线能量状态相同时的数量
            sameCountsForES += 1
            # 记录短期均线能量状态相同时上涨和下跌的天数的数量
            if needDataToDay['endP'][i] >= needDataToDay['endP'][i + preDay]:
                sameDownCountsForES += 1
            else:
                sameUpCountsForES += 1
        # 短期均线能量状态互逆时
        else:
            # 记录短期均线能量状态互逆时的数量
            oppositeCountsForES += 1
            # 记录短期均线能量状态互逆时上涨和下跌的天数的数量
            if needDataToDay['endP'][i] >= needDataToDay['endP'][i + preDay]:
                oppositeDownCountsForES += 1
            else:
                oppositeUpCountsForES += 1
        # 中期均线能量状态相同时
        if (needDataToMin['E_M'][i] >= 0 and needDataToDay['E_M'][i] >= 0) or (
                needDataToMin['E_M'][i] < 0 and needDataToDay['E_M'][i] < 0):
            # 记录中期均线能量状态相同时的数量
            sameCountsForEM += 1
            # 记录中期均线能量状态相同时上涨和下跌的天数的数量
            if needDataToDay['endP'][i] >= needDataToDay['endP'][i + preDay]:
                sameDownCountsForEM += 1
            else:
                sameUpCountsForEM += 1
        # 中期均线能量状态互逆时
        else:
            # 记录中期均线能量状态互逆时的数量
            oppositeCountsForEM += 1
            # 记录中期均线能量状态互逆时上涨和下跌的天数的数量
            if needDataToDay['endP'][i] >= needDataToDay['endP'][i + preDay]:
                oppositeDownCountsForEM += 1
            else:
                oppositeUpCountsForEM += 1

    # 创建两个列表分别存放EM和ES统计的数据
    dataListForEM = []
    dataListForES = []
    # 循环两次 第一次放进去状态相同时相关的数据，第二次放入状态互逆时相关的数据
    for i in range(2):
        tempListForES = []
        tempListForEM = []
        if i == 0:
            tempListForES.append(sameCountsForES)
            tempListForES.append(sameUpCountsForES)
            tempListForES.append(sameDownCountsForES)
            dataListForES.append(tempListForES)

            tempListForEM.append(sameCountsForEM)
            tempListForEM.append(sameUpCountsForEM)
            tempListForEM.append(sameDownCountsForEM)
            dataListForEM.append(tempListForEM)
        else:
            tempListForES.append(oppositeCountsForES)
            tempListForES.append(oppositeUpCountsForES)
            tempListForES.append(oppositeDownCountsForES)
            dataListForES.append(tempListForES)

            tempListForEM.append(oppositeCountsForEM)
            tempListForEM.append(oppositeUpCountsForEM)
            tempListForEM.append(oppositeDownCountsForEM)
            dataListForEM.append(tempListForEM)

    return dataListForES, dataListForEM
