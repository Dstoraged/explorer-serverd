/*
Navicat MySQL Data Transfer

Source Server         : 192.168.9.101
Source Server Version : 50717
Source Host           : 192.168.9.101:3306
Source Database       : chainexplorer

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2021-11-11 10:35:23
*/

set foreign_key_checks=0;

-- ----------------------------
-- Table structure for `addr_balances`
-- ----------------------------
drop table if exists `addr_balances`;
create table `addr_balances` (
`id`  bigint(20) not null auto_increment ,
`address`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`blocknumber`  bigint(20) not null default 0  ,
`balance`  decimal(65,0) not null default 0 ,
`nonce`  bigint(20) not null default 0  ,
primary key (`id`),
unique index `unique` (`address`, `contract`, `blocknumber`) using btree ,
index `address` (`address`) using btree ,
index `contract` (`contract`) using btree ,
index `blocknumber` (`blocknumber`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Changes in historical balance'
auto_increment=1452204

;

-- ----------------------------
-- Table structure for `addresses`
-- ----------------------------
drop table if exists `addresses`;
create table `addresses` (
`id`  bigint(20) not null auto_increment ,
`address`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null,
`balance`  decimal(65,0) not null default 0  ,
`nonce`  bigint(20) not null default 0 ,
`blocknumber`  bigint(20) not null default 0 ,
`inserted_time`  datetime null default null  ,
`haslock`  int(4) null default null  ,
`srt_balance`  decimal(65,0) null default 0 ,
`srt_nonce`  bigint(20) null default 0,
`isinner` int(11)   null default 0 ,
primary key (`id`),
unique index `unique` (`address`, `contract`) using btree ,
index `address` (`address`) using btree ,
index `contract` (`contract`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Wallet address information'
auto_increment=922258

;

-- ----------------------------
-- Table structure for `addresses_token`
-- ----------------------------
drop table if exists `addresses_token`;
create table `addresses_token` (
`id`  bigint(20) not null auto_increment ,
`address`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null,
`balance`  decimal(65,0) not null default 0 ,
`nonce`  bigint(20) not null default 0 ,
`blocknumber`  bigint(20) not null default 0 ,
`inserted_time`  datetime null default null ,
primary key (`id`),
unique index `unique` (`address`, `contract`) using btree ,
index `address` (`address`) using btree ,
index `contract` (`contract`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Token transaction wallet address information'
auto_increment=415

;

-- ----------------------------
-- Table structure for `addrfriendly`
-- ----------------------------
drop table if exists `addrfriendly`;
create table `addrfriendly` (
`address`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`aliasname`  varchar(255) character set utf8 collate utf8_general_ci not null,
`iscandidate`  tinyint(4) not null default 0 ,
`metadata`  json null default null  ,
primary key (`address`),
index `candidate` (`iscandidate`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Address friendly name'

;

-- ----------------------------
-- Table structure for `api_config`
-- ----------------------------
drop table if exists `api_config`;
create table `api_config` (
`id`  bigint(11) not null auto_increment ,
`config_key`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`config_memo`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`description`  varchar(255) character set utf8 collate utf8_bin null default null,
`config_value`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`updated_by`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`type`  tinyint(2) null default null comment 'type(0:input  2:response )' ,
`fokid`  bigint(11) null default null ,
`valuesconf`  json null default null ,
`code`  bigint(11) null default null ,
`resultdesc`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`isshow`  bigint(20) null default null ,
primary key (`id`),
foreign key (`fokid`) references `api_configplat` (`id`) on delete restrict on update restrict,
index `fokid` (`fokid`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='api parameter configuration sub-table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `api_configplat`
-- ----------------------------
drop table if exists `api_configplat`;
create table `api_configplat` (
`id`  bigint(11) not null auto_increment ,
`group_name`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`url`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
`descion`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`method`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`action`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`method_name`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`types`  bigint(2) null default null ,
primary key (`id`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='api parameter configuration main table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `api_configplat_en`
-- ----------------------------
drop table if exists `api_configplat_en`;
create table `api_configplat_en` (
`id`  bigint(11) not null auto_increment ,
`group_name`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
`url`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`descion`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`method`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`action`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`method_name`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`types`  bigint(2) null default null ,
primary key (`id`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='api parameter configuration main table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `block_fork`
-- ----------------------------
drop table if exists `block_fork`;
create table `block_fork` (
`id`  bigint(20) not null auto_increment ,
`nephewhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`nephewnumber`  bigint(20) not null default 0  ,
`istrunk`  tinyint(4) not null default 0  ,
`unclehash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`unclehandled`  tinyint(4) not null default 0 ,
primary key (`id`),
unique index `unique` (`nephewhash`, `unclehash`) using btree ,
index `nephewhash` (`nephewhash`) using btree ,
index `nephewnumber` (`istrunk`, `nephewnumber`) using btree ,
index `unclehash` (`unclehash`) using btree ,
index `unclehandled` (`unclehandled`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Block fork information'
auto_increment=1

;

-- ----------------------------
-- Table structure for `block_rewards`
-- ----------------------------
drop table if exists `block_rewards`;
create table `block_rewards` (
`id`  bigint(20) not null auto_increment ,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`blocknumber`  bigint(20) null default null  ,
`istrunk`  tinyint(4) not null default 0  ,
`address`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`rewardtype`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`rewardhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`reward`  decimal(65,0) not null default 0  ,
`timestamp`  datetime null default null  ,
primary key (`id`),
unique index `unique` (`blockhash`, `address`, `rewardtype`, `rewardhash`) using btree ,
index `blockhash` (`blockhash`) using btree ,
index `blocknumber` (`istrunk`) using btree ,
index `address` (`address`) using btree ,
index `rewardtype` (`rewardtype`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Block reward'
auto_increment=288063

;

-- ----------------------------
-- Table structure for `blocks`
-- ----------------------------
drop table if exists `blocks`;
create table `blocks` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`blocknumber`  bigint(20) null default null ,
`istrunk`  tinyint(4) not null default 0  ,
`timestamp`  datetime not null ,
`mineraddress`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`blocksize`  int(11) not null default 0 ,
`gaslimit`  bigint(20) not null default 0 ,
`gasused`  bigint(20) not null default 0  ,
`reward`  decimal(65,0) not null default 0  ,
`txscount`  int(11) not null default 0  ,
`nonce`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`difficulty`  bigint(20) null default null  ,
`totaldifficulty`  bigint(20) null default null  ,
`parenthash`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
`round`  int(11) null default 0 ,
primary key (`hash`),
index `blocktime` (`timestamp`, `istrunk`) using btree ,
index `blocknumber` (`blocknumber`, `istrunk`) using btree ,
index `mineraddress` (`mineraddress`, `istrunk`) using btree ,
index `istrunk` (`istrunk`, `blocknumber`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Block information'

;

-- ----------------------------
-- Table structure for `contractdecompiled`
-- ----------------------------
drop table if exists `contractdecompiled`;
create table `contractdecompiled` (
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`version`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`sourcecode`  text character set utf8 collate utf8_general_ci null  ,
primary key (`contract`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Smart contract decompilation information'

;

-- ----------------------------
-- Table structure for `contracts`
-- ----------------------------
drop table if exists `contracts`;
create table `contracts` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`istoken`  tinyint(4) not null default 0  ,
primary key (`hash`),
index `istoken` (`istoken`) using btree ,
index `transhash` (`transhash`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Smart contract list'

;

-- ----------------------------
-- Table structure for `contractverified`
-- ----------------------------
drop table if exists `contractverified`;
create table `contractverified` (
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`name`  varchar(255) character set utf8 collate utf8_general_ci not null ,
`version`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`optimization`  tinyint(4) not null  ,
`abi`  json not null  ,
`sourcecode`  text character set utf8 collate utf8_general_ci null  ,
primary key (`contract`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Smart contract verification information'

;

-- ----------------------------
-- Table structure for `dposcandidate`
-- ----------------------------
drop table if exists `dposcandidate`;
create table `dposcandidate` (
`id`  bigint(20) not null auto_increment ,
`loopstarttime`  datetime not null  ,
`candidate`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`tally`  decimal(65,0) not null  ,
`state`  int(11) not null ,
primary key (`id`),
index `loopstarttime` (`loopstarttime`) using btree ,
index `candidate` (`candidate`) using btree ,
index `unique` (`loopstarttime`, `candidate`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Candidate list'
auto_increment=1

;

-- ----------------------------
-- Table structure for `dposdeclare`
-- ----------------------------
drop table if exists `dposdeclare`;
create table `dposdeclare` (
`id`  bigint(20) not null auto_increment ,
`proposal`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`declarer`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`decision`  tinyint(4) not null ,
primary key (`id`),
unique index `unique` (`proposal`, `declarer`) using btree ,
index `proposal` (`proposal`) using btree ,
index `declarer` (`declarer`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Suggested voting status'
auto_increment=1

;

-- ----------------------------
-- Table structure for `dposhardware`
-- ----------------------------
drop table if exists `dposhardware`;
create table `dposhardware` (
`address`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`processor`  decimal(10,3) not null default 0.000  ,
`memory`  decimal(10,3) not null default 0.000  ,
`storage`  decimal(10,3) not null default 0.000  ,
`bandwidth`  decimal(10,3) not null default 0.000  ,
`introduction`  varchar(4096) character set utf8 collate utf8_general_ci null default null ,
primary key (`address`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Mining machine hardware configuration'

;

-- ----------------------------
-- Table structure for `dposnode`
-- ----------------------------
drop table if exists `dposnode`;
create table `dposnode` (
`id`  bigint(20) not null auto_increment ,
`voter`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`stake`  decimal(65,0) not null  ,
`blocknumber`  bigint(20) not null  ,
`round`  int(11) null default 0 ,
`type`  int(11) null default 0 ,
primary key (`id`),
unique index `unique` (`stake`, `voter`, `blocknumber`) using btree ,
index `voter` (`voter`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Node data'
auto_increment=64

;

-- ----------------------------
-- Table structure for `dposproposal`
-- ----------------------------
drop table if exists `dposproposal`;
create table `dposproposal` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`receivednumber`  bigint(20) not null  ,
`proposaltype`  int(11) not null ,
`proposer`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`currentdeposit`  decimal(65,0) not null  ,
`validationloopcnt`  int(11) not null  ,
`decisioncount`  int(11) not null default 0 ,
`targetaddress`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
`minerreward`  int(11) null default null  ,
`voterbalance`  decimal(65,0) null default null  ,
`proposaldeposit`  decimal(65,0) null default null ,
`schash`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`scblockcount`  int(11) null default null  ,
`scblockreward`  decimal(65,0) null default null ,
`screntfee`  decimal(65,0) null default null ,
`screntrate`  decimal(65,0) null default null ,
`screntlength`  int(11) null default null  ,
primary key (`hash`),
index `receivenumber` (`receivednumber`) using btree ,
index `proposaltype` (`proposaltype`) using btree ,
index `proposer` (`proposer`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Proposal list'

;

-- ----------------------------
-- Table structure for `dposrepresentatives`
-- ----------------------------
drop table if exists `dposrepresentatives`;
create table `dposrepresentatives` (
`address`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`state`  smallint(6) not null default 0  ,
`level`  smallint(6) not null default 0 ,
`allowvoted`  tinyint(4) not null default 0  ,
`country`  varchar(50) character set utf8 collate utf8_general_ci null default null  ,
`city`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`location`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`geo`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`tags`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
primary key (`address`),
index `state` (`state`) using btree ,
index `level` (`level`) using btree ,
index `allowvoted` (`allowvoted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Candidate Profile'

;

-- ----------------------------
-- Table structure for `dpossigners`
-- ----------------------------
drop table if exists `dpossigners`;
create table `dpossigners` (
`id`  bigint(20) not null auto_increment ,
`loopstarttime`  datetime not null  ,
`signeraddress`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`punished`  bigint(20) not null default 0  ,
primary key (`id`),
unique index `unique` (`loopstarttime`, `signeraddress`) using btree ,
index `loopstarttime` (`loopstarttime`) using btree ,
index `signeraddress` (`signeraddress`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='List of miners'
auto_increment=1

;

-- ----------------------------
-- Table structure for `dpossnapshot`
-- ----------------------------
drop table if exists `dpossnapshot`;
create table `dpossnapshot` (
`blocknumber`  bigint(20) not null  ,
`headertime`  datetime not null ,
`confirmednumber`  int(11) not null ,
`loopstarttime`  datetime not null,
`period`  int(11) not null  ,
`minerreward`  int(11) not null  ,
`voterbalance`  decimal(65,0) not null  ,
primary key (`blocknumber`),
index `loopstarttime` (`loopstarttime`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='DPOS parameters'

;

-- ----------------------------
-- Table structure for `dposvotes`
-- ----------------------------
drop table if exists `dposvotes`;
create table `dposvotes` (
`id`  bigint(20) not null auto_increment ,
`loopstarttime`  datetime not null  ,
`candidate`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`voter`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`stake`  decimal(65,0) not null ,
`blocknumber`  bigint(20) not null  ,
`round`  int(11) null default 0 ,
primary key (`id`),
index `loopstarttime` (`loopstarttime`) using btree ,
index `candidate` (`candidate`) using btree ,
index `voter` (`voter`) using btree ,
index `blocknumber` (`blocknumber`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Voting'
auto_increment=4

;

-- ----------------------------
-- Table structure for `dposvoteswallet`
-- ----------------------------
drop table if exists `dposvoteswallet`;
create table `dposvoteswallet` (
`id`  bigint(20) not null auto_increment ,
`loopstarttime`  datetime not null  ,
`candidate`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`voter`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`stake`  decimal(65,0) not null  ,
`blocknumber`  bigint(20) not null  ,
`round`  int(11) null default 0 ,
`isexit`  int(11) null default 0  ,
`exitblocknumber`  bigint(20) not null ,
`txhash`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
primary key (`id`),
unique index `unique` (`voter`, `blocknumber`, `candidate`) using btree ,
unique index `txhash` (`txhash`) using btree ,
index `loopstarttime` (`loopstarttime`) using btree ,
index `candidate` (`candidate`) using btree ,
index `voter` (`voter`) using btree ,
index `blocknumber` (`blocknumber`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Wallet voting election situation'
auto_increment=1

;

-- ----------------------------
-- Table structure for `job_config`
-- ----------------------------
drop table if exists `job_config`;
create table `job_config` (
`id`  bigint(11) unsigned not null auto_increment ,
`job_name`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`namespace`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`zk_list`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`job_class`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`sharding_total_count`  int(11) null default null  ,
`load_level`  int(11) not null default 1  ,
`cron`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`pause_period_date`  text character set utf8 collate utf8_general_ci null  ,
`pause_period_time`  text character set utf8 collate utf8_general_ci null ,
`sharding_item_parameters`  text character set utf8 collate utf8_general_ci null ,
`job_parameter`  text character set utf8 collate utf8_general_ci null  ,
`monitor_execution`  tinyint(1) null default 1  ,
`process_count_interval_seconds`  int(11) null default null ,
`concurrent_data_process_thread_count`  int(11) null default null ,
`fetch_data_count`  int(11) null default null ,
`max_time_diff_seconds`  int(11) null default null ,
`monitor_port`  int(11) null default null  ,
`failover`  tinyint(1) null default null  ,
`misfire`  tinyint(1) null default null  ,
`job_sharding_strategy_class`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`description`  text character set utf8 collate utf8_general_ci null  ,
`timeout_seconds`  int(11) null default null ,
`show_normal_log`  tinyint(1) null default null ,
`channel_name`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`job_type`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`queue_name`  text character set utf8 collate utf8_general_ci null  ,
`prefer_list`  text character set utf8 collate utf8_general_ci null  ,
`local_mode`  tinyint(1) null default null  ,
`use_disprefer_list`  tinyint(1) null default null ,
`use_serial`  tinyint(1) null default null  ,
`job_degree`  tinyint(1) not null default 0  ,
`enabled_report`  tinyint(1) null default null  ,
`dependencies`  varchar(1000) character set utf8 collate utf8_general_ci null default null  ,
`groups`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`timeout_4_alarm_seconds`  int(11) not null default 0 ,
`time_zone`  varchar(255) character set utf8 collate utf8_general_ci not null default 'Asia/Shanghai'  ,
`is_enabled`  tinyint(1) null default 0  ,
`job_mode`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`custom_context`  varchar(8192) character set utf8 collate utf8_general_ci null default null ,
`rerun`  tinyint(4) not null default 0  ,
`up_stream`  varchar(1000) character set utf8 collate utf8_general_ci not null default '' ,
`down_stream`  varchar(1000) character set utf8 collate utf8_general_ci not null default '' ,
`backup1`  varchar(255) character set utf8 collate utf8_general_ci null default null,
`backup2`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`backup3`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`create_by`  varchar(255) character set utf8 collate utf8_general_ci null default null,
`create_time`  timestamp null default null  ,
`last_update_by`  varchar(255) character set utf8 collate utf8_general_ci null default null,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
primary key (`id`),
unique index `uniq_namespace_job_name` (`namespace`, `job_name`) using btree ,
index `idx_namespace` (`namespace`) using btree ,
index `idx_zk_list` (`zk_list`) using btree ,
index `idx_job_name` (`job_name`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
auto_increment=1

;

-- ----------------------------
-- Table structure for `job_config_history`
-- ----------------------------
drop table if exists `job_config_history`;
create table `job_config_history` (
`id`  bigint(11) unsigned not null auto_increment  ,
`job_name`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`job_class`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`sharding_total_count`  int(11) null default null  ,
`load_level`  int(11) not null default 1 ,
`time_zone`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`cron`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`pause_period_date`  text character set utf8 collate utf8_general_ci null  ,
`pause_period_time`  text character set utf8 collate utf8_general_ci null  ,
`sharding_item_parameters`  text character set utf8 collate utf8_general_ci null  ,
`job_parameter`  text character set utf8 collate utf8_general_ci null  ,
`monitor_execution`  tinyint(1) null default 1  ,
`process_count_interval_seconds`  int(11) null default null  ,
`concurrent_data_process_thread_count`  int(11) null default null  ,
`fetch_data_count`  int(11) null default null  ,
`max_time_diff_seconds`  int(11) null default null  ,
`monitor_port`  int(11) null default null  ,
`failover`  tinyint(1) null default null  ,
`misfire`  tinyint(1) null default null  ,
`job_sharding_strategy_class`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`description`  text character set utf8 collate utf8_general_ci null  ,
`timeout_4_alarm_seconds`  int(11) null default null ,
`timeout_seconds`  int(11) null default null ,
`show_normal_log`  tinyint(1) null default null ,
`channel_name`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`job_type`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`queue_name`  text character set utf8 collate utf8_general_ci null  ,
`namespace`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`zk_list`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`prefer_list`  text character set utf8 collate utf8_general_ci null,
`local_mode`  tinyint(1) null default null  ,
`use_disprefer_list`  tinyint(1) null default null  ,
`use_serial`  tinyint(1) null default null  ,
`job_degree`  tinyint(1) null default null  ,
`enabled_report`  tinyint(1) null default null  ,
`groups`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`dependencies`  text character set utf8 collate utf8_general_ci null ,
`is_enabled`  tinyint(1) null default 0 ,
`job_mode`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`custom_context`  varchar(8192) character set utf8 collate utf8_general_ci null default null  ,
`create_by`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`create_time`  timestamp null default null  ,
`last_update_by`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`last_update_time`  timestamp null default null ,
primary key (`id`),
index `job_name_idx` (`job_name`) using btree ,
index `namespace_idx` (`namespace`) using btree ,
index `zk_list_idx` (`zk_list`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Job configuration history table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `namespace_info`
-- ----------------------------
drop table if exists `namespace_info`;
create table `namespace_info` (
`id`  bigint(11) unsigned not null auto_increment  ,
`namespace`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`content`  varchar(16383) character set utf8 collate utf8_general_ci not null default ''  ,
`bus_id`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`is_deleted`  tinyint(4) not null default 0 ,
`create_time`  timestamp not null default '1979-12-31 08:00:00'  ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
primary key (`id`),
unique index `uniq_namespace_info_namespace` (`namespace`) using btree ,
index `idx_namespace_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Domain Information Table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `namespace_version_mapping`
-- ----------------------------
drop table if exists `namespace_version_mapping`;
create table `namespace_version_mapping` (
`id`  bigint(11) unsigned not null auto_increment  ,
`namespace`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`version_number`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`is_forced`  tinyint(1) null default 0  ,
`is_deleted`  tinyint(1) not null default 0  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00'  ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp  ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '',
primary key (`id`),
unique index `uniq_nvm_namespace` (`namespace`) using btree ,
index `idx_nvm_version_number` (`version_number`) using btree ,
index `idx_nvm_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Domain name version configuration table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `namespace_zkcluster_mapping`
-- ----------------------------
drop table if exists `namespace_zkcluster_mapping`;
create table `namespace_zkcluster_mapping` (
`id`  bigint(11) unsigned not null auto_increment  ,
`namespace`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`name`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`zk_cluster_key`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`is_deleted`  tinyint(4) not null default 0 ,
`create_time`  timestamp not null default '1979-12-31 08:00:00'  ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp  ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
primary key (`id`),
unique index `uniq_namespace` (`namespace`) using btree ,
index `idx_zk_cluster_key` (`zk_cluster_key`) using btree ,
index `idx_zk_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Domain name cluster mapping table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `utg_bandwidth_config`
-- ----------------------------
drop table if exists `utg_bandwidth_config`;
create table `utg_bandwidth_config` (
`id`  tinyint(4) not null auto_increment ,
`pledge_param`  varchar(10) character set utf8mb4 collate utf8mb4_general_ci not null  ,
`min`  int(8) not null  ,
`max`  int(8) null default null  ,
`val`  decimal(4,2) null default null  ,
`update_time`  datetime null default null ,
primary key (`id`),
index `idx_param` (`pledge_param`) using btree 
)
engine=innodb
default character set=utf8mb4 collate=utf8mb4_general_ci
auto_increment=5

;

-- ----------------------------
-- Table structure for `utg_clt_storagedata`
-- ----------------------------
drop table if exists `utg_clt_storagedata`;
create table `utg_clt_storagedata` (
`id`  bigint(20) not null auto_increment ,
`en_address`  varchar(60) character set utf8 collate utf8_general_ci null default null ,
`report_time`  datetime null default null ,
`storage_value`  bigint(60) null default null ,
`srtnum`  decimal(65,0) null default null ,
`profitamount`  decimal(65,0) null default null ,
`router_address`  decimal(20,8) null default null,
`from_addr`  varchar(60) character set utf8 collate utf8_general_ci null default null  ,
`to_addr`  varchar(60) character set utf8 collate utf8_general_ci null default null ,
`router_ipaddr`  varchar(60) character set utf8 collate utf8_general_ci null default null  ,
`trans_hash`  varchar(120) character set utf8 collate utf8_general_ci null default null  ,
`instime`  datetime null default null ,
`blocknumber`  bigint(20) null default null ,
primary key (`id`),
unique index `idx_hash` (`trans_hash`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='mining data table'
auto_increment=3

;

-- ----------------------------
-- Table structure for `utg_cvt_cfg`
-- ----------------------------
drop table if exists `utg_cvt_cfg`;
create table `utg_cvt_cfg` (
`tfn`  bigint(60) null default null  ,
`pfn`  bigint(60) null default null ,
`freq_n`  int(8) null default null,
`cvt_rate`  decimal(20,8) null default null  
)
engine=innodb
default character set=utf8mb4 collate=utf8mb4_general_ci
comment='UTG and flow conversion calculation table'

;

-- ----------------------------
-- Table structure for `utg_storage_miner`
-- ----------------------------
drop table if exists `utg_storage_miner`;
create table `utg_storage_miner` (
`id`  bigint(20) not null auto_increment ,
`miner_addr`  varchar(60) character set utf8 collate utf8_general_ci null default null  ,
`revenue_address`  varchar(60) character set utf8 collate utf8_general_ci null default null  ,
`pay_address`  varchar(60) character set utf8 collate utf8_general_ci null default null  ,
`line_type`  varchar(20) character set utf8 collate utf8_general_ci null default null  ,
`miner_mode`  int(6) null default 1 comment 'Mode 1 Traffic mining 2 Bandwidth mining' ,
`miner_status`  int(6) null default null comment '1 Mining 2 To be pledged (binding income address) 3 Exit' ,
`addpool`  int(6) null default 0 comment '0 not joined 1 joined the mining pool' ,
`bandwidth`  decimal(65,0) null default null comment ' Mbps' ,
`pledge_amount`  decimal(65,0) null default null comment 'Pledge Deposit Unit UTG' ,
`miner_storage`  bigint(30) null default null  ,
`paysrt`  decimal(65,0) null default null  ,
`revenue_amount`  decimal(65,0) null default null comment 'Total revenue unit UTG' ,
`lock_amount`  decimal(65,0) null default null comment 'Current lock up amount Unit UTG' ,
`release_amount`  decimal(65,0) null default null comment 'Total released amount' ,
`draw_amount`  decimal(65,0) null default null  ,
`blocknumber`  bigint(30) null default null  ,
`join_time`  datetime null default null ,
`sync_time`  datetime null default null ,
primary key (`id`),
unique index `idx_miner_addr` (`miner_addr`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Current miner information table'
auto_increment=40

;

-- ----------------------------
-- Table structure for `utg_node_miner`
-- ----------------------------
drop table if exists `utg_node_miner`;
create table `utg_node_miner` (
`id`  bigint(20) not null auto_increment ,
`node_address`  varchar(60) character set utf8 collate utf8_general_ci null default null comment 'Node address' ,
`revenue_address`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`node_type`  int(6) null default 1 comment '1 Candidate node 2 Witness node 3 Exit node' ,
`fractions`  int(6) null default null  ,
`pledge_amount`  decimal(65,0) null default null  ,
`blocknumber`  bigint(30) null default null ,
`join_time`  datetime null default null ,
`sync_time`  datetime null default null ,
primary key (`id`),
unique index `idx_node` (`node_address`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Node table'
auto_increment=3

;

-- ----------------------------
-- Table structure for `node_exit`
-- ----------------------------
drop table if exists `node_exit`;
create table `node_exit` (
`id`  bigint(20) not null auto_increment ,
`timestamp`  datetime not null default '1979-12-31 08:00:00'  ,
`pledgeamount`  decimal(65,0) null default 0  ,
`addressname`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`deductionamount`  decimal(65,0) null default 0 ,
`tractamount`  decimal(65,0) null default 0 ,
`lockstartnumber`  bigint(20) null default null  ,
`locknumber`  bigint(20) null default null  ,
`releasenumber`  bigint(20) null default null ,
`releaseinterval`  bigint(20) null default null  ,
primary key (`id`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='exit data sheet'
auto_increment=1

;

-- ----------------------------
-- Table structure for `pageslide`
-- ----------------------------
drop table if exists `pageslide`;
create table `pageslide` (
`id`  bigint(20) not null auto_increment ,
`slidetitle`  varchar(80) character set utf8 collate utf8_general_ci not null  ,
`slidepicture`  varchar(255) character set utf8 collate utf8_general_ci not null ,
`slidetype`  int(11) not null  ,
`slidesort`  int(11) not null  ,
`slideurl`  varchar(255) character set utf8 collate utf8_general_ci not null,
`slidetarget`  int(11) not null ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00'  ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
primary key (`id`),
unique index `idx_slide_type` (`slidetype`, `slidesort`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Page ad slides'
auto_increment=1

;

-- ----------------------------
-- Table structure for `pledge_data`
-- ----------------------------
drop table if exists `pledge_data`;
create table `pledge_data` (
`id`  bigint(20) not null auto_increment ,
`starttime`  datetime not null ,
`txhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`type`  int(11) not null  ,
`logindex`  int(11) not null,
`address`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`value`  decimal(65,0) not null default 0 ,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`blocknumber`  bigint(20) not null default 0  ,
`totalamount`  decimal(65,0) null default 0 ,
`leftamount`  decimal(65,0) null default 0  ,
`status`  int(11) null default 0 ,
`gaslimit`  bigint(20) not null default 0  ,
`gasused`  bigint(20) not null default 0 ,
`gasprice`  bigint(20) not null default 0  ,
`unlocknumber`  bigint(20) not null default 0  ,
`loglength`  bigint(20) not null default 0  ,
`nodenumber`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`presentamount`  decimal(65,0) null default 0  ,
`locknumheigth`  bigint(20) not null default 0  ,
`pledgeamount`  decimal(65,0) null default 0,
`pledgeaddress`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`punilshamount`  decimal(65,0) null default 0  ,
`receiveaddress`  varchar(255) character set utf8 collate utf8_general_ci null default null,
`releaseheigth`  bigint(20) not null default 0 ,
`releaseinterval`  bigint(20) not null default 0 ,
`pledgetotalamount`  decimal(65,0) null default 0 ,
primary key (`id`),
unique index `txhash` (`txhash`, `unlocknumber`) using btree ,
index `type` (`type`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='New pledge contract data table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `pledge_total_data`
-- ----------------------------
drop table if exists `pledge_total_data`;
create table `pledge_total_data` (
`id`  bigint(20) not null auto_increment ,
`starttime`  datetime not null ,
`type`  int(11) not null  ,
`address`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`value`  decimal(65,0) null default 0  ,
`blocknumber`  bigint(20) null default 0 ,
`pledgetotalamount`  decimal(65,0) null default 0  ,
`cashtotalamount`  decimal(65,0) null default 0  ,
`punilshamount`  decimal(65,0) null default 0 ,
`realshnumber`  bigint(20) null default 0 ,
`status`  int(11) null default 0  ,
`nodenumber`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`locknumber`  bigint(20) null default 0  ,
`releaseintervalnum`  bigint(20) null default 0  ,
`exittime`  datetime null default null ,
`pledgeaddress`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
primary key (`id`),
index `type` (`type`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Pledge summary data table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `punished`
-- ----------------------------
drop table if exists `punished`;
create table `punished` (
`id`  bigint(20) not null auto_increment ,
`timestamp`  datetime not null default '1979-12-31 08:00:00' ,
`address`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`type`  int(11) not null comment 'Type 1: Absent block production 2: Not participating in block production' ,
`blocknumber`  bigint(20) null default null ,
`fractions`  bigint(20) null default null  ,
`round`  int(11) not null ,
`addressname`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`pledgeamount`  decimal(65,0) null default 0 ,
primary key (`id`),
index `type` (`type`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Penalty data structure table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `release_version_info`
-- ----------------------------
drop table if exists `release_version_info`;
create table `release_version_info` (
`id`  bigint(11) unsigned not null auto_increment ,
`version_number`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`package_url`  varchar(512) character set utf8 collate utf8_general_ci not null default ''  ,
`check_code`  varchar(255) character set utf8 collate utf8_general_ci not null default '',
`version_desc`  varchar(2048) character set utf8 collate utf8_general_ci null default '',
`is_deleted`  tinyint(1) not null default 0 ,
`create_time`  timestamp not null default '1979-12-31 08:00:00'  ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
primary key (`id`),
unique index `uniq_rvi_version_number` (`version_number`) using btree ,
index `idx_version_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Saturn release version information table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `saturn_dashboard_history`
-- ----------------------------
drop table if exists `saturn_dashboard_history`;
create table `saturn_dashboard_history` (
`id`  bigint(11) unsigned not null auto_increment ,
`record_date`  date null default null ,
`zk_cluster`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`record_type`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`topic`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`content`  longtext character set utf8 collate utf8_general_ci null ,
primary key (`id`),
unique index `uniq_dashboard_history_zk_cluster_record_type_topic_record_date` (`zk_cluster`, `record_type`, `topic`, `record_date`) using btree ,
index `dashboard_record_date_idx` (`record_date`) using btree ,
index `dashboard_zk_cluster_idx` (`zk_cluster`) using btree ,
index `dashboard_record_type_idx` (`record_type`) using btree ,
index `dashboard_topic_idx` (`topic`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='dashboard history table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `saturn_statistics`
-- ----------------------------
drop table if exists `saturn_statistics`;
create table `saturn_statistics` (
`id`  bigint(11) not null auto_increment  ,
`name`  varchar(255) character set utf8 collate utf8_general_ci not null,
`zklist`  varchar(255) character set utf8 collate utf8_general_ci not null  ,
`result`  longtext character set utf8 collate utf8_general_ci not null  ,
primary key (`id`),
unique index `uniq_statistics_zk_cluster_topic` (`zklist`, `name`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
auto_increment=1

;

-- ----------------------------
-- Table structure for `stakingdelegators`
-- ----------------------------
drop table if exists `stakingdelegators`;
create table `stakingdelegators` (
`id`  bigint(20) not null auto_increment ,
`poolhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`delegator`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`stakeamount`  decimal(65,0) not null  ,
`orderedwithdraw`  decimal(65,0) not null  ,
`withdrawallowed`  decimal(65,0) not null  ,
`orderedallowed`  decimal(65,0) not null  ,
`epoch`  int(11) not null  ,
primary key (`id`),
unique index `unique` (`poolhash`, `delegator`) using btree ,
index `poolhash` (`poolhash`) using btree ,
index `delegator` (`delegator`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Equity representative information'
auto_increment=1

;

-- ----------------------------
-- Table structure for `stakingpools`
-- ----------------------------
drop table if exists `stakingpools`;
create table `stakingpools` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`miningaddress`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`isactive`  tinyint(4) not null  ,
`isdeleted`  tinyint(4) not null  ,
`delegatorscount`  int(11) not null ,
`stakedamount`  decimal(65,0) not null ,
`selfamount`  decimal(65,0) not null  ,
`isvalidator`  tinyint(4) not null  ,
`validatorcount`  int(11) not null ,
`isbanned`  tinyint(4) not null  ,
`bannedcount`  int(11) not null  ,
`likelihood`  decimal(5,2) not null ,
`stakedratio`  decimal(5,2) not null ,
`banneduntil`  datetime null default null  ,
primary key (`hash`),
index `mineraddress` (`miningaddress`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Stake pool information'

;

-- ----------------------------
-- Table structure for `sys_admin`
-- ----------------------------
drop table if exists `sys_admin`;
create table `sys_admin` (
`id`  bigint(11) unsigned not null auto_increment  ,
`admin_name`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`real_name`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`password`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`salt`  varchar(255) character set utf8 collate utf8_general_ci not null ,
`phone`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`email`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`available`  tinyint(4) not null default 0  ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00' ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
primary key (`id`),
unique index `uniq_admin_admin_name` (`admin_name`) using btree ,
index `idx_admin_available` (`available`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Backstage management user table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `sys_admin_role`
-- ----------------------------
drop table if exists `sys_admin_role`;
create table `sys_admin_role` (
`id`  bigint(11) unsigned not null auto_increment ,
`admin_name`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`role_key`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`namespace`  varchar(255) character set utf8 collate utf8_general_ci not null default '',
`need_approval`  tinyint(4) not null default 1 ,
`is_deleted`  tinyint(4) not null default 0 ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00' ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
primary key (`id`),
unique index `uniq_admin_role_key` (`admin_name`, `role_key`, `namespace`) using btree ,
index `idx_admin_role_is_deleted` (`is_deleted`) using btree ,
index `idx_admin_role_u_r_n_n_i` (`admin_name`, `role_key`, `namespace`, `need_approval`, `is_deleted`) using btree ,
index `idx_admin_role_r_n_n_i` (`role_key`, `namespace`, `need_approval`, `is_deleted`) using btree ,
index `idx_admin_role_n_n_i` (`namespace`, `need_approval`, `is_deleted`) using btree ,
index `idx_admin_role_n_i` (`need_approval`, `is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Backstage management user role relationship table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `sys_config`
-- ----------------------------
drop table if exists `sys_config`;
create table `sys_config` (
`id`  bigint(11) unsigned not null auto_increment  ,
`property`  varchar(255) character set utf8 collate utf8_general_ci not null ,
`value`  varchar(2000) character set utf8 collate utf8_general_ci not null  ,
primary key (`id`),
unique index `idx_property` (`property`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='System configuration table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `sys_permission`
-- ----------------------------
drop table if exists `sys_permission`;
create table `sys_permission` (
`id`  bigint(11) unsigned not null auto_increment  ,
`permission_key`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`permission_name`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`description`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`is_deleted`  tinyint(4) not null default 0 ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`create_time`  timestamp not null default '1979-12-31 08:00:00'  ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp  ,
primary key (`id`),
unique index `uniq_permission_permission_key` (`permission_key`) using btree ,
index `idx_permission_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Background management authority table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
drop table if exists `sys_role`;
create table `sys_role` (
`id`  bigint(11) unsigned not null auto_increment  ,
`role_key`  varchar(255) character set utf8 collate utf8_general_ci not null default '',
`role_name`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`description`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`is_deleted`  tinyint(4) not null default 0  ,
`is_relating_to_namespace`  tinyint(4) not null default 0  ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00' ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
primary key (`id`),
unique index `uniq_role_role_key` (`role_key`) using btree ,
index `idx_role_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Background management role table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `sys_role_permission`
-- ----------------------------
drop table if exists `sys_role_permission`;
create table `sys_role_permission` (
`id`  bigint(11) unsigned not null auto_increment  ,
`role_key`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`permission_key`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`is_deleted`  tinyint(4) not null default 0 ,
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00' ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '',
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
primary key (`id`),
unique index `uniq_role_permission_key` (`role_key`, `permission_key`) using btree ,
index `idx_role_permission_key` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Background management role permission relationship table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `t_sys_cfg`
-- ----------------------------
drop table if exists `t_sys_cfg`;
create table `t_sys_cfg` (
`id`  bigint(64) not null auto_increment  ,
`cfgvalue`  bigint(64) null default null  ,
`cfgdesc`  varchar(200) character set utf8 collate utf8_general_ci null default null  ,
`cfgname`  varchar(128) character set utf8 collate utf8_general_ci null default null  ,
`createtime`  datetime null default null ,
`createby`  bigint(20) null default null ,
`modifytime`  datetime null default null ,
`modifyby`  bigint(20) null default null ,
`cfgtype`  tinyint(1) null default 0  ,
primary key (`id`),
unique index `cfgname` (`cfgname`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='System parameters'
auto_increment=11

;

-- ----------------------------
-- Table structure for `temporary_shared_status`
-- ----------------------------
drop table if exists `temporary_shared_status`;
create table `temporary_shared_status` (
`id`  bigint(11) unsigned not null auto_increment  ,
`status_key`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`status_value`  varchar(4000) character set utf8 collate utf8_general_ci not null default ''  ,
primary key (`id`),
unique index `uniq_tss_status_key` (`status_key`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Shared status table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `token_contract`
-- ----------------------------
drop table if exists `token_contract`;
create table `token_contract` (
`id`  bigint(20) not null auto_increment ,
`contractaddress`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`contractmanager`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
primary key (`id`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='1155 contract'
auto_increment=1

;

-- ----------------------------
-- Table structure for `tokencatalog`
-- ----------------------------
drop table if exists `tokencatalog`;
create table `tokencatalog` (
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`websites`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`tokenico`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`introduction`  text character set utf8 collate utf8_general_ci null  ,
primary key (`contract`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Token registration information'

;

-- ----------------------------
-- Table structure for `tokeninstances`
-- ----------------------------
drop table if exists `tokeninstances`;
create table `tokeninstances` (
`id`  bigint(20) not null auto_increment ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`tokenid`  decimal(65,0) not null comment 'ERC-721 tokens have IDs' ,
`tokenURI`  varchar(1024) character set utf8 collate utf8_general_ci null default null ,
`metadata`  text character set utf8 collate utf8_general_ci null ,
primary key (`id`),
unique index `unique` (`contract`, `tokenid`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='ERC721 token example'
auto_increment=1

;

-- ----------------------------
-- Table structure for `tokenmarket`
-- ----------------------------
drop table if exists `tokenmarket`;
create table `tokenmarket` (
`id`  bigint(20) not null auto_increment ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`marketdate`  datetime not null ,
`closingprice`  decimal(65,8) not null  ,
`openingprice`  decimal(65,8) not null  ,
primary key (`id`),
unique index `unique` (`contract`, `marketdate`) using btree ,
index `marketdate` (`marketdate`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Token market price'
auto_increment=1

;

-- ----------------------------
-- Table structure for `tokens`
-- ----------------------------
drop table if exists `tokens`;
create table `tokens` (
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null,
`type`  int(11) not null comment 'type：\\n    0 - ERC-20,\\n    1 - ERC-721' ,
`name`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`symbol`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`decimals`  int(11) null default null  ,
`totalsupply`  decimal(65,0) null default null  ,
`cataloged`  tinyint(4) not null  ,
`description`  text character set utf8 collate utf8_general_ci null ,
`contractmanager`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
primary key (`contract`),
index `type` (`type`) using btree ,
index `cataloged` (`cataloged`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Token list'

;

-- ----------------------------
-- Table structure for `trans_logs`
-- ----------------------------
drop table if exists `trans_logs`;
create table `trans_logs` (
`id`  bigint(20) not null auto_increment ,
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`logindex`  int(11) not null  ,
`type`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`address`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
`firsttopic`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`secondtopic`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`thirdtopic`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`fourthtopic`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`data`  text character set utf8 collate utf8_general_ci null ,
primary key (`id`),
unique index `unique` (`transhash`, `logindex`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Transaction log list'
auto_increment=3751

;

-- ----------------------------
-- Table structure for `trans_token`
-- ----------------------------
drop table if exists `trans_token`;
create table `trans_token` (
`id`  bigint(20) not null auto_increment ,
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`logindex`  int(11) not null,
`cointype`  int(11) not null default 0 comment 'type:\n     0 - ERC20\n     1 - ERC721' ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`amount`  decimal(65,0) not null  ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null,
`blocknumber`  bigint(20) not null ,
`tokenid`  decimal(65,0) null default null comment 'ERC-721 tokens have IDs' ,
`gasused`  bigint(20) not null default 0 ,
`gasprice`  bigint(20) not null default 0 ,
`gaslimit`  bigint(20) not null default 0 ,
`nonce`  bigint(20) not null default 0  ,
`timestamp`  datetime not null ,
primary key (`id`),
unique index `unique` (`transhash`, `logindex`) using btree ,
index `from` (`fromaddr`) using btree ,
index `to` (`toaddr`) using btree ,
index `contract` (`contract`) using btree ,
index `blockhash` (`blockhash`) using btree ,
index `blocknumber` (`blocknumber`) using btree ,
index `cointype` (`cointype`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Token transaction list'
auto_increment=3437

;

-- ----------------------------
-- Table structure for `transaction`
-- ----------------------------
drop table if exists `transaction`;
create table `transaction` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`istrunk`  tinyint(4) not null ,
`timestamp`  datetime not null ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`value`  decimal(65,0) not null default 0 ,
`nonce`  bigint(20) not null default 0  ,
`gaslimit`  bigint(20) not null default 0 ,
`gasprice`  bigint(20) not null default 0  ,
`status`  int(11) not null default 0  ,
`gasused`  bigint(20) not null default 0  ,
`cumulative`  bigint(20) not null default 0  ,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`blocknumber`  bigint(20) not null default 0 ,
`blockindex`  int(11) not null default 0  ,
`input`  text character set utf8 collate utf8_general_ci null,
`contract`  varchar(100) character set utf8 collate utf8_general_ci null default null  ,
`error`  varchar(1024) character set utf8 collate utf8_general_ci null default null  ,
`internal`  tinyint(4) not null default 0  ,
`type`  tinyint(4) not null default 0 ,
`ufoprefix`  varchar(10) character set utf8 collate utf8_general_ci null default null comment 'Prefix' ,
`ufoversion`  varchar(10) character set utf8 collate utf8_general_ci null default null comment 'Version' ,
`ufooperator`  varchar(20) character set utf8 collate utf8_general_ci null default null comment 'operate' ,
`param1`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`param2`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`param3`  decimal(65,0) null default null ,
`param4`  decimal(65,0) null default null ,
`param5`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`param6`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
primary key (`hash`),
index `from` (`fromaddr`, `nonce`) using btree ,
index `to` (`toaddr`) using btree ,
index `timestamp` (`timestamp`) using btree ,
index `status` (`status`) using btree ,
index `blockhash` (`blockhash`) using btree ,
index `blocknumber` (`blocknumber`) using btree ,
index `istrunk` (`istrunk`, `blocknumber`) using btree ,
index `internal` (`internal`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Transaction list'

;

-- ----------------------------
-- Table structure for `transdiscard`
-- ----------------------------
drop table if exists `transdiscard`;
create table `transdiscard` (
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`timestamp`  datetime not null ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`value`  decimal(65,0) not null default 0  ,
`nonce`  bigint(20) not null default 0 ,
`gaslimit`  bigint(20) not null default 0 ,
`gasprice`  bigint(20) not null default 0  ,
`input`  text character set utf8 collate utf8_general_ci null ,
primary key (`transhash`),
index `from` (`fromaddr`, `nonce`) using btree ,
index `to` (`toaddr`) using btree ,
index `timestamp` (`timestamp`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Abandoned transaction list'

;

-- ----------------------------
-- Table structure for `transfer_miner`
-- ----------------------------
drop table if exists `transfer_miner`;
create table `transfer_miner` (
  `id` bigint(20) not null auto_increment,
  `txhash` varchar(100) not null ,
  `type` int(11) default null comment 'Type 1: block Locking 2: block Release Type 3: Node Locking 4: Node Release 5: Traffic Locking 6: Traffic Release 7: Miner Locking 8 Miner Release 9 Bandwidth Reward Locking, 10 Bandwidth Reward Release',
  `logindex` int(11) default '0' comment 'Log index (0 automatic, 1 fill) ',
  `address` varchar(255) default null ,
  `value` decimal(65,0) default '0' ,
  `blockhash` varchar(100) default null ,
  `blocknumber` bigint(20) default '0' ,
  `totalamount` decimal(65,0) default '0' ,
  `leftamount` decimal(65,0) default '0' ,
  `status` int(11) default '0',
  `gaslimit` bigint(20) default '0',
  `gasused` bigint(20) default '0' ,
  `gasprice` bigint(20) default '0' ,
  `unlocknumber` bigint(20) default '0' ,
  `loglength` bigint(20) default '0' ,
  `starttime` datetime default null ,
  `nodenumber` varchar(255) default null ,
  `presentamount` decimal(65,0) default '0' ,
  `locknumheigth` bigint(20) default '0' ,
  `pledgeamount` decimal(65,0) default '0' ,
  `pledgeaddress` varchar(255) default null ,
  `punilshamount` decimal(65,0) default '0' ,
  `receiveaddress` varchar(255) default null,
  `releaseheigth` bigint(20) default '0' ,
  `releaseinterval` bigint(20) default '0' ,
  `pledgetotalamount` decimal(65,0) default '0',
  `curtime` datetime default null ,
  `releaseamount` decimal(65,0) default '0' ,
  `revenueaddress` varchar(60) default null ,
  primary key (`id`),
  key `type` (`type`) using btree,
  key `add_type_blocknumber` (`type`,`address`,`blocknumber`)
) engine=innodb auto_increment=1 default charset=utf8 comment='Lock release data table';



-- ----------------------------
-- Table structure for `transferminer`
-- ----------------------------
drop table if exists `transferminer`;
create table `transferminer` (
`id`  bigint(20) not null auto_increment ,
`txhash`  varchar(100) character set utf8 collate utf8_general_ci not null,
`type`  int(11) not null ,
`logindex`  int(11) not null  ,
`address`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`value`  decimal(65,0) not null default 0  ,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`blocknumber`  bigint(20) not null default 0  ,
primary key (`id`),
index `type` (`type`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Lock release data table'
auto_increment=1

;

-- ----------------------------
-- Table structure for `transforks`
-- ----------------------------
drop table if exists `transforks`;
create table `transforks` (
`id`  bigint(20) not null auto_increment ,
`nephewhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`nephewblock`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`unclehash`  varchar(100) character set utf8 collate utf8_general_ci not null,
`uncleblock`  varchar(100) character set utf8 collate utf8_general_ci not null ,
primary key (`id`),
unique index `unique` (`nephewhash`, `nephewblock`, `unclehash`, `uncleblock`) using btree ,
index `nephew` (`nephewhash`, `nephewblock`) using btree ,
index `nephewblock` (`nephewblock`) using btree ,
index `uncle` (`unclehash`, `uncleblock`) using btree ,
index `uncleblock` (`uncleblock`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Fork transaction list'
auto_increment=1

;

-- ----------------------------
-- Table structure for `transinternal`
-- ----------------------------
drop table if exists `transinternal`;
create table `transinternal` (
`id`  bigint(20) not null auto_increment ,
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`transindex`  int(11) not null default 0 ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`value`  decimal(65,0) not null default 0  ,
`gaslimit`  bigint(20) not null default 0 ,
`gasused`  bigint(20) not null default 0 ,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`blocknumber`  bigint(20) null default null ,
`type`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`calltype`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`input`  text character set utf8 collate utf8_general_ci null  ,
`createdcode`  text character set utf8 collate utf8_general_ci null  ,
`initcode`  text character set utf8 collate utf8_general_ci null  ,
`output`  text character set utf8 collate utf8_general_ci null  ,
`traceaddress`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
primary key (`id`),
unique index `unique` (`transhash`, `transindex`) using btree ,
index `transhash` (`transhash`) using btree ,
index `from` (`fromaddr`) using btree ,
index `to` (`toaddr`) using btree ,
index `blockhash` (`blockhash`) using btree ,
index `blocknumber` (`blocknumber`) using btree ,
index `type` (`type`, `calltype`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='List of internal transactions'
auto_increment=1

;

-- ----------------------------
-- Table structure for `translogs`
-- ----------------------------
drop table if exists `translogs`;
create table `translogs` (
`id`  bigint(20) not null auto_increment ,
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`logindex`  int(11) not null  ,
`type`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`address`  varchar(100) character set utf8 collate utf8_general_ci null default null ,
`firsttopic`  varchar(255) character set utf8 collate utf8_general_ci null default null ,
`secondtopic`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`thirdtopic`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`fourthtopic`  varchar(255) character set utf8 collate utf8_general_ci null default null  ,
`data`  text character set utf8 collate utf8_general_ci null ,
primary key (`id`),
unique index `unique` (`transhash`, `logindex`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Transaction log list'
auto_increment=1

;

-- ----------------------------
-- Table structure for `transpending`
-- ----------------------------
drop table if exists `transpending`;
create table `transpending` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`timestamp`  datetime not null ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`value`  decimal(65,0) not null default 0  ,
`nonce`  bigint(20) not null default 0 ,
`gaslimit`  bigint(20) not null default 0  ,
`gasprice`  bigint(20) not null default 0 ,
`input`  text character set utf8 collate utf8_general_ci null ,
primary key (`hash`),
index `from` (`fromaddr`, `nonce`) using btree ,
index `to` (`toaddr`) using btree ,
index `timestamp` (`timestamp`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Pending transaction list'

;

-- ----------------------------
-- Table structure for `transtemp`
-- ----------------------------
drop table if exists `transtemp`;
create table `transtemp` (
`hash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`timestamp`  datetime not null ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`value`  decimal(65,0) not null default 0  ,
`nonce`  bigint(20) not null default 0  ,
`gaslimit`  bigint(20) not null default 0 ,
`gasprice`  bigint(20) not null default 0 ,
`input`  text character set utf8 collate utf8_general_ci null ,
primary key (`hash`)
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Temporary transaction list'

;

-- ----------------------------
-- Table structure for `transtoken`
-- ----------------------------
drop table if exists `transtoken`;
create table `transtoken` (
`id`  bigint(20) not null auto_increment ,
`transhash`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`logindex`  int(11) not null ,
`cointype`  int(11) not null default 0 comment 'type:\n     0 - ERC20\n     1 - ERC721' ,
`fromaddr`  varchar(100) character set utf8 collate utf8_general_ci not null ,
`toaddr`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`amount`  decimal(65,0) not null ,
`contract`  varchar(100) character set utf8 collate utf8_general_ci not null,
`blockhash`  varchar(100) character set utf8 collate utf8_general_ci not null  ,
`blocknumber`  bigint(20) not null ,
`tokenid`  decimal(65,0) null default null comment 'ERC-721 tokens have IDs' ,
primary key (`id`),
unique index `unique` (`transhash`, `logindex`) using btree ,
index `from` (`fromaddr`) using btree ,
index `to` (`toaddr`) using btree ,
index `contract` (`contract`) using btree ,
index `blockhash` (`blockhash`) using btree ,
index `blocknumber` (`blocknumber`) using btree ,
index `cointype` (`cointype`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='Token transaction list'
auto_increment=1

;

-- ----------------------------
-- Table structure for `zk_cluster_info`
-- ----------------------------
drop table if exists `zk_cluster_info`;
create table `zk_cluster_info` (
`id`  bigint(11) unsigned not null auto_increment  ,
`zk_cluster_key`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`alias`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`connect_string`  varchar(255) character set utf8 collate utf8_general_ci not null default ''  ,
`description`  varchar(255) character set utf8 collate utf8_general_ci not null default '',
`is_deleted`  tinyint(4) not null default 0  ,
`create_time`  timestamp not null default '1979-12-31 08:00:00',
`created_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
`last_update_time`  timestamp not null default '1979-12-31 08:00:00' on update current_timestamp ,
`last_updated_by`  varchar(255) character set utf8 collate utf8_general_ci not null default '' ,
primary key (`id`),
unique index `uniq_zk_cluster_info_zk_cluster_key` (`zk_cluster_key`) using btree ,
index `idx_cluster_is_deleted` (`is_deleted`) using btree 
)
engine=innodb
default character set=utf8 collate=utf8_general_ci
comment='zk cluster information table'
auto_increment=1

;
drop table if exists `utg_clt_storagedata_day`;
create table `utg_clt_storagedata_day` (
  `id` bigint(20) not null auto_increment,
  `en_address` varchar(60) default null ,
  `report_time` datetime default null ,
  `storage_value` bigint(60) default null ,
  `srtnum` decimal(65,0) default null ,
  `profitamount` decimal(65,0) default null ,
  `instime` datetime default null ,
  `blocknumber` bigint(20) default null ,
  `fwflag` tinyint(4) default '0' comment '0 storage revenue 1 bandwidth reward',
  primary key (`id`)
) engine=innodb auto_increment=1 default charset=utf8 comment='Store mining data daily table';

drop table if exists `utg_net_statics`;
create table `utg_net_statics` (
  `ctime` datetime not null comment ' yyyy-MM-dd',
  `utg_gbrate` decimal(20,4) default '0.0000' comment 'Redeem storage per GButg/bw',
  `total_utg` decimal(65,0) default '0' comment '24 hour utg',
  `total_bw` decimal(65,0) default '0' comment ' total storage M',
  `incre_bw` decimal(65,0) default '0' comment 'Increase storage compared to the previous day',
  primary key (`ctime`)
) engine=innodb default charset=utf8mb4;

drop table if exists `utg_faucet`;
create table `utg_faucet` (
  `id` bigint(20) not null auto_increment,
  `address` varchar(60) default null ,
  `create_time` datetime default null ,
  `hash` varchar(100) default null,
  `num` decimal(65,0) default null,
  primary key (`id`)
) engine=innodb auto_increment=5 default charset=utf8 comment='Faucet table';


drop table if exists `storage_rent`;
create table `storage_rent`(
	`rentid` bigint(11) not null  auto_increment , 
	`rent_hash` varchar(100) collate utf8_general_ci null  , 
	`device_addr` varchar(60) collate utf8_general_ci null  , 
	`rent_addr` varchar(60) collate utf8_general_ci null  , 
	`rent_space` decimal(60,0) null  , 
	`rent_price` decimal(40,0) null  , 
	`rent_time` int(8) null  , 
	`rent_number` bigint(20) null  , 
	`rent_amount` decimal(60,0) null  , 
	`rent_status` int(8) not null  , 
	`recev_amount` decimal(60,0) null  , 
	`valid_number` bigint(20) null  , 
	`succ_number` bigint(20) null  , 
	`fail_count` int(8) null  default 0 , 
	`instime` datetime null  , 
	`updatetime` datetime null  , 
	primary key (`rentid`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';

drop table if exists `storage_request`;
create table `storage_request`(
	`reqid` bigint(11) not null  auto_increment , 
	`device_addr` varchar(60) collate utf8_general_ci null  , 
	`rent_hash` varchar(100) collate utf8_general_ci null  , 
	`storageid` bigint(11) null  , 
	`rentid` bigint(11) null  , 
	`req_type` varchar(60) collate utf8_general_ci null  , 
	`req_status` int(6) null  , 
	`req_number` bigint(20) null  , 
	`req_space` decimal(60,0) null  , 
	`rent_price` decimal(40,0) null  , 
	`rent_time` int(8) null  , 
	`rent_amount` decimal(60,0) null  , 
	`pledge_status` int(6) null  , 
	`instime` datetime null  , 
	`updatetime` datetime null  , 
	primary key (`reqid`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';

drop table if exists `storage_space`;
create table `storage_space`(
	`storageid` bigint(11) not null  auto_increment , 
	`device_addr` varchar(60) collate utf8_general_ci null  , 
	`pledge_addr` varchar(60) collate utf8_general_ci null  , 
	`revenue_addr` varchar(60) collate utf8_general_ci null  , 
	`pledge_status` int(6) null  , 
	`pledge_number` bigint(20) null  , 
	`pledge_amount` decimal(60,0) null  , 
	`pensate_amount` decimal(60,0) null  , 
	`declare_space` decimal(60,0) null  , 
	`free_space` decimal(60,0) null  , 
	`rent_price` decimal(40,0) null  , 
	`rent_num` bigint(20) null  , 
	`total_amount` decimal(60,0) null  , 
	`storage_amount` decimal(60,0) null  , 
	`rent_amount` decimal(60,0) null  ,
	`bw_size` decimal(10,2) default null,
	`bw_ratio` decimal(10,2) default null,	
	`valid_number` bigint(20) null  , 
	`succ_number` bigint(20) null  , 
	`fail_count` int(8) null  default 0 , 
	`instime` datetime null  , 
	`updatetime` datetime null  , 
	primary key (`storageid`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';

drop table if exists `storage_config`;
create table `storage_config` (
  `type` varchar(80) not null,
  `seq` int(11) not null,
  `min` decimal(20,2) default null,
  `max` decimal(20,2) default null,
  `value` decimal(10,2) default null,
  `updatetime` datetime default null,
  primary key (`type`,`seq`)
) engine=innodb default charset=utf8 collate='utf8_general_ci';

drop table if exists `storage_revenue`;
create table `storage_revenue` (
  `revenueid` bigint(11) not null auto_increment,
  `revenue_addr` varchar(60) not null,
  `ratio` decimal(10,2) default null,
  `capacity` decimal(60,0) default null,
  `updatetime` datetime default null,
  primary key (`revenueid`)
) engine=innodb default charset=utf8 collate='utf8_general_ci';

drop table if exists `storage_release_detail`;
create table `storage_release_detail` (
  `id` bigint(20) not null auto_increment,
  `rewardid` bigint(20) default null,
  `address` varchar(60) default null,
  `revenueaddress` varchar(60) default null,
  `type` int(11) default null,
  `blocknumber` bigint(20) default null,
  `totalamount` decimal(65,0) default null,
  `releaseonce` decimal(65,0) default null,
  `releasetype` int(11) default null,
  `updatetime` datetime default null,
  primary key (`id`)
) engine=innodb default charset=utf8 collate='utf8_general_ci';

drop table if exists `storage_release_compare`;
create table `storage_release_compare` (
  `id` bigint(20) not null auto_increment,
  `blockdays` int(10) default null,
  `startblock` bigint(20) default null,
  `endblock` bigint(20) default null,
  `revenueaddress` varchar(60) default null,
  `release_stat` decimal(65,0) default null,
  `release_pay` decimal(65,0) default null,
  `offset` decimal(65,0) default null,
  `updatetime` datetime default null,
  primary key (`id`)
) engine=innodb default charset=utf8 collate='utf8_general_ci';

alter table `storage_rent` 
	add key `device_addr`(`device_addr`) , 
	add key `rent_hash`(`rent_hash`) ;

alter table `storage_space` 
	add key `device_addr`(`device_addr`) , 
	add key `revenue_addr`(`revenue_addr`) ;
	
alter table `storage_revenue` 
	add key `revenue_addr`(`revenue_addr`) ;


-- ----------------------------
-- Auto increment value for `addr_balances`
-- ----------------------------
alter table `addr_balances` auto_increment=1452204;

-- ----------------------------
-- Auto increment value for `addresses`
-- ----------------------------
alter table `addresses` auto_increment=922258;

-- ----------------------------
-- Auto increment value for `addresses_token`
-- ----------------------------
alter table `addresses_token` auto_increment=415;

-- ----------------------------
-- Auto increment value for `api_config`
-- ----------------------------
alter table `api_config` auto_increment=1;

-- ----------------------------
-- Auto increment value for `api_configplat`
-- ----------------------------
alter table `api_configplat` auto_increment=1;

-- ----------------------------
-- Auto increment value for `api_configplat_en`
-- ----------------------------
alter table `api_configplat_en` auto_increment=1;

-- ----------------------------
-- Auto increment value for `block_fork`
-- ----------------------------
alter table `block_fork` auto_increment=1;

-- ----------------------------
-- Auto increment value for `block_rewards`
-- ----------------------------
alter table `block_rewards` auto_increment=288063;

-- ----------------------------
-- Auto increment value for `dposcandidate`
-- ----------------------------
alter table `dposcandidate` auto_increment=1;

-- ----------------------------
-- Auto increment value for `dposdeclare`
-- ----------------------------
alter table `dposdeclare` auto_increment=1;

-- ----------------------------
-- Auto increment value for `dposnode`
-- ----------------------------
alter table `dposnode` auto_increment=64;

-- ----------------------------
-- Auto increment value for `dpossigners`
-- ----------------------------
alter table `dpossigners` auto_increment=1;

-- ----------------------------
-- Auto increment value for `dposvotes`
-- ----------------------------
alter table `dposvotes` auto_increment=4;

-- ----------------------------
-- Auto increment value for `dposvoteswallet`
-- ----------------------------
alter table `dposvoteswallet` auto_increment=1;

-- ----------------------------
-- Auto increment value for `job_config`
-- ----------------------------
alter table `job_config` auto_increment=1;

-- ----------------------------
-- Auto increment value for `job_config_history`
-- ----------------------------
alter table `job_config_history` auto_increment=1;

-- ----------------------------
-- Auto increment value for `namespace_info`
-- ----------------------------
alter table `namespace_info` auto_increment=1;

-- ----------------------------
-- Auto increment value for `namespace_version_mapping`
-- ----------------------------
alter table `namespace_version_mapping` auto_increment=1;

-- ----------------------------
-- Auto increment value for `namespace_zkcluster_mapping`
-- ----------------------------
alter table `namespace_zkcluster_mapping` auto_increment=1;

-- ----------------------------
-- Auto increment value for `utg_bandwidth_config`
-- ----------------------------
alter table `utg_bandwidth_config` auto_increment=5;

-- ----------------------------
-- Auto increment value for `utg_clt_storagedata`
-- ----------------------------
alter table `utg_clt_storagedata` auto_increment=3;

-- ----------------------------
-- Auto increment value for `utg_storage_miner`
-- ----------------------------
alter table `utg_storage_miner` auto_increment=40;

-- ----------------------------
-- Auto increment value for `utg_node_miner`
-- ----------------------------
alter table `utg_node_miner` auto_increment=3;

-- ----------------------------
-- Auto increment value for `node_exit`
-- ----------------------------
alter table `node_exit` auto_increment=1;

-- ----------------------------
-- Auto increment value for `pageslide`
-- ----------------------------
alter table `pageslide` auto_increment=1;

-- ----------------------------
-- Auto increment value for `pledge_data`
-- ----------------------------
alter table `pledge_data` auto_increment=1;

-- ----------------------------
-- Auto increment value for `pledge_total_data`
-- ----------------------------
alter table `pledge_total_data` auto_increment=1;

-- ----------------------------
-- Auto increment value for `punished`
-- ----------------------------
alter table `punished` auto_increment=1;

-- ----------------------------
-- Auto increment value for `release_version_info`
-- ----------------------------
alter table `release_version_info` auto_increment=1;

-- ----------------------------
-- Auto increment value for `saturn_dashboard_history`
-- ----------------------------
alter table `saturn_dashboard_history` auto_increment=1;

-- ----------------------------
-- Auto increment value for `saturn_statistics`
-- ----------------------------
alter table `saturn_statistics` auto_increment=1;

-- ----------------------------
-- Auto increment value for `stakingdelegators`
-- ----------------------------
alter table `stakingdelegators` auto_increment=1;

-- ----------------------------
-- Auto increment value for `sys_admin`
-- ----------------------------
alter table `sys_admin` auto_increment=1;

-- ----------------------------
-- Auto increment value for `sys_admin_role`
-- ----------------------------
alter table `sys_admin_role` auto_increment=1;

-- ----------------------------
-- Auto increment value for `sys_config`
-- ----------------------------
alter table `sys_config` auto_increment=1;

-- ----------------------------
-- Auto increment value for `sys_permission`
-- ----------------------------
alter table `sys_permission` auto_increment=1;

-- ----------------------------
-- Auto increment value for `sys_role`
-- ----------------------------
alter table `sys_role` auto_increment=1;

-- ----------------------------
-- Auto increment value for `sys_role_permission`
-- ----------------------------
alter table `sys_role_permission` auto_increment=1;

-- ----------------------------
-- Auto increment value for `t_sys_cfg`
-- ----------------------------
alter table `t_sys_cfg` auto_increment=11;

-- ----------------------------
-- Auto increment value for `temporary_shared_status`
-- ----------------------------
alter table `temporary_shared_status` auto_increment=1;

-- ----------------------------
-- Auto increment value for `token_contract`
-- ----------------------------
alter table `token_contract` auto_increment=1;

-- ----------------------------
-- Auto increment value for `tokeninstances`
-- ----------------------------
alter table `tokeninstances` auto_increment=1;

-- ----------------------------
-- Auto increment value for `tokenmarket`
-- ----------------------------
alter table `tokenmarket` auto_increment=1;

-- ----------------------------
-- Auto increment value for `trans_logs`
-- ----------------------------
alter table `trans_logs` auto_increment=3751;

-- ----------------------------
-- Auto increment value for `trans_token`
-- ----------------------------
alter table `trans_token` auto_increment=3437;

-- ----------------------------
-- Auto increment value for `transfer_miner`
-- ----------------------------
alter table `transfer_miner` auto_increment=10;

-- ----------------------------
-- Auto increment value for `transferminer`
-- ----------------------------
alter table `transferminer` auto_increment=1;

-- ----------------------------
-- Auto increment value for `transforks`
-- ----------------------------
alter table `transforks` auto_increment=1;

-- ----------------------------
-- Auto increment value for `transinternal`
-- ----------------------------
alter table `transinternal` auto_increment=1;

-- ----------------------------
-- Auto increment value for `translogs`
-- ----------------------------
alter table `translogs` auto_increment=1;

-- ----------------------------
-- Auto increment value for `transtoken`
-- ----------------------------
alter table `transtoken` auto_increment=1;

-- ----------------------------
-- Auto increment value for `zk_cluster_info`
-- ----------------------------
alter table `zk_cluster_info` auto_increment=1;


-- ----------------------------
-- Records of utg_bandwidth_config
-- ----------------------------
insert into `utg_bandwidth_config` values ('1', 'P1', '1', '300', '0.03', null);
insert into `utg_bandwidth_config` values ('2', 'P2', '301', '800', '0.80', null);
insert into `utg_bandwidth_config` values ('3', 'P3', '801', '1500', '1.20', null);
insert into `utg_bandwidth_config` values ('4', 'P4', '1500', null, '1.60', null);

insert into `transaction` values ('uxba78a093041b51720c7242ba7f5cd262ad52ea5a8eaa36fa0b09ca913fe905f2', '1', '2021-11-17 13:06:57', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', '0', '0', '21320', '176190476190', '1', '21320', '21320', 'uxe39e572d405216f544870385cdc91831976bd5396b3e8805a1c59fce432a8c9a', '0', '0', '0x5353433a313a45786368526174653a3330303030', null, null, '0', '0', 'SSC', '1', 'ExchRate', '10000', null, null, null, null, null);
insert into `transaction` values ('ux119578d0dc78b02e1fc62c10445bc6f4474526c3f6c237c2703d7e8353de5fe6', '1', '2021-11-17 13:08:47', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', '0', '1', '21352', '176190476190', '1', '21352', '21352', 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', '0', '0', '0x5353433a313a436e644c6f636b3a37383a62343a3363', null, null, '0', '0', 'SSC', '1', 'CndLock', '1555200', '0', '0', null, null, null);
insert into `transaction` values ('uxef6e3fa241571abf927935a4cb7f9d079fe986959a80da53e99bcd03472dc67e', '1', '2021-11-17 13:08:47', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', '0', '2', '21320', '176190476190', '1', '21320', '42672', 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', '0', '1', '0x5353433a313a466c774c6f636b3a37383a303a30', null, null, '0', '0', 'SSC', '1', 'FlwLock', '1555200', '0', '0', null, null, null);
insert into `transaction` values ('uxa3d23721a356ea67e9ace5995448b128bc3fe93ce37e3e4d720ff53a9cd5864f', '1', '2021-11-17 13:08:47', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', '0', '3', '21352', '176190476190', '1', '21352', '64024', 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', '0', '2', '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, '0', '0', 'SSC', '1', 'RwdLock', '259200', '1555200', '8640', null, null, null);
insert into `transaction` values ('ux2c18ff21a5f63839e3608b8cfc9cd0c363ab29e9384ea4a618f51a2b55735075', '1', '2021-11-17 13:08:47', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', '0', '7', '246930', '176190476190', '1', '21496', '21496', 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', '0', '0', '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, '0', '0', 'SSC', '1', 'Deposit', '20000000000000000000','0', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values ('ux1111111111111111111111111111111111111111111111111111111111111111', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 4, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '6', '11', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values ('ux2222222222222222222222222222222222222222222222222222222222222222', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 5, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '4', '800000000000000', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values ('ux3333333333333333333333333333333333333333333333333333333333333333', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 6, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '5', '1', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values ('ux3333333333333333333333333333333333333333333333333333333333333332', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 6, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '7', '1', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values ('ux3333333333333333333333333333333333333333333333333333333333333334', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 6, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '8', '30', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values ('ux3333333333333333333333333333333333333333333333333333333333333335', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 6, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '3', '60000000000000000000', null, null, null, null);

insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','0','0.00','29.00','0.00',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','1','29.00','50.00','0.70',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','2','50.00','99.00','0.90',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','3','99.00','100.00','1.00',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','4','100.00','500.00','1.10',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','5','500.00','1023.00','1.30',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('bandwidth','6','1023.00',null,'1.50',null);

insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','0','0.00','1023','0',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','1','1023','1024','0.10',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','2','1024','51200','0.30',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','3','51200','512000','0.50',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','4','512000','1048575','0.70',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','5','1048575','1048576','1.00',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','6','1048576','52428800','1.20',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','7','52428800','524288000','1.50',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','8','524288000','1073741824','1.80',null);
insert into `storage_config` (`type`, `seq`, `min`, `max`, `value`, `updatetime`) values('storage','9','1073741824',null,'2.00',null);

alter table utg_clt_storagedata_day 
add lockamount decimal(65,0),
add releaseamount decimal(65,0);
alter  table  transaction  add  index ufo(`status`,`ufooperator`,`param2`);
alter  table  utg_clt_storagedata_day  add  index ad_time_blk(`en_address`,`report_time`,`blocknumber`);

-- ALTER TABLE `addresses` ADD COLUMN `isinner` int(11)   NULL DEFAULT 0 after `srt_nonce` ;


alter table storage_space  add column vaild_status int null after fail_count;
alter table storage_space  add column vaild_time datetime null after vaild_status;
alter table storage_space  add column fail_days int null after vaild_time;

alter table storage_rent  add column vaild_status int null after fail_count;
alter table storage_rent  add column vaild_time datetime null after vaild_status;
alter table storage_rent  add column fail_days int null after vaild_time;

-- update transfer_miner t left join blocks b on t.blocknumber=b.blocknumber set t.curtime=b.timestamp where t.curtime is null;


create table `contract`(
	`contract` varchar(100) collate utf8_general_ci not null  , 
	`name` varchar(255) collate utf8_general_ci null  , 
	`symbol` varchar(255) collate utf8_general_ci null ,
	`txhash` varchar(255) collate utf8_general_ci null ,
	`author` varchar(255) collate utf8_general_ci null ,
	`blocknumber` bigint(20) null,	
	`type` int(11) null  , 
	`admin1` varchar(255) collate utf8_general_ci null  , 
	`admin2` varchar(255) collate utf8_general_ci null  , 
	`lockupstart` bigint(20) null  , 
	`lockupperiod` bigint(20) null  , 
	`releaseperiod` bigint(20) null  , 
	`releaseinterval` bigint(20) null  , 
	`createtime` datetime null  , 
	`updatetime` datetime null  , 
	primary key (`contract`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';

create table `contract_lockup`(
	`id` bigint(20) not null  auto_increment , 
	`contract` varchar(255) collate utf8_general_ci null  , 
	`address` varchar(255) collate utf8_general_ci null  , 
	`txhash` varchar(255) collate utf8_general_ci null  , 
	`type` int(11) null  , 
	`lockupnumber` bigint(20) null  , 
	`lockupamount` decimal(65,0) null  , 
	`pickupamount` decimal(65,0) null  , 
	`remainamount` decimal(65,0) null  , 
	`lockupperiod` bigint(20) null  , 
	`releaseperiod` bigint(20) null  , 
	`releaseinterval` bigint(20) null  , 
	`cancelnumber` bigint(20) null  , 
	`cancelamount` decimal(65,0) null  , 
	`createtime` datetime null  , 
	`updatetime` datetime null  , 
	primary key (`id`) , 
	unique key `contract`(`contract`,`address`,`txhash`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';

create table `contract_source`(
	`id` bigint(20) not null  , 
	`contract` varchar(255) collate utf8_general_ci null  , 
	`filename` varchar(255) collate utf8_general_ci null  , 
	`sourcecode` longtext collate utf8_general_ci null  , 
	`ord` int(11) null  , 
	primary key (`id`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';

alter table `storage_space` 
	add column `vaild_progress` decimal(10,0)   null after `vaild_time` , 
	change `fail_days` `fail_days` int(11)   null after `vaild_progress` ;
	
alter table `storage_rent`   
  add column `renew_status` int null after `rent_status`,
  add column `renew_reqhash` varchar(100) null after `renew_status`;

alter table `storage_request`   
  add column `reqhash` varchar(100) null after `reqid`;


-- since 2022-05-30

alter table `addresses`   
  add column `balance_block` bigint null after `haslock`,
  add column `srt_block` bigint null after `srt_nonce`;
  
alter table `transaction` 
	change `input` `input` longtext  collate utf8_general_ci null after `blockindex` ,
	change `param5` `param5` varchar(5000) charset utf8 collate utf8_general_ci null,
	change `param6` `param6` varchar(5000) charset utf8 collate utf8_general_ci null;
	
alter table `tokens` 
	change `cataloged` `cataloged` tinyint(4)   null after `totalsupply` ;

alter table `storage_space` 
	add column `vaild24_status` int(11)   null after `vaild_status` ,
	add unique index `device_pledge` (`device_addr`, `pledge_number`);

alter table `storage_rent` 
	add column `pledge_addr` varchar(60) null after `rent_addr`,
	add column `renew_number` bigint null after `renew_reqhash`,
	add column `renew_time` int null after `renew_number`,
	add column `vaild24_status` int(11)   null after `vaild_status` ,
	drop index `rent_hash`,
    add unique index `rent_hash` (`rent_hash`);
	
alter table `storage_request`   
  add  unique index `reqhash` (`reqhash`);
  

-- sine 2022-06-09
alter table `addresses` 
	change `blocknumber` `blocknumber` bigint(20)   null default 0 after `nonce` ;
	
alter table `contract` 
	add column  `contractname` varchar(255) null after `contract`,
	change `name` `name` varchar(255)  null after `contractname` , 
	add column `verified` int(11)   null default 0 after `blocknumber` , 
	add column `version` varchar(100)  null after `verified` , 
	add column `verifydate` datetime null after `version`,
	add column `istoken` int default 0  null after `verifydate`, 
	change `type` `type` int(11)   null after `istoken` ;

alter table `contract_source` 
	change `id` `id` bigint(20)   not null auto_increment first ,
	add column `bin` longtext  null after `sourcecode` , 
	add column `abi` longtext  null after `bin` , 
	change `ord` `ord` int(11)   null after `abi` ;
	
create table `contract_version` (
  `version` varchar(100) not null,
  `vername` varchar(255) default null,
  `metadata_bytecode` varchar(255) default null,
  `status` int(11) default '1',
  `ord` int(11) default null,
  primary key (`version`)
) engine=innodb default charset='utf8' collate='utf8_general_ci';


create table `storage_space_stat` (
  `id` bigint(20) not null auto_increment,
  `blocknumber` bigint(20) not null,
  `blocktime` datetime default null,
  `sttime` datetime default null,
  `storageid` bigint(20) not null,
  `device_addr` varchar(100) not null,
  `revenue_addr` varchar(100) default null,
  `pledge_status` int(11) default null,
  `rent_space` decimal(60,0) default null,
  `rent_price` decimal(40,0) default null,
  `rent_num` bigint(20) default null,
  `bw_size` decimal(10,2) default null,
  `bw_ratio` decimal(10,2) default null,
  `valid_number` bigint(20) default null,
  `vaild_status` int(11) default null,
  `vaild_progress` decimal(10,0) default null,
  `total_amount` decimal(65,0) default null,
  `storage_amount` decimal(65,0) default null,
  `rent_amount` decimal(65,0) default null,
  primary key (`id`),
  unique key `UNIQUE` (`blocknumber`,`device_addr`),
  key `blocknumber` (`blocknumber`),
  key `device_addr` (`device_addr`)
) engine=innodb default charset='utf8' collate='utf8_general_ci';


create table `storage_revenue_stat` (
  `id` bigint(20) not null auto_increment,
  `blocknumber` bigint(20) not null,
  `blocktime` datetime default null,
  `sttime` datetime default null,
  `revenue_addr` varchar(100) not null,
  `ratio` decimal(10,2) default null,
  `capacity` decimal(60,0) default null,
  `pledge_amount` decimal(65,0) default null,
  `total_amount` decimal(65,0) default null,
  `storage_amount` decimal(65,0) default null,
  `rent_amount` decimal(65,0) default null,
  primary key (`id`),
  unique key `UNIQUE` (`blocknumber`,`revenue_addr`),
  key `blocknumber` (`blocknumber`),
  key `revenue_addr` (`revenue_addr`)
) engine=innodb default charset='utf8' collate='utf8_general_ci';


-- sine 2022-06-24
delete from contract_version;
insert into contract_version (version,vername,metadata_bytecode,status,ord) values
('0.8.14','0.8.14+commit.80d49f37','a264697066735822',1,814),
('0.8.13','0.8.13+commit.abaa5c0e','a264697066735822',1,813),
('0.8.12','0.8.12+commit.f00d7308','a264697066735822',1,812),
('0.8.11','0.8.11+commit.d7f03943','a264697066735822',1,811),
('0.8.10','0.8.10+commit.fc410830','a264697066735822',1,810),
('0.8.9','0.8.9+commit.e5eed63a','a264697066735822',1,809),
('0.8.8','0.8.8+commit.dddeac2f','a264697066735822',1,808),
('0.8.7','0.8.7+commit.e28d00a7','a264697066735822',1,807),
('0.8.6','0.8.6+commit.11564f7e','a264697066735822',1,806),
('0.8.5','0.8.5+commit.a4f2e591','a264697066735822',1,805),
('0.8.4','0.8.4+commit.c7e474f2','a264697066735822',1,804),
('0.8.3','0.8.3+commit.8d00100c','a264697066735822',1,803),
('0.8.2','0.8.2+commit.661d1103','a264697066735822',1,802),
('0.8.1','0.8.1+commit.df193b15','a264697066735822',1,801),
('0.8.0','0.8.0+commit.c7dfd78e','a264697066735822',1,800),
('0.7.6','0.7.6+commit.7338295f','a264697066735822',1,706),
('0.7.5','0.7.5+commit.eb77ed08','a264697066735822',1,705),
('0.7.4','0.7.4+commit.3f05b770','a264697066735822',1,704),
('0.7.3','0.7.3+commit.9bfce1f6','a264697066735822',1,703),
('0.7.2','0.7.2+commit.51b20bc0','a264697066735822',1,702),
('0.7.1','0.7.1+commit.f4a555be','a264697066735822',1,701),
('0.7.0','0.7.0+commit.9e61f92b','a264697066735822',1,700),
('0.6.12','0.6.12+commit.27d51765','a264697066735822',1,612),
('0.6.11','0.6.11+commit.5ef660b1','a264697066735822',1,611),
('0.6.10','0.6.10+commit.00c0fcaf','a264697066735822',1,610),
('0.6.9','0.6.9+commit.3e3065ac','a264697066735822',1,609),
('0.6.8','0.6.8+commit.0bbfe453','a264697066735822',1,608),
('0.6.7','0.6.7+commit.b8d736ae','a264697066735822',1,607),
('0.6.6','0.6.6+commit.6c089d02','a264697066735822',1,606),
('0.6.5','0.6.5+commit.f956cc89','a264697066735822',1,605),
('0.6.4','0.6.4+commit.1dca32f3','a264697066735822',1,604),
('0.6.3','0.6.3+commit.8dda9521','a264697066735822',1,603),
('0.6.2','0.6.2+commit.bacdbe57','a264697066735822',1,602),
('0.6.1','0.6.1+commit.e6f7d5a4','a264697066735822',1,601),
('0.6.0','0.6.0+commit.26b70077','a264697066735822',1,600),
('0.5.17','0.5.17+commit.d19bba13','a265627a7a72315820',1,517),
('0.5.16','0.5.16+commit.9c3226ce','a265627a7a72315820',1,516),
('0.5.15','0.5.15+commit.6a57276f','a265627a7a72315820',1,515),
('0.5.14','0.5.14+commit.01f1aaa4','a265627a7a72315820',1,514),
('0.5.13','0.5.13+commit.5b0b510c','a265627a7a72315820',1,513),
('0.5.12','0.5.12+commit.7709ece9','a265627a7a72315820',1,512),
('0.5.11','0.5.11+commit.22be8592','a265627a7a72305820',1,511),
('0.5.10','0.5.10+commit.5a6ea5b1','a265627a7a72305820',1,510),
('0.5.9','0.5.9+commit.c68bc34e','a265627a7a72305820',1,509),
('0.5.8','0.5.8+commit.23d335f2','a165627a7a72305820',1,508),
('0.5.7','0.5.7+commit.6da8b019','a165627a7a72305820',1,507),
('0.5.6','0.5.6+commit.b259423e','a165627a7a72305820',1,506),
('0.5.5','0.5.5+commit.47a71e8f','a165627a7a72305820',1,505),
('0.5.4','0.5.4+commit.9549d8ff','a165627a7a72305820',1,504),
('0.5.3','0.5.3+commit.10d17f24','a165627a7a72305820',1,503),
('0.5.2','0.5.2+commit.1df8f40c','a165627a7a72305820',1,502),
('0.5.1','0.5.1+commit.c8a2cb62','a165627a7a72305820',1,501),
('0.5.0','0.5.0+commit.1d4f565a','a165627a7a72305820',1,500),
('0.4.26','0.4.26+commit.4563c3fc','a165627a7a72305820',1,426),
('0.4.25','0.4.25+commit.59dbf8f1','a165627a7a72305820',1,425),
('0.4.24','0.4.24+commit.e67f0147','a165627a7a72305820',1,424),
('0.4.23','0.4.23+commit.124ca40d','a165627a7a72305820',1,423),
('0.4.22','0.4.22+commit.4cb486ee','a165627a7a72305820',1,422),
('0.4.21','0.4.21+commit.dfe3193c','a165627a7a72305820',1,421),
('0.4.20','0.4.20+commit.3155dd80','a165627a7a72305820',1,420),
('0.4.19','0.4.19+commit.c4cbbb05','a165627a7a72305820',1,419),
('0.4.18','0.4.18+commit.9cf6e910','a165627a7a72305820',1,418),
('0.4.17','0.4.17+commit.bdeb9e52','a165627a7a72305820',1,417),
('0.4.16','0.4.16+commit.d7661dd9','a165627a7a72305820',0,416),
('0.4.15','0.4.15+commit.8b45bddb','a165627a7a72305820',0,415),
('0.4.14','0.4.14+commit.c2215d46','a165627a7a72305820',0,414),
('0.4.13','0.4.13+commit.0fb4cb1a','a165627a7a72305820',0,413),
('0.4.12','0.4.12+commit.194ff033','a165627a7a72305820',0,412),
('0.4.11','0.4.11+commit.68ef5810','a165627a7a72305820',0,411)
;

insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values 
('ux3333333333333333333333333333333333333333333333333333333333333310', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 6, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '10', '5', null, null, null, null);
insert into `transaction`(`hash`, `istrunk`, `timestamp`, `fromaddr`, `toaddr`, `value`, `nonce`, `gaslimit`, `gasprice`, `status`, `gasused`, `cumulative`, `blockhash`, `blocknumber`, `blockindex`, `input`, `contract`, `error`, `internal`, `type`, `ufoprefix`, `ufoversion`, `ufooperator`, `param1`, `param2`, `param3`, `param4`, `param5`, `param6`) values 
('ux3333333333333333333333333333333333333333333333333333333333333311', 1, '2022-03-18 16:01:51', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 'ux7a4539ed8a0b8b4583ead1e5a3f604e83419f502', 0, 6, 0, 0, 1, 0, 0, 'uxaeded418ba96481e1b88ef78ca9c779753faf5fa79300f394242a8fb5769beac', 0, 0, '0x5353433a313a5277644c6f636b3a37383a62343a3363', null, null, 0, 0, 'UTG', '1', 'stset', '11', '360', null, null, null, null);


alter table `storage_revenue_stat` 
	add column `storage_num` bigint(20)   null after `capacity` , 
	add column `rent_num` bigint(20)   null after `storage_num` , 
	add column `storage_space` decimal(65,0)   null after `rent_num` , 
	add column `rent_space` decimal(65,0)   null after `storage_space` , 
	change `pledge_amount` `pledge_amount` decimal(65,0)   null after `rent_space` , 
	change `storage_amount` `storage_amount` decimal(65,0)   null after `pledge_amount` , 
	add column `storage_release` decimal(65,0)   null after `storage_amount` , 
	change `rent_amount` `rent_amount` decimal(65,0)   null after `storage_release` , 
	add column `rent_release` decimal(65,0)   null after `rent_amount` , 
	drop column `total_amount` ;

  
create table `storage_global_stat`(
	`id` bigint(20) not null  auto_increment , 
	`blocknumber` bigint(20) not null  , 
	`blocktime` datetime null  , 
	`sttime` datetime null  , 
	`ratio` decimal(10,2) null  , 
	`capacity` decimal(60,0) null  , 
	`revenue_num` bigint(20) null  , 
	`storage_num` bigint(20) null  , 
	`rent_num` bigint(20) null  , 
	`increase_space` decimal(65,0) null  , 
	`storage_space` decimal(65,0) null  , 
	`rent_space` decimal(65,0) null  , 
	`pledge_amount` decimal(65,0) null  , 
	`storage_amount` decimal(65,0) null  , 
	`storage_release` decimal(65,0) null  , 
	`rent_amount` decimal(65,0) null  , 
	`rent_release` decimal(65,0) null  , 
	primary key (`id`) , 
	unique key `blocknumber`(`blocknumber`) 
) engine=innodb default charset='utf8' collate='utf8_general_ci';



-- since 2022-07-13
alter table `transaction`   
  add index `contract` (`contract`);
  
alter table `storage_space` 
	add column `prepledge_amount` decimal(60,0)   null after `pledge_amount` , 
	change `pensate_amount` `pensate_amount` decimal(60,0)   null after `prepledge_amount` , 
	change `bw_ratio` `bw_ratio` decimal(10,4)   null after `bw_size` , 
	add column `reward_ratio` decimal(10,5)   null after `bw_ratio` , 
	change `valid_number` `valid_number` bigint(20)   null after `reward_ratio` ;
  
alter table `transfer_miner` 
	add column `burntratio` decimal(10,6)   null default 0.000000 after `revenueaddress` , 
	add column `burntaddress` varchar(60)  collate utf8_general_ci null after `burntratio` , 
	add column `burntamount` decimal(65,0)   null default 0 after `burntaddress` ;  


-- since 2022-07-28
alter table `storage_space`   
  add column `bw_changed` int default 0  null after `reward_ratio`;
  
update storage_space t set t.rent_price=1000000000000000    where t.rent_price=500000000000000;

alter table `storage_rent`   
  add column `pledge_amount` decimal(60,0) null after `pledge_addr`;


-- since 2022-08-09
alter table `trans_token`
  change `logindex` `logindex` int(11) null,
  change `amount` `amount` decimal(65,0) null,
  change `contract` `contract` varchar(100) charset utf8 collate utf8_general_ci null,
  change `blockhash` `blockhash` varchar(100) charset utf8 collate utf8_general_ci null,
  change `blocknumber` `blocknumber` bigint(20) null,
  change `gasused` `gasused` bigint(20) default 0  null,
  change `gasprice` `gasprice` bigint(20) default 0  null,
  change `gaslimit` `gaslimit` bigint(20) default 0  null,
  change `nonce` `nonce` bigint(20) default 0  null,
  change `timestamp` `timestamp` datetime null,	
  add column `value` decimal(65,0) null after `timestamp`,
  add column `operator` varchar(100) null after `value`,
  add column `owner` varchar(100) null after `operator`;
   
  
alter table `addresses`   
  add column `isstorage` int(11) default 0  null after `isinner`,
  add column `isrevenue` int(11) default 0  null after `isstorage`,
  add column `isminer` int(11) default 0  null after `isrevenue`,
  add column `iscontract` int(11) default 0  null after `isminer`;

update addresses set isstorage=1 where address in (select device_addr from storage_space);  
update addresses set isrevenue=1 where address in (select revenue_address from utg_storage_miner);
update addresses set isminer=1 where address in (select node_address from utg_node_miner);
update addresses set iscontract=1 where address in (select contract from contract);

update contract set type=0 where contract in (select contract from tokens);

alter table `storage_global_stat`   
  change `blocknumber` `blocknumber` bigint(20) null,
  add column `increase_rent` decimal(65,0) null after `increase_space`;

insert into storage_global_stat (blocktime,sttime,storage_space,increase_space) select ctime,ctime,total_bw,incre_bw from utg_net_statics;


-- since 2022-09-28
alter table `utg_node_miner`   
  add column `manage_address` varchar(100) null after `revenue_address`,
  add column `rate` int(10) null after `fractions`,
  add column `total_amount` decimal(65,0) null after `pledge_amount`,  
  add column `punish_block` bigint(20) null after `sync_time`,
  add column `exit_type` int(10) null after `punish_block`;

create table `node_pledge` (
  `id` bigint(20) not null auto_increment,
  `node_address` varchar(60) default null,
  `pledge_address` varchar(60) default null,
  `pledge_hash` varchar(200) default null,
  `pledge_amount` decimal(65,0) default null,
  `pledge_status` int(11) default null,
  `pledge_number` bigint(20) default null,
  `pledge_time` datetime default null,
  `unpledge_type` int default null,
  `unpledge_hash` varchar(200) default null,
  `unpledge_number` bigint(20) default null,
  `unpledge_time` datetime default null,
  primary key (`id`),
  unique key `pledge_hash` (`pledge_hash`)
) engine=innodb default charset=utf8;

update utg_node_miner set manage_address=revenue_address where manage_address is null or manage_address ='';
update utg_node_miner set manage_address=node_address where manage_address is null or manage_address ='';
update utg_node_miner set total_amount=pledge_amount where total_amount is null;

create table `transfer_revenueaddress_exclude` (
  `revenueaddress` varchar(60) not null,
  primary key (`revenueaddress`)
) engine=innodb default charset=utf8;

create table transfer_miner_exclude like transfer_miner;

alter table `storage_rent`   
  add column `attach_text` varchar(1000) null after `updatetime`,
  add column `attach_pic` blob null after `attach_text`,
  add column `attach_picmd5` varchar(64) null after `attach_pic`,
  add column `attach_time` datetime null after `attach_picmd5`;


create table `stat_global` (
  `id` int(11) not null,
  `blocknumber` bigint(20) default null,
  `tx_count` bigint(20) default null,
  `total_locked` decimal(65,0) default null,
  `total_burnt` decimal(65,0) default null,
  `total_pledge` decimal(65,2) default null,
  `total_storage` decimal(65,0) default null,
  `storage_amount` decimal(65,0) default null,
  `last_mintage` decimal(65,0) default null,
  `next_election` varchar(64) default null,

  primary key (`id`)
) engine=innodb default charset=utf8;

alter table blocks engine = myisam;
alter table transaction engine = myisam;

create table `transfer_miner_release` (
  `id` bigint(20) not null auto_increment,
  `txhash` varchar(100) not null,
  `type` int(11) not null ,
  `logindex` int(11) default '0' ,
  `address` varchar(255) default null,
  `value` decimal(65,0) default '0',
  `blockhash` varchar(100) default null,
  `blocknumber` bigint(20) default '0',
  `totalamount` decimal(65,0) default '0',
  `leftamount` decimal(65,0) default '0',
  `status` int(11) default '0',
  `gaslimit` bigint(20) default '0',
  `gasused` bigint(20) default '0',
  `gasprice` bigint(20) default '0',
  `unlocknumber` bigint(20) default '0',
  `loglength` bigint(20) default '0',
  `starttime` datetime default null,
  `nodenumber` varchar(255) default null,
  `presentamount` decimal(65,0) default '0',
  `locknumheigth` bigint(20) default '0',
  `pledgeamount` decimal(65,0) default '0',
  `pledgeaddress` varchar(255) default null,
  `punilshamount` decimal(65,0) default '0',
  `receiveaddress` varchar(255) default null,
  `releaseheigth` bigint(20) default '0',
  `releaseinterval` bigint(20) default '0',
  `pledgetotalamount` decimal(65,0) default '0',
  `curtime` datetime default null,
  `releaseamount` decimal(65,0) default '0',
  `revenueaddress` varchar(60) default null,
  `burntratio` decimal(10,6) default '0.000000',
  `burntaddress` varchar(60) default null,
  `burntamount` decimal(65,0) default '0',
  primary key (`id`),
  key `type` (`type`) using btree,
  key `add_type_blocknumber` (`type`,`address`,`blocknumber`),
  key `address` (`address`),
  key `revenueaddr` (`revenueaddress`),
  key `blocknumber` (`blocknumber`),
  key `curtime` (`curtime`)
) engine=innodb default charset=utf8 comment='Lock release data table';


alter table `stat_global`   
  add column `total_amount` decimal(65,0) null after `next_election`,
  add column `total_release` decimal(65,0) null after `total_amount`,
  add column `block_amount` decimal(65,0) null after `total_release`,
  add column `block_release` decimal(65,0) null after `block_amount`,
  add column `block_burnt` decimal(65,0) null after `block_release`,
  change `storage_amount` `storage_amount` decimal(65,0) null  after `block_burnt`,
  add column `storage_release` decimal(65,0) null after `storage_amount`,
  add column `storage_burnt` decimal(65,0) null after `storage_release`,
  add column `rent_amount` decimal(65,0) null after `storage_burnt`,
  add column `rent_release` decimal(65,0) null after `rent_amount`,
  add column `rent_burnt` decimal(65,0) null after `rent_release`,
  add column `tx_burnt` decimal(65,0) null after `rent_burnt`,
  add column `reward_burnt` decimal(65,0) null after `tx_burnt`,
  add column `fee_burnt` decimal(65,0) null after `reward_burnt`,
  add column `pledge_burnt` decimal(65,0) null after `fee_burnt`,
  add column `lease_his` decimal(65,0) null after `pledge_burnt`;

alter table `storage_revenue`   
  add column `storage_amount` decimal(60,0) null after `capacity`,
  add column `storage_release` decimal(60,0) null after `storage_amount`,
  add column `storage_burnt` decimal(60,0) null after `storage_release`,
  add column `rent_amount` decimal(60,0) null after `storage_burnt`,
  add column `rent_release` decimal(60,0) null after `rent_amount`,
  add column `rent_burnt` decimal(60,0) null after `rent_release`;

alter table `storage_space`   
  add column `storage_release` decimal(60,0) null after `storage_amount`,
  add column `storage_burnt` decimal(60,0) null after `storage_release`,
  add column `rent_release` decimal(60,0) null after `rent_amount`,
  add column `rent_burnt` decimal(60,0) null after `rent_release`;

alter table `contract`   
  change `type` `type` varchar(255) null;
 
alter table `contract_lockup`   
  add column `logindex` int null after `txhash`, 
  drop index `contract`,
  add  unique index `txlog` (`txhash`, `logindex`);

alter table `transaction` add index (`ufooperator`);

 create table `global_config` (
  `type` varchar(64) character set utf8mb4 not null,
  `value` varchar(255) character set utf8mb4 not null,
  `txhash` varchar(255) character set utf8mb4 default null,
  `blocknumber` bigint(20) default null,
  `updatetime` datetime default null,
  primary key (`type`)
) engine=innodb default charset=utf8; 

insert into `global_config` (`type`, `value`) select param1 type,param2 value from transaction a where a.ufooperator ='stset' and status=1;
insert into `global_config` (`type`, `value`) select 'ExchRate' type,param1 value from transaction a where a.ufooperator ='ExchRate' and status=1 order  by a.blocknumber desc limit 1;


alter table `storage_space`   
  add column `attach_text` varchar(1000) null after `updatetime`,
  add column `attach_pic` blob null after `attach_text`,
  add column `attach_picmd5` varchar(64) null after `attach_pic`,
  add column `attach_time` datetime null after `attach_picmd5`;

ALTER TABLE storage_space 
  add manager_addr varchar(60),
  add entrust_rate int(6),
  add sp_hash varchar(100),
  add sp_height BIGINT(20),
  add sp_jointime datetime DEFAULT NULL,
  add hav_amount decimal(60,0),
  add manager_amount decimal(60,0),
  add manager_height BIGINT(20),
  add active_height BIGINT(20),
  add sn_ratio decimal(20,6);
update  storage_space  t set t.manager_addr=pledge_addr where t.manager_addr is null ;
ALTER TABLE node_pledge 
  add node_type varchar(10) COMMENT 'SP/SN/PoS',
  add sp_hash varchar(120);
update node_pledge  t set t.node_type='PoS' where t.node_type is null ;
CREATE TABLE `storage_pool` (
  `hash` varchar(120) NOT NULL,
  `sp_addr` varchar(60) DEFAULT NULL,
  `manager_addr` varchar(60) DEFAULT NULL,
  `pledge_addr` varchar(60) DEFAULT NULL,
  `revenue_addr` varchar(60) DEFAULT NULL,
  `active_status` int(6) DEFAULT NULL COMMENT '0 inactive 1active   2 wait exit 3 exit',
  `total_capacity` decimal(60,0) DEFAULT NULL,
  `used_capacity` decimal(60,0) DEFAULT NULL,
  `pledge_amount` decimal(60,0) DEFAULT NULL,
  `hav_amount` decimal(60,0) DEFAULT NULL,
  `manager_amount` decimal(30,0) DEFAULT NULL,
  `sp_amount` decimal(60,0) DEFAULT NULL COMMENT 'reward',
  `sp_release` decimal(60,0) DEFAULT NULL COMMENT 'release',
  `sp_burnt` decimal(60,0) DEFAULT NULL,
  `fee_rate` int(6) DEFAULT NULL,
  `entrust_rate` int(6) DEFAULT NULL,
  `storage_ratio` decimal(10,6) DEFAULT NULL,
  `active_height` bigint(30) DEFAULT NULL,
	`createtime` datetime DEFAULT NULL,
  `instime` datetime DEFAULT NULL,
	`sn_num` int(6) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE stat_global 
   add sp_amount decimal(60,0)  after `lease_his`,
	 add sp_release decimal(60,0)  after `sp_amount`,
   add sp_burnt decimal(60,0)  after `sp_release`;
   ALTER TABLE node_pledge MODIFY COLUMN node_address varchar(120);
   insert into global_config (type,value) VALUES("spentrustday",7);
insert into global_config (type,value) VALUES("snentrustday",7);
insert into global_config (type,value) VALUES("posentrustday",30);
insert into global_config (type,value) VALUES("utgv2number",38);
insert into global_config (type,value) VALUES("snJoinSpday",7);
insert into global_config (type,value) VALUES("spexitday",7);  

CREATE TABLE `node_reward`  (
  `reward_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `target_address` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `node_address` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `revenue_address` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `node_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `reward_type` int(8) DEFAULT NULL,
  `leiji_amount` decimal(30, 0) DEFAULT NULL,
  `reward_amount` decimal(30, 0) DEFAULT NULL,
  `block_number` bigint(30) DEFAULT NULL,
  `reward_time` datetime(0) DEFAULT NULL,
  `instime` datetime(0) DEFAULT NULL,
  PRIMARY KEY (`reward_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 215 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;