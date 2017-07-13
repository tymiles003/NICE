DROP TABLE IF EXISTS `Attendances`;
CREATE TABLE `Attendances` (
  `attendanceid` INT(11)       NOT NULL AUTO_INCREMENT,
  `datetime`     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `studentid`    INT(11)       NOT NULL,
  `crn`          DECIMAL(5, 0) NOT NULL,
  PRIMARY KEY (`attendanceid`)
);
DROP TABLE IF EXISTS `Registrations`;
CREATE TABLE `Registrations` (
  `registrationid` BIGINT        NOT NULL AUTO_INCREMENT,
  `datetime`       TIMESTAMP     NULL     DEFAULT CURRENT_TIMESTAMP,
  `studentid`      INT(11)       NOT NULL,
  `crn`            DECIMAL(5, 0) NOT NULL,
  PRIMARY KEY (`registrationid`)
);
DROP TABLE IF EXISTS `Courses`;
CREATE TABLE `Courses` (
  `crn`     DECIMAL(5, 0) NOT NULL,
  `name`    VARCHAR(45)   DEFAULT NULL,
  `number`  VARCHAR(45)   DEFAULT NULL,
  `section` DECIMAL(2, 0) DEFAULT NULL,
  `m_start` TIME          DEFAULT NULL,
  `m_end`   TIME          DEFAULT NULL,
  `t_start` TIME          DEFAULT NULL,
  `t_end`   TIME          DEFAULT NULL,
  `w_start` TIME          DEFAULT NULL,
  `w_end`   TIME          DEFAULT NULL,
  `r_start` TIME          DEFAULT NULL,
  `r_end`   TIME          DEFAULT NULL,
  `f_start` TIME          DEFAULT NULL,
  `f_end`   TIME          DEFAULT NULL,
  `s_start` TIME          DEFAULT NULL,
  `s_end`   TIME          DEFAULT NULL,
  `u_start` TIME          DEFAULT NULL,
  `u_end`   TIME          DEFAULT NULL,
  PRIMARY KEY (`crn`)
);
DROP TABLE IF EXISTS `Students`;
CREATE TABLE `Students` (
  `studentid`  INT(11) NOT NULL,
  `firstname`  VARCHAR(255) DEFAULT NULL,
  `middlename` VARCHAR(255) DEFAULT NULL,
  `lastname`   VARCHAR(255) DEFAULT NULL,
  `email`      VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`studentid`)
);
ALTER TABLE `Attendances`
  ADD CONSTRAINT `crn_fk2` FOREIGN KEY (`crn`) REFERENCES `Courses` (`crn`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Attendances`
  ADD CONSTRAINT `studentid_fk2` FOREIGN KEY (`studentid`) REFERENCES `Students` (`studentid`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Registrations`
  ADD CONSTRAINT `crn_fk` FOREIGN KEY (`crn`) REFERENCES `Courses` (`crn`) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `Registrations`
  ADD CONSTRAINT `studentid_fk` FOREIGN KEY (`studentid`) REFERENCES `Students` (`studentid`) ON DELETE CASCADE ON UPDATE CASCADE;
