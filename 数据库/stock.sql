DROP TABLE IF EXISTS `lstm_argument`;
CREATE TABLE `lstm_argument`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `lstm_layers` int(11) NOT NULL,
  `dense_layers` int(11) NOT NULL,
  `units` int(11) NOT NULL,
  `loss` double NOT NULL,
  `mape` double NOT NULL,
  `root_mean_squared_error` double NOT NULL,
  `use_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '使用状态（0表示未使用，1表示使用，只要有一个为1）',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型参数创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `random_forest_argument`;
CREATE TABLE `random_forest_argument`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `estimators_count` int(11) NOT NULL,
  `random_states` int(11) NOT NULL,
  `n_jobs` int(11) NOT NULL,
  `mean_absolute_error` double NOT NULL,
  `mean_squared_error` double NOT NULL,
  `root_mean_squared_error` double NOT NULL,
  `use_state` bit(1) NOT NULL DEFAULT b'0' COMMENT '使用状态（0表示未使用，1表示使用，只要有一个为1）',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型参数创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `stock_data`;
CREATE TABLE `stock_data`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票ID',
  `stock_date` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票日期',
  `start_price` double NOT NULL COMMENT '开盘价格',
  `high_price` double NOT NULL COMMENT '最高价格',
  `low_price` double NOT NULL COMMENT '最低价格',
  `end_price` double NOT NULL COMMENT '收盘价格',
  `deal_number` bigint(20) NOT NULL COMMENT '成交量',
  `deal_money` double NOT NULL COMMENT '成交额',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间（年-月-日）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `stock_minute_data`;
CREATE TABLE `stock_minute_data`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票ID',
  `stock_date` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票日期',
  `minute` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票日期',
  `start_price` double NOT NULL COMMENT '开盘价格',
  `high_price` double NOT NULL COMMENT '最高价格',
  `low_price` double NOT NULL COMMENT '最低价格',
  `end_price` double NOT NULL COMMENT '收盘价格',
  `deal_number` bigint(20) NOT NULL COMMENT '成交量',
  `deal_money` double NOT NULL COMMENT '成交额',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间（年-月-日）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `stock_minute_name`;
CREATE TABLE `stock_minute_name`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票ID',
  `stock_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票名称',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间（年-月-日）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `stock_name`;
CREATE TABLE `stock_name`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票ID',
  `stock_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '股票名称',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间（年-月-日）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `user_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户类型（普通用户、管理员）',
  `avatar_url` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户头像',
  `create_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建时间（年-月-日）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

INSERT INTO `user` VALUES (1, 'zhangsan', 'zhangsan', 'normal', 'https://ts4.cn.mm.bing.net/th?id=OIP-C.dB5gZRP1BczutWDG1iY3qgAAAA&w=250&h=250&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2', '2023-04-15');
INSERT INTO `user` VALUES (2, 'admin', 'admin', 'admin', 'https://ts3.cn.mm.bing.net/th?id=OIP-C.dD1idq2wwb8izj7vEOVGiwAAAA&w=250&h=250&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2', '2023-04-15');
