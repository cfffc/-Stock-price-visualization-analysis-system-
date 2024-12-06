import pandas as pd


def generateMinuteMAData(resPath, stockName, startDate, endDate):
    data = pd.read_excel(resPath + stockName + '.xls')

    startIndex = 0
    endIndex = 0
    findStartFlag = False
    findEndFlag = False

    dataList = data['everyDate']
    for i in range(len(dataList)):
        if startDate <= dataList[i] and not findStartFlag:
            startIndex = i
            findStartFlag = True
        if endDate <= dataList[i] and not findEndFlag:
            endIndex = i
            findEndFlag = True

    MA = data[['everyDate', 'MA5', 'MA10', 'MA20', 'MA30', 'MA60', 'MA120', 'MA250', 'minute']]
    if not findStartFlag:
        return []
    elif not findEndFlag:
        MAList = MA[startIndex:].values.tolist()
    else:
        if endDate == dataList[endIndex]:
            MAList = MA[startIndex:endIndex + 1].values.tolist()
        else:
            MAList = MA[startIndex:endIndex].values.tolist()
    return MAList
