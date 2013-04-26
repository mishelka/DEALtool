-- -----------------------------------------------------
-- Table `ALLOCATION`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ALLOCATION` ;

CREATE  TABLE IF NOT EXISTS `ALLOCATION` (
  `APPOINTMENT_ID` INT NOT NULL ,
  `RESOURCE_ID` INT NOT NULL,
  `OPTIONAL` INT
  );

-- -----------------------------------------------------
-- Table `APPOINTMENT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `APPOINTMENT` ;

CREATE  TABLE IF NOT EXISTS `APPOINTMENT` (
   `ID` INT NOT NULL
  ,`EVENT_ID` INT NOT NULL
  ,`APPOINTMENT_START`   DATETIME       NOT NULL
  ,`APPOINTMENT_END`     DATETIME       NOT NULL
  ,`REPETITION_TYPE`     VARCHAR(15)    NULL DEFAULT NULL
  ,`REPETITION_NUMBER`   INT            NULL DEFAULT NULL
  ,`REPETITION_END`      DATETIME       NULL DEFAULT NULL
  ,`REPETITION_INTERVAL` INT            NULL DEFAULT NULL  
  ,PRIMARY KEY (`ID`) );


-- -----------------------------------------------------
-- Table `APPOINTMENT_EXCEPTION`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `APPOINTMENT_EXCEPTION` ;

CREATE  TABLE IF NOT EXISTS `APPOINTMENT_EXCEPTION` (
  `APPOINTMENT_ID` INT NOT NULL ,
  `EXCEPTION_DATE` DATETIME NOT NULL );


-- -----------------------------------------------------
-- Table `CATEGORY`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `CATEGORY` ;

CREATE  TABLE IF NOT EXISTS `CATEGORY` (
  `ID` INT NOT NULL ,
  `PARENT_ID` INT NULL DEFAULT NULL ,
  `CATEGORY_KEY` VARCHAR(50) NOT NULL ,
  `LABEL` VARCHAR(250) NULL DEFAULT NULL ,
  `DEFINITION` TEXT NULL DEFAULT NULL ,
  `PARENT_ORDER` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) );


-- -----------------------------------------------------
-- Table `DYNAMIC_TYPE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `DYNAMIC_TYPE` ;

CREATE  TABLE IF NOT EXISTS `DYNAMIC_TYPE` (
  `ID` INT NOT NULL ,
  `TYPE_KEY` VARCHAR(50) NOT NULL ,
  `DEFINITION` TEXT NOT NULL ,
  PRIMARY KEY (`ID`) );


-- -----------------------------------------------------
-- Table `EVENT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `EVENT` ;

CREATE  TABLE IF NOT EXISTS `EVENT` (
  `ID` INT NOT NULL ,
  `TYPE_KEY` VARCHAR(50) NOT NULL ,
  `OWNER_ID` INT NOT NULL ,
  `CREATION_TIME` DATETIME ,
  `LAST_CHANGED` DATETIME ,
  `LAST_CHANGED_BY` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) );


-- -----------------------------------------------------
-- Table `EVENT_ATTRIBUTE_VALUE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `EVENT_ATTRIBUTE_VALUE` ;

CREATE  TABLE IF NOT EXISTS `EVENT_ATTRIBUTE_VALUE` (
  `EVENT_ID` INT NOT NULL ,
  `ATTRIBUTE_KEY` VARCHAR(20) NOT NULL ,
  `VALUE` VARCHAR(1000) NULL DEFAULT NULL );


-- -----------------------------------------------------
-- Table `PERIOD`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PERIOD` ;

CREATE  TABLE IF NOT EXISTS `PERIOD` (
  `ID` INT NOT NULL ,
  `NAME` VARCHAR(255) NOT NULL ,
  `PERIOD_START` DATETIME NOT NULL ,
  `PERIOD_END` DATETIME NOT NULL ,
  PRIMARY KEY (`ID`) );


-- -----------------------------------------------------
-- Table `PERMISSION`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PERMISSION` ;

CREATE  TABLE IF NOT EXISTS `PERMISSION` (
  `RESOURCE_ID` INT NOT NULL ,
  `USER_ID` INT NULL DEFAULT NULL ,
  `GROUP_ID` INT NULL DEFAULT NULL ,
  `ACCESS_LEVEL` INT NOT NULL ,
  `MIN_ADVANCE` INT NULL DEFAULT NULL ,
  `MAX_ADVANCE` INT NULL DEFAULT NULL ,
  `START_DATE` DATETIME NULL DEFAULT NULL ,
  `END_DATE` DATETIME NULL DEFAULT NULL );


-- -----------------------------------------------------
-- Table `PREFERENCE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `PREFERENCE` ;

CREATE  TABLE IF NOT EXISTS `PREFERENCE` (
  `USER_ID` INT NULL DEFAULT NULL ,
  `ROLE` VARCHAR(200) NOT NULL ,
  `STRING_VALUE` VARCHAR(1000) NULL DEFAULT NULL ,
  `XML_VALUE` MEDIUMTEXT NULL DEFAULT NULL );


-- -----------------------------------------------------
-- Table `RAPLA_USER`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RAPLA_USER` ;

CREATE  TABLE IF NOT EXISTS `RAPLA_USER` (
  `ID` INT NOT NULL ,
  `USERNAME` VARCHAR(30) NOT NULL ,
  `PASSWORD` VARCHAR(130) NULL DEFAULT NULL ,
  `NAME` VARCHAR(200) NOT NULL ,
  `EMAIL` VARCHAR(150) NOT NULL ,
  `ISADMIN` INT NOT NULL ,
  PRIMARY KEY (`ID`) );


-- -----------------------------------------------------
-- Table `RAPLA_USER_GROUP`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RAPLA_USER_GROUP` ;

CREATE  TABLE IF NOT EXISTS `RAPLA_USER_GROUP` (
  `USER_ID` INT NOT NULL ,
  `CATEGORY_ID` INT NOT NULL );


-- -----------------------------------------------------
-- Table `RAPLA_RESOURCE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RAPLA_RESOURCE` ;

CREATE  TABLE IF NOT EXISTS `RAPLA_RESOURCE` (
  `ID` INT NOT NULL ,
  `TYPE_KEY` VARCHAR(100) NOT NULL ,
  `IGNORE_CONFLICTS` INT NOT NULL ,
  `OWNER_ID` INT,
  `CREATION_TIME` DATETIME ,
  `LAST_CHANGED` DATETIME,
  `LAST_CHANGED_BY` INT NULL DEFAULT NULL ,
  PRIMARY KEY (`ID`) );



-- -----------------------------------------------------
-- Table `RESOURCE_ATTRIBUTE_VALUE`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RESOURCE_ATTRIBUTE_VALUE` ;

CREATE  TABLE IF NOT EXISTS `RESOURCE_ATTRIBUTE_VALUE` (
  `RESOURCE_ID` INT NOT NULL ,
  `ATTRIBUTE_KEY` VARCHAR(25) NULL DEFAULT NULL ,
  `VALUE` VARCHAR(1000) NULL DEFAULT NULL );