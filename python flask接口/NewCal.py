import xlwt
import os


def addStock(stockName, resPath, listOfLines):
    stockID = []
    everyDate = []
    startP = []
    highP = []
    lowP = []
    endP = []
    dealN = []
    dealM = []
    MA5 = []
    MA10 = []
    MA20 = []
    MA30 = []
    MA60 = []
    MA120 = []
    MA250 = []
    XL10 = []
    XL20 = []
    XL30 = []
    XL60 = []
    XL120 = []
    XL250 = []
    S1020 = []
    S3060 = []
    S120250 = []
    S1060 = []
    S30250 = []
    S10250 = []

    priceChange = []
    sortGoldDead1020 = []
    sortGoldDead3060 = []
    sortGoldDead120250 = []

    E10List = [-1, -0.02, -0.01, 0, 0.01, 0.02, 1]
    E10ListValue = [-3, -2, -1, 1, 2, 3]
    EList = [-1, -0.015, -0.005, 0, 0.005, 0.015, 1]
    E20ListValue = [-4, -3, -2, 2, 3, 4]
    E30ListValue = [-5, -4, -3, 3, 4, 5]
    E60ListValue = [-6, -5, -4, 4, 5, 6]
    E120ListValue = [-7, -6, -5, 5, 6, 7]
    E250ListValue = [-8, -7, -6, 6, 7, 8]

    E10 = []
    E20 = []
    E30 = []
    E60 = []
    E120 = []
    E250 = []

    SList = [-1, -0.12, -0.06, -0.03, 0, 0.03, 0.06, 0.12, 1]
    SListValue = [-1, -2, -3, -4, 0, 4, 3, 2, 1]

    SS1020 = []
    SS3060 = []
    SS120250 = []
    SS1060 = []
    SS30250 = []
    SS10250 = []

    E_S = []
    E_M = []
    E_L = []
    E_SM = []
    E_ML = []
    E_SML = []

    listOf5 = []
    listOf10 = []
    listOf20 = []
    listOf30 = []
    listOf60 = []
    listOf120 = []
    listOf250 = []

    for i in range(len(listOfLines) - 1):
        if i == 0:
            stockID.append(listOfLines[i][0:6])
            continue
        if i == 1:
            continue
        line = listOfLines[i].split("\t")
        lastLine = listOfLines[i - 1].split("\t")
        everyDate.append(line[0])
        startP.append(line[1])
        highP.append(line[2])
        lowP.append(line[3])
        endP.append(line[4])
        dealN.append(line[5])
        dealM.append(line[6])
        listOf5.append(float(line[4]))
        listOf10.append(float(line[4]))
        listOf20.append(float(line[4]))
        listOf30.append(float(line[4]))
        listOf60.append(float(line[4]))
        listOf120.append(float(line[4]))
        listOf250.append(float(line[4]))

        if i > 2:
            priceChange.append(float(line[4]) - float(lastLine[4]))

        if i < 6:
            continue
        sumOf5 = 0
        for j in range(5):
            sumOf5 += listOf5[i - j - 2]
        MA5.append(round(float(sumOf5 / 5), 2))

        if i < 11:
            continue
        sumOf10 = 0
        for j in range(10):
            sumOf10 += listOf10[i - j - 2]
        MA10.append(round(float(sumOf10 / 10), 2))

        if i < 21:
            continue
        sumOf20 = 0
        for j in range(20):
            sumOf20 += listOf20[i - j - 2]
        MA20.append(round(float(sumOf20 / 20), 2))

        if round(float(sumOf10 / 10), 2) > round(float(sumOf20 / 20), 2):
            sortGoldDead1020.append(1)
        else:
            sortGoldDead1020.append(0)

        if i < 31:
            continue
        sumOf30 = 0
        for j in range(30):
            sumOf30 += listOf30[i - j - 2]
        MA30.append(round(float(sumOf30 / 30), 2))

        if i < 61:
            continue
        sumOf60 = 0
        for j in range(60):
            sumOf60 += listOf60[i - j - 2]
        MA60.append(round(float(sumOf60 / 60), 2))

        if round(float(sumOf30 / 30), 2) > round(float(sumOf60 / 60), 2):
            sortGoldDead3060.append(1)
        else:
            sortGoldDead3060.append(0)

        if i < 121:
            continue
        sumOf120 = 0
        for j in range(120):
            sumOf120 += listOf120[i - j - 2]
        MA120.append(round(float(sumOf120 / 120), 2))

        if i < 251:
            continue
        sumOf250 = 0
        for j in range(250):
            sumOf250 += listOf250[i - j - 2]
        MA250.append(round(float(sumOf250 / 250), 2))

        if round(float(sumOf120 / 120), 2) > round(float(sumOf250 / 250), 2):
            sortGoldDead120250.append(1)
        else:
            sortGoldDead120250.append(0)

    def getSectionValue(value, list, valueList):
        if value == 0:
            return 0
        for index in range(1, len(list)):
            if list[index - 1] < value < list[index]:
                return valueList[index - 1]
        return 0

    # 计算斜率
    for i in range(len(MA10)):
        if i < 2:
            continue
        XL10.append(round(MA10[i] / MA10[i - 2] - 1, 5))
        E10.append(getSectionValue(round(MA10[i] / MA10[i - 2] - 1, 5), E10List, E10ListValue))

    for i in range(len(MA20)):
        if i < 2:
            continue
        XL20.append(round(MA20[i] / MA20[i - 2] - 1, 5))
        E20.append(getSectionValue(round(MA20[i] / MA20[i - 2] - 1, 5), EList, E20ListValue))

    for i in range(len(MA30)):
        if i < 2:
            continue
        XL30.append(round(MA30[i] / MA30[i - 2] - 1, 5))
        E30.append(getSectionValue(round(MA30[i] / MA30[i - 2] - 1, 5), EList, E30ListValue))

    for i in range(len(MA60)):
        if i < 2:
            continue
        XL60.append(round(MA60[i] / MA60[i - 2] - 1, 5))
        E60.append(getSectionValue(round(MA60[i] / MA60[i - 2] - 1, 5), EList, E60ListValue))

    for i in range(len(MA120)):
        if i < 2:
            continue
        XL120.append(round(MA120[i] / MA120[i - 2] - 1, 5))
        E120.append(getSectionValue(round(MA120[i] / MA120[i - 2] - 1, 5), EList, E120ListValue))

    for i in range(len(MA250)):
        if i < 2:
            continue
        XL250.append(round(MA250[i] / MA250[i - 2] - 1, 5))
        E250.append(getSectionValue(round(MA250[i] / MA250[i - 2] - 1, 5), EList, E250ListValue))

    # 计算距离
    for i in range(len(MA20)):
        S1020.append(round(1 - MA20[i] / MA10[i + 10], 5))
        SS1020.append(getSectionValue(round(1 - MA20[i] / MA10[i + 10], 5), SList, SListValue))

    for i in range(len(MA60)):
        S3060.append(round(1 - MA60[i] / MA30[i + 30], 5))
        S1060.append(round(1 - MA60[i] / MA10[i + 50], 5))
        SS3060.append(getSectionValue(round(1 - MA60[i] / MA30[i + 30], 5), SList, SListValue))
        SS1060.append(getSectionValue(round(1 - MA60[i] / MA10[i + 50], 5), SList, SListValue))

    for i in range(len(MA250)):
        S120250.append(round(1 - MA250[i] / MA120[i + 130], 5))
        S30250.append(round(1 - MA250[i] / MA30[i + 220], 5))
        S10250.append(round(1 - MA250[i] / MA10[i + 240], 5))
        SS120250.append(getSectionValue(round(1 - MA250[i] / MA120[i + 130], 5), SList, SListValue))
        SS30250.append(getSectionValue(round(1 - MA250[i] / MA30[i + 220], 5), SList, SListValue))
        SS10250.append(getSectionValue(round(1 - MA250[i] / MA10[i + 240], 5), SList, SListValue))

    # 计算均线能量
    for i in range(len(E20)):
        E_S.append(E10[i + 10] + E20[i] + SS1020[i + 2])

    for i in range(len(E60)):
        E_M.append(E30[i + 30] + E60[i] + SS3060[i + 2])
        E_SM.append(E_S[i + 40] + E_M[i] + SS1060[i + 2])

    for i in range(len(E250)):
        E_L.append(E120[i + 130] + E250[i] + SS120250[i + 2])
        E_ML.append(E_M[i + 190] + E_L[i] + SS30250[i + 2])
        E_SML.append(E_S[i + 230] + E_M[i + 190] + E_L[i] + SS10250[i + 2])

    workbook = xlwt.Workbook(encoding='utf-8')
    sheet = workbook.add_sheet("student_sheet")

    sheet.write(0, 0, 'stockID')
    sheet.write(0, 1, 'everyDate')
    sheet.write(0, 2, 'startP')
    sheet.write(0, 3, 'highP')
    sheet.write(0, 4, 'lowP')
    sheet.write(0, 5, 'endP')
    sheet.write(0, 6, 'dealN')
    sheet.write(0, 7, 'dealM')
    sheet.write(0, 8, 'MA5')
    sheet.write(0, 9, 'MA10')
    sheet.write(0, 10, 'MA20')
    sheet.write(0, 11, 'MA30')
    sheet.write(0, 12, 'MA60')
    sheet.write(0, 13, 'MA120')
    sheet.write(0, 14, 'MA250')
    sheet.write(0, 15, 'XL10')
    sheet.write(0, 16, 'XL20')
    sheet.write(0, 17, 'XL30')
    sheet.write(0, 18, 'XL60')
    sheet.write(0, 19, 'XL120')
    sheet.write(0, 20, 'XL250')
    sheet.write(0, 21, 'S1020')
    sheet.write(0, 22, 'S3060')
    sheet.write(0, 23, 'S120250')
    sheet.write(0, 24, 'S1060')
    sheet.write(0, 25, 'S30250')
    sheet.write(0, 26, 'S10250')

    sheet.write(0, 27, 'PriceChange')
    sheet.write(0, 28, 'sortGoldDead1020')
    sheet.write(0, 29, 'sortGoldDead3060')
    sheet.write(0, 30, 'sortGoldDead120250')

    sheet.write(0, 31, 'E10')
    sheet.write(0, 32, 'E20')
    sheet.write(0, 33, 'E30')
    sheet.write(0, 34, 'E60')
    sheet.write(0, 35, 'E120')
    sheet.write(0, 36, 'E250')

    sheet.write(0, 37, 'SS1020')
    sheet.write(0, 38, 'SS3060')
    sheet.write(0, 39, 'SS120250')
    sheet.write(0, 40, 'SS1060')
    sheet.write(0, 41, 'SS30250')
    sheet.write(0, 42, 'SS10250')

    sheet.write(0, 43, 'E_S')
    sheet.write(0, 44, 'E_M')
    sheet.write(0, 45, 'E_L')
    sheet.write(0, 46, 'E_SM')
    sheet.write(0, 47, 'E_ML')
    sheet.write(0, 48, 'E_SML')

    for i in range(len(everyDate)):
        if i < 251:
            continue
        sheet.write(i + 1 - 251, 0, stockID[0])
        sheet.write(i + 1 - 251, 1, everyDate[i])
        sheet.write(i + 1 - 251, 2, startP[i])
        sheet.write(i + 1 - 251, 3, highP[i])
        sheet.write(i + 1 - 251, 4, lowP[i])
        sheet.write(i + 1 - 251, 5, endP[i])
        sheet.write(i + 1 - 251, 6, dealN[i])
        sheet.write(i + 1 - 251, 7, dealM[i])

        sheet.write(i + 1 - 251, 8, MA5[i - 4])
        sheet.write(i + 1 - 251, 9, MA10[i - 9])
        sheet.write(i + 1 - 251, 10, MA20[i - 19])
        sheet.write(i + 1 - 251, 11, MA30[i - 29])
        sheet.write(i + 1 - 251, 12, MA60[i - 59])
        sheet.write(i + 1 - 251, 13, MA120[i - 119])
        sheet.write(i + 1 - 251, 14, MA250[i - 249])
        sheet.write(i + 1 - 251, 15, XL10[i - 11])
        sheet.write(i + 1 - 251, 16, XL20[i - 21])
        sheet.write(i + 1 - 251, 17, XL30[i - 31])
        sheet.write(i + 1 - 251, 18, XL60[i - 61])
        sheet.write(i + 1 - 251, 19, XL120[i - 121])
        sheet.write(i + 1 - 251, 20, XL250[i - 251])
        sheet.write(i + 1 - 251, 21, S1020[i - 19])
        sheet.write(i + 1 - 251, 22, S3060[i - 59])
        sheet.write(i + 1 - 251, 23, S120250[i - 249])
        sheet.write(i + 1 - 251, 24, S1060[i - 59])
        sheet.write(i + 1 - 251, 25, S30250[i - 249])
        sheet.write(i + 1 - 251, 26, S10250[i - 249])

        sheet.write(i + 1 - 251, 27, priceChange[i - 1])
        sheet.write(i + 1 - 251, 28, sortGoldDead1020[i - 19])
        sheet.write(i + 1 - 251, 29, sortGoldDead3060[i - 59])
        sheet.write(i + 1 - 251, 30, sortGoldDead120250[i - 249])

        sheet.write(i + 1 - 251, 31, E10[i - 11])
        sheet.write(i + 1 - 251, 32, E20[i - 21])
        sheet.write(i + 1 - 251, 33, E30[i - 31])
        sheet.write(i + 1 - 251, 34, E60[i - 61])
        sheet.write(i + 1 - 251, 35, E120[i - 121])
        sheet.write(i + 1 - 251, 36, E250[i - 251])

        sheet.write(i + 1 - 251, 37, SS1020[i - 19])
        sheet.write(i + 1 - 251, 38, SS3060[i - 59])
        sheet.write(i + 1 - 251, 39, SS120250[i - 249])
        sheet.write(i + 1 - 251, 40, SS1060[i - 59])
        sheet.write(i + 1 - 251, 41, SS30250[i - 249])
        sheet.write(i + 1 - 251, 42, SS10250[i - 249])

        sheet.write(i + 1 - 251, 43, E_S[i - 21])
        sheet.write(i + 1 - 251, 44, E_M[i - 61])
        sheet.write(i + 1 - 251, 45, E_L[i - 251])
        sheet.write(i + 1 - 251, 46, E_SM[i - 61])
        sheet.write(i + 1 - 251, 47, E_ML[i - 251])
        sheet.write(i + 1 - 251, 48, E_SML[i - 251])

    folder = os.path.exists(resPath)
    if not folder:
        os.makedirs(resPath)

    workbook.save(resPath + stockName + '.xls')
