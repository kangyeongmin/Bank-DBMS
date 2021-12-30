-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.6.5-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- bank 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `bank` /*!40100 DEFAULT CHARACTER SET utf8mb3 */;
USE `bank`;

-- 테이블 bank.account 구조 내보내기
CREATE TABLE IF NOT EXISTS `account` (
  `AccountNumber` int(11) NOT NULL,
  `UserSsn` bigint(20) NOT NULL,
  `Password` int(11) NOT NULL,
  `Balance` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`AccountNumber`),
  KEY `UserSsn` (`UserSsn`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`UserSsn`) REFERENCES `user` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 bank.account:~0 rows (대략적) 내보내기
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
/*!40000 ALTER TABLE `account` ENABLE KEYS */;

-- 테이블 bank.bank 구조 내보내기
CREATE TABLE IF NOT EXISTS `bank` (
  `BankID` int(11) NOT NULL,
  `Name` varchar(50) NOT NULL,
  `Location` varchar(15) NOT NULL,
  `Mnum` int(11) NOT NULL,
  PRIMARY KEY (`BankID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 bank.bank:~3 rows (대략적) 내보내기
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
INSERT INTO `bank` (`BankID`, `Name`, `Location`, `Mnum`) VALUES
	(1, '컴소은행사가정지점', '중랑구면목동', 1),
	(2, '컴소은행답십리지점', '동대문구답십리동', 1),
	(3, '컴소은행한양대지점', '성동구행당동', 0);
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;

-- 테이블 bank.history 구조 내보내기
CREATE TABLE IF NOT EXISTS `history` (
  `TimeandDate` varchar(50) NOT NULL,
  `Ussn` bigint(20) NOT NULL,
  `Money` int(11) NOT NULL,
  `Bal` int(11) NOT NULL,
  `TransactionType` varchar(10) NOT NULL,
  `AccountNum` int(11) NOT NULL,
  PRIMARY KEY (`TimeandDate`),
  KEY `Ussn` (`Ussn`),
  CONSTRAINT `history_ibfk_1` FOREIGN KEY (`Ussn`) REFERENCES `user` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 bank.history:~0 rows (대략적) 내보내기
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
/*!40000 ALTER TABLE `history` ENABLE KEYS */;

-- 테이블 bank.manager 구조 내보내기
CREATE TABLE IF NOT EXISTS `manager` (
  `ManagerID` int(11) NOT NULL,
  `Name` varchar(15) NOT NULL,
  `StartDate` int(11) NOT NULL,
  `BId` int(11) NOT NULL,
  PRIMARY KEY (`ManagerID`),
  KEY `BId` (`BId`),
  CONSTRAINT `manager_ibfk_1` FOREIGN KEY (`BId`) REFERENCES `bank` (`BankID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 bank.manager:~2 rows (대략적) 내보내기
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` (`ManagerID`, `Name`, `StartDate`, `BId`) VALUES
	(11025, '김이현', 20211203, 1),
	(30303, '박효준', 20201008, 3),
	(11111, '김영희', 20211102, 2);
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;

-- 테이블 bank.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `Ssn` bigint(20) NOT NULL,
  `Name` varchar(15) NOT NULL,
  `Sex` char(1) NOT NULL,
  `PhoneNumber` int(11) DEFAULT NULL,
  `ManagerId` int(11) NOT NULL,
  PRIMARY KEY (`Ssn`),
  KEY `ManagerId` (`ManagerId`),
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`ManagerId`) REFERENCES `manager` (`ManagerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 테이블 데이터 bank.user:~2 rows (대략적) 내보내기
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`Ssn`, `Name`, `Sex`, `PhoneNumber`, `ManagerId`) VALUES
	(4444444, '강영민', '여', 82226050, 11025),
	(5555555, '이철수', '남', 12345678, 11025);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
