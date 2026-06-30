-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: supermarket
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `supermarket`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `supermarket` /*!40100 DEFAULT CHARACTER SET gb2312 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `supermarket`;

--
-- Table structure for table `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods` (
  `goods_id` int NOT NULL AUTO_INCREMENT COMMENT '商品唯一ID',
  `goods_name` varchar(100) NOT NULL COMMENT '商品名称',
  `goods_count` int NOT NULL DEFAULT '0' COMMENT '商品库存数量',
  `goods_price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `goods_category` varchar(50) NOT NULL COMMENT '商品分类',
  `goods_unit` varchar(10) NOT NULL COMMENT '计量单位',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='超市商品信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods`
--

LOCK TABLES `goods` WRITE;
/*!40000 ALTER TABLE `goods` DISABLE KEYS */;
INSERT INTO `goods` VALUES (1,'黑人牙膏',32,12.90,'日化用品','支','2026-06-11 16:43:50','薄荷味，200g装'),(2,'康师傅红烧牛肉面',99,4.50,'方便食品','桶','2026-06-11 16:43:50','经典款，143g装'),(7,'葡萄糖',99,5.00,'其他','个','2026-06-11 19:43:38',NULL),(8,'矿泉水',1,50.00,'其他','个','2026-06-17 11:09:04',NULL);
/*!40000 ALTER TABLE `goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale_order`
--

DROP TABLE IF EXISTS `sale_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale_order` (
  `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `sale_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收银时间',
  `total_money` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `receive_money` decimal(10,2) NOT NULL COMMENT '实收现金',
  `change_money` decimal(10,2) NOT NULL COMMENT '找零金额',
  `cashier_name` varchar(50) NOT NULL DEFAULT '收银员' COMMENT '收银员姓名',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale_order`
--

LOCK TABLES `sale_order` WRITE;
/*!40000 ALTER TABLE `sale_order` DISABLE KEYS */;
INSERT INTO `sale_order` VALUES (1,'2026-06-19 15:10:08',34.80,35.00,0.20,'收银员'),(2,'2026-06-19 15:13:08',92.00,100.00,8.00,'收银员'),(3,'2026-06-29 09:52:36',64.50,100.00,35.50,'收银员'),(4,'2026-06-29 10:01:22',69.00,69.00,0.00,'收银员'),(5,'2026-06-29 10:03:43',12.90,12.90,0.00,'收银员');
/*!40000 ALTER TABLE `sale_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale_order_item`
--

DROP TABLE IF EXISTS `sale_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale_order_item` (
  `item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL COMMENT '关联订单号',
  `goods_name` varchar(100) NOT NULL COMMENT '商品名称',
  `buy_num` int NOT NULL COMMENT '购买数量',
  `single_price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `sub_total` decimal(10,2) NOT NULL COMMENT '单品小计',
  PRIMARY KEY (`item_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `sale_order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `sale_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=gb2312;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale_order_item`
--

LOCK TABLES `sale_order_item` WRITE;
/*!40000 ALTER TABLE `sale_order_item` DISABLE KEYS */;
INSERT INTO `sale_order_item` VALUES (1,1,'黑人牙膏',2,12.90,25.80),(2,1,'康师傅红烧牛肉面',2,4.50,9.00),(3,2,'黑人牙膏',5,12.90,64.50),(4,2,'康师傅红烧牛肉面',5,4.50,22.50),(5,2,'葡萄糖',1,5.00,5.00),(6,3,'黑人牙膏',5,12.90,64.50),(7,4,'黑人牙膏',5,12.90,64.50),(8,4,'康师傅红烧牛肉面',1,4.50,4.50),(9,5,'黑人牙膏',1,12.90,12.90);
/*!40000 ALTER TABLE `sale_order_item` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-30 14:50:11
