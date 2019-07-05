/*创建序列*/
CREATE SEQUENCE ppm_seq INCREMENT BY 1 START WITH 1000 ; 
CREATE SEQUENCE ppm_order_num_seq INCREMENT BY 1 START WITH 100 MAXVALUE 99999 CYCLE;

/*模板表*/
CREATE TABLE ppm_template(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	name varchar2(30),
	creator varchar2(10)
);

/*工序表*/

CREATE TABLE ppm_working_procedure(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10),
	name varchar(30)
);

/*工序模板关系表*/
CREATE TABLE ppm_template_work_link(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10),
	template_id number(7),
	tw_id number(7),
	ppm_order number(5)
);
/*创建工序检验特性表*/
CREATE TABLE ppm_characteristic(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10),
	tw_id number(7),
	name varchar2(30),
	total number(10),
	coefficient NUMBER(3),
	ppm_order number(5)
);
ALTER TABLE ppm_characteristic ADD( del_flag number(1) DEFAULT 0);
/*创建型号表*/
CREATE TABLE ppm_model(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10),
	name varchar2(30),
	model_code varchar2(30)
);

/*创建产品表*/
CREATE TABLE ppm_product(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10),
	name varchar2(30),
	model_id number(7),
	product_code varchar2(30),
	model_type varchar2(10),
	batch varchar2(30)
);
/*表单定义表*/
CREATE TABLE ppm_form(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10) ,
	product_id number(7) ,
	chara_id number(7),
	tw_id number(7) ,
	ppm_order number(5) 
);
COMMENT ON TABLE ppm_form IS '表单定义表';
COMMENT ON COLUMN ppm_form.creator IS '创建者';
COMMENT ON COLUMN ppm_form.product_id IS '产品id';
COMMENT ON COLUMN ppm_form.chara_id IS '检验性特性ID';
COMMENT ON COLUMN ppm_form.tw_id IS '工序ID';
COMMENT ON COLUMN ppm_form.ppm_order IS '排序数字';
/*表单数据实例*/
CREATE TABLE ppm_data_instance(
	updateTime timestamp  default current_timestamp,
	createTime timestamp default current_timestamp,
	id number(7)   primary key not NULL,
	creator varchar2(10) ,
	product_id number(7),
	chara_id number(7),
	defect_number number(7),
	tw_id number(7),
	ppm_order number(5)
);
COMMENT ON COLUMN ppm_data_instance.defect_number IS '检验特性的缺陷数';



/*追加外键*/
/*alter table  books add constraint fk_book_categoryid foreign key(categoryid ) references category(id);*/
ALTER TABLE ppm_template_work_link ADD CONSTRAINT seq_te FOREIGN KEY (template_id) REFERENCES ppm_template(id);/*模板外键*/
ALTER TABLE PPM_TEMPLATE_WORK_LINK ADD CONSTRAINT ppm_foreign_key_2 FOREIGN KEY(tw_id) REFERENCES ppm_working_procedure(id);
ALTER TABLE ppm_characteristic ADD CONSTRAINT ppm_foreign_key_3 FOREIGN KEY(tw_id) REFERENCES ppm_working_procedure(id); 
ALTER TABLE ppm_product ADD CONSTRAINT ppm_foreign_key_4 FOREIGN KEY(model_id) REFERENCES ppm_model(id);
ALTER TABLE ppm_form ADD CONSTRAINT ppm_foreign_key_5 FOREIGN KEY (product_id) REFERENCES PPM_PRODUCT(id);
ALTER TABLE ppm_form ADD CONSTRAINT ppm_foreign_key_6 FOREIGN KEY (chara_id) REFERENCES ppm_characteristic(id);
ALTER TABLE ppm_form ADD CONSTRAINT ppm_foreign_key_7 FOREIGN KEY (tw_id) REFERENCES ppm_working_procedure(id);
ALTER TABLE ppm_data_instance ADD CONSTRAINT ppm_foreign_key_8 FOREIGN KEY (product_id) REFERENCES PPM_PRODUCT(id);
ALTER TABLE ppm_data_instance ADD CONSTRAINT ppm_foreign_key_9 FOREIGN KEY (chara_id) REFERENCES ppm_characteristic(id);
ALTER TABLE ppm_data_instance ADD CONSTRAINT ppm_foreign_key_10 FOREIGN KEY (tw_id) REFERENCES ppm_working_procedure(id);


/*对产品表进行结构更改 2019年6月15日17:29:35 by 张少波 start*/
/*删除产品表里面对型号的外键约束*/
ALTER TABLE PPM_PRODUCT DROP CONSTRAINT PPM_FOREIGN_KEY_4;
/*删除产品表的旧字段model_id*/
ALTER TABLE PPM_PRODUCT DROP COLUMN model_id;
/*增加产品表“model_id”，其类型为varchar2(100)*/
ALTER TABLE PPM_PRODUCT ADD  (model_id varchar2(100));
/*对产品表进行结构更改 2019年6月15日17:29:35 by 张少波 end*/

/*修改表单定义表ppm_form的表结构 2019年6月16日11:25:58 by 任凯 start*/
/*新增生产批次字段*/
ALTER TABLE PPM_FORM ADD batch varchar2(10);
/*新增生产数量字段*/
ALTER TABLE PPM_FORM ADD quantity NUMBER(7);
/*新增类别字段*/
ALTER TABLE PPM_FORM ADD category varchar2(10);
/*新增模件名称字段*/
ALTER TABLE PPM_FORM ADD module_name varchar2(10);
/*新增工序名称字段*/
ALTER TABLE PPM_FORM ADD procedure_name varchar2(10);
/*新增工序检验特性名称字段*/
ALTER TABLE PPM_FORM ADD charac_name varchar2(30);
/*新增检验特性数量字段*/
ALTER TABLE PPM_FORM ADD charac_quantity NUMBER(7);
/*新增严酷度加权系数字段*/
ALTER TABLE PPM_FORM ADD kj NUMBER(7);
/*修改表单定义表ppm_form的表结构 2019年6月16日11:25:58 by 任凯 end*/

/*修改产品表结构 PPM_PRODUCT 删除两个字段 batch和quantity 2019年6月17日14:37:01 by fxiao start*/
ALTER TABLE PPM_PRODUCT DROP COLUMN batch;
ALTER TABLE PPM_PRODUCT DROP COLUMN quantity;
/*修改产品表结构 PPM_PRODUCT 删除两个字段 batch和quantity 2019年6月17日14:37:01 by fxiao end*/

/*修改from表，增加表单表示：logo字段 by 张少波 2019年6月19日21:24:25 start*/
ALTER table PPM_FORM ADD (logo NUMBER(7));
/*修改from表，增加表单表示：logo字段 by 张少波 2019年6月19日21:24:25 end*/

/*修改from表，去除一个失效的外键-对特性的外键 by 张少波 2019年6月19日22:14:03 start*/
ALTER TABLE PPM_FORM DROP CONSTRAINT PPM_FOREIGN_KEY_6;
/*修改from表，去除一个失效的外键-对特性的外键 by 张少波 2019年6月19日22:14:08 end*/

/*修改from表，扩充MODULE_NAME、PROCEDURE_NAME字段长度到100*/
ALTER TABLE PPM_FORM MODIFY MODULE_NAME VARCHAR2(100);
ALTER TABLE PPM_FORM MODIFY PROCEDURE_NAME VARCHAR2(100);


/*修改 PPM_DATA_INSTANCE 表的表结构 2019年6月22日19:31:45 by BaiBoLong start*/
  DROP TABLE PPM_DATA_INSTANCE;
   CREATE TABLE "PPM_DATA_INSTANCE"
   (	"UPDATETIME" TIMESTAMP (6) DEFAULT current_timestamp,
	"CREATETIME" TIMESTAMP (6) DEFAULT current_timestamp,
	"ID" NUMBER(7,0) NOT NULL ENABLE,
	"CREATOR" VARCHAR2(10),
	"PRODUCT_ID" NUMBER(7,0),
	"CHARA_ID" NUMBER(7,0),
	"DEFECT_NUMBER" NUMBER(7,0),
	"TW_ID" NUMBER(7,0),
	"PPM_ORDER" NUMBER(5,0),
	"BATCH" VARCHAR2(10),
	"QUANTITY" NUMBER(7,0),
	"CHECK_TYPE" VARCHAR2(10),
	"CHECK_PERSON" VARCHAR2(10),
	"CHECK_PERSON_ID" VARCHAR2(10),
	"CHECK_TIME" TIMESTAMP (6),
	"PRODUCT_COUNT" NUMBER,
	"CATEGORY" VARCHAR2(10),
	"MODULE_NAME" VARCHAR2(10),
	"PROCEDURE_NAME" VARCHAR2(32),
	"CHARAC_NAME" VARCHAR2(32),
	"CHARAC_QUANTITY" NUMBER,
	"CHARAC_PPM" NUMBER,
	"KJ" NUMBER,
	 PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE,
	 CONSTRAINT "PPM_FOREIGN_KEY_81" FOREIGN KEY ("PRODUCT_ID")
	  REFERENCES "PPM_PRODUCT" ("ID") ENABLE,
	 CONSTRAINT "PPM_FOREIGN_KEY_91" FOREIGN KEY ("CHARA_ID")
	  REFERENCES "PPM_CHARACTERISTIC" ("ID") ENABLE
   ) SEGMENT CREATION IMMEDIATE
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;

   COMMENT ON COLUMN "PPM_DATA_INSTANCE"."DEFECT_NUMBER" IS '检验特性的缺陷数';
   COMMENT ON COLUMN "PPM_DATA_INSTANCE"."BATCH" IS '生产批次';
/*修改 PPM_DATA_INSTANCE 表的表结构 2019年6月22日19:31:45 by BaiBoLong end*/

/*表单实例增加表单标识logo字段 start*/
alter table PPM_DATA_INSTANCE add (logo NUMBER(10));
/*表单实例增加表单标识logo字段 end*/

/*修改数据实例表，扩充字段 start*/
ALTER TABLE PPM_DATA_INSTANCE MODIFY CHECK_TYPE VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY CHECK_PERSON VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY CREATOR VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY BATCH VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY CHECK_PERSON_ID VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY CATEGORY VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY MODULE_NAME VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY PROCEDURE_NAME VARCHAR2(100);
ALTER TABLE PPM_DATA_INSTANCE MODIFY CHARAC_NAME VARCHAR2(100);
/*修改数据实例表，扩充字段 end*/
/*修改数据实例表，删除对数据检验特性表（PPM_CHARACTERISTIC）的外键引用 start*/
ALTER TABLE WCADMIN.PPM_DATA_INSTANCE DROP CONSTRAINT PPM_FOREIGN_KEY_9;
/*修改数据实例表，扩充字段 end*/

/*修改表，增加字段：检验特性总数 characteristics_total*/
ALTER TABLE PPM_DATA_INSTANCE add(characteristics_total NUMBER(10));
ALTER TABLE PPM_DATA_INSTANCE add (defect_number_item NUMBER(10)) ;
ALTER TABLE PPM_DATA_INSTANCE add(procedure_ppm NUMBER(10));

/*修改表*/
ALTER TABLE PPM_TEMPLATE_WORK_LINK MODIFY (creator varchar2(100));
ALTER TABLE PPM_DATA_INSTANCE add(datains_mark NUMBER(10));

/*修改工序表的表结构，增加逻辑删除标记*/
ALTER TABLE PPM_WORKING_PROCEDURE add(DEL_FLAG NUMBER(1) DEFAULT 0);

/*修改form表结构*/
ALTER TABLE PPM_FORM MODIFY CREATOR VARCHAR2(100);
ALTER TABLE PPM_FORM MODIFY BATCH VARCHAR2(100);

/*整体修改字符类型的字符长度*/
ALTER TABLE WCADMIN.PPM_TEMPLATE MODIFY NAME VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_TEMPLATE MODIFY CREATOR VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_WORKING_PROCEDURE MODIFY CREATOR VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_WORKING_PROCEDURE MODIFY NAME VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_CHARACTERISTIC MODIFY CREATOR VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_CHARACTERISTIC MODIFY NAME VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_MODEL MODIFY CREATOR VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_MODEL MODIFY NAME VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_MODEL MODIFY MODEL_CODE VARCHAR2(100);

ALTER TABLE WCADMIN.PPM_PRODUCT MODIFY CREATOR VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_PRODUCT MODIFY NAME VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_PRODUCT MODIFY PRODUCT_CODE VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_PRODUCT MODIFY MODEL_TYPE VARCHAR2(100);

ALTER TABLE WCADMIN.PPM_FORM MODIFY CATEGORY VARCHAR2(100);
ALTER TABLE WCADMIN.PPM_FORM MODIFY CHARAC_NAME VARCHAR2(100);

/*更改全部表的时间默认值 start*/
/*模板表*/
ALTER TABLE PPM_TEMPLATE add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		PPM_TEMPLATE SET CREATETIME_1=CREATETIME;
ALTER TABLE PPM_TEMPLATE DROP COLUMN createtime;
ALTER TABLE PPM_TEMPLATE RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE PPM_TEMPLATE add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		PPM_TEMPLATE SET UPDATETIME_1=UPDATETIME;
ALTER TABLE PPM_TEMPLATE DROP COLUMN UPDATETIME;
ALTER TABLE PPM_TEMPLATE RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*工序表*/
ALTER TABLE PPM_WORKING_PROCEDURE add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		PPM_WORKING_PROCEDURE SET CREATETIME_1=CREATETIME;
ALTER TABLE PPM_WORKING_PROCEDURE DROP COLUMN createtime;
ALTER TABLE PPM_WORKING_PROCEDURE RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE PPM_WORKING_PROCEDURE add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		PPM_WORKING_PROCEDURE SET UPDATETIME_1=UPDATETIME;
ALTER TABLE PPM_WORKING_PROCEDURE DROP COLUMN UPDATETIME;
ALTER TABLE PPM_WORKING_PROCEDURE RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*模板工序关系表*/
ALTER TABLE ppm_template_work_link add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_template_work_link SET CREATETIME_1=CREATETIME;
ALTER TABLE ppm_template_work_link DROP COLUMN createtime;
ALTER TABLE ppm_template_work_link RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE ppm_template_work_link add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_template_work_link SET UPDATETIME_1=UPDATETIME;
ALTER TABLE ppm_template_work_link DROP COLUMN UPDATETIME;
ALTER TABLE ppm_template_work_link RENAME COLUMN UPDATETIME_1 TO UPDATETIME;

/*特性表*/
ALTER TABLE ppm_characteristic add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_characteristic SET CREATETIME_1=CREATETIME;
ALTER TABLE ppm_characteristic DROP COLUMN createtime;
ALTER TABLE ppm_characteristic RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE ppm_characteristic add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_characteristic SET UPDATETIME_1=UPDATETIME;
ALTER TABLE ppm_characteristic DROP COLUMN UPDATETIME;
ALTER TABLE ppm_characteristic RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*模型表*/
ALTER TABLE ppm_model add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_model SET CREATETIME_1=CREATETIME;
ALTER TABLE ppm_model DROP COLUMN createtime;
ALTER TABLE ppm_model RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE ppm_model add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_model SET UPDATETIME_1=UPDATETIME;
ALTER TABLE ppm_model DROP COLUMN UPDATETIME;
ALTER TABLE ppm_model RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*产品表*/
ALTER TABLE ppm_product add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_product SET CREATETIME_1=CREATETIME;
ALTER TABLE ppm_product DROP COLUMN createtime;
ALTER TABLE ppm_product RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE ppm_product add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_product SET UPDATETIME_1=UPDATETIME;
ALTER TABLE ppm_product DROP COLUMN UPDATETIME;
ALTER TABLE ppm_product RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*表单表*/
ALTER TABLE ppm_form add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_form SET CREATETIME_1=CREATETIME;
ALTER TABLE ppm_form DROP COLUMN createtime;
ALTER TABLE ppm_form RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE ppm_form add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_form SET UPDATETIME_1=UPDATETIME;
ALTER TABLE ppm_form DROP COLUMN UPDATETIME;
ALTER TABLE ppm_form RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*表单实例表*/
ALTER TABLE ppm_data_instance add("CREATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_data_instance SET CREATETIME_1=CREATETIME;
ALTER TABLE ppm_data_instance DROP COLUMN createtime;
ALTER TABLE ppm_data_instance RENAME COLUMN CREATETIME_1 TO CREATETIME;

ALTER TABLE ppm_data_instance add("UPDATETIME_1" TIMESTAMP (6) DEFAULT sysdate);
UPDATE 		ppm_data_instance SET UPDATETIME_1=UPDATETIME;
ALTER TABLE ppm_data_instance DROP COLUMN UPDATETIME;
ALTER TABLE ppm_data_instance RENAME COLUMN UPDATETIME_1 TO UPDATETIME;
/*更改全部表的时间默认值 end*/