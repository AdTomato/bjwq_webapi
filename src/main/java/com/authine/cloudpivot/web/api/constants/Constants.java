package com.authine.cloudpivot.web.api.constants;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author liulei
 * @ClassName com.authine.cloudpivot.web.api.constants.Constants
 * @Date 2019/12/18 15:34
 **/
public class Constants {
    /**
     * 社保卡办理模型code: social_security_card
     */
    public static final String SOCIAL_SECURITY_CARD = "social_security_card";

    /**
     * 五险一金享受模型code: insurance_and_housing_fund
     */
    public static final String INSURANCE_AND_HOUSING_FUND = "insurance_and_housing_fund";

    /**
     * 社保卡办理流程code: social_security_card_process
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS = "social_security_card_process";

    /**
     * 办理社保卡流程>>运行办卡节点CODE:process_social_security_card
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS_NODE_PROCESS = "process_social_security_card";

    /**
     * 办理社保卡流程>>业务发卡节点CODE:give_out
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS_NODE_GIVE_OUT = "give_out";

    /**
     * 办理社保卡流程>>业务递交办卡材料或办理失败时跟进节点CODE:upload_info
     */
    public static final String SOCIAL_SECURITY_CARD_PROCESS_NODE_UPLOAD_INFO = "upload_info";

    /**
     * 业务员角色id
     */
    public static final String SALESMAN_ROLE_ID = "";

    /**
     * 上海增员表单Schema
     */
    public static final String SH_ADD_EMPLOYEE_SCHEMA = "sh_add_employee";

    /**
     * 上海增员表单流程
     */
    public static final String SH_ADD_EMPLOYEE_SCHEMA_WF = "sh_add_employee_wf";

    /**
     * 上海减员表单Schema
     */
    public static final String SH_DELETE_EMPLOYEE_SCHEMA = "sh_delete_employee";

    /**
     * 上海减员表单流程
     */
    public static final String SH_DELETE_EMPLOYEE_SCHEMA_WF = "sh_delete_employee_wf";

    /**
     * 全国派单表单Schema
     */
    public static final String NATIONWIDE_DISPATCH_SCHEMA = "nationwide_dispatch";

    /**
     * 全国派单增员流程
     */
    public static final String NATIONWIDE_DISPATCH_WF = "nationwide_dispatch_wf";


    /**
     * 全国派单表单流程
     */
    public static final String NATIONWIDE_DISPATCH_SCHEMA_WF = "nationwide_dispatch_wf";

    /**
     * 全国派单减员名称
     */
    public static final String NATIONWIDE_DISPATCH_DELETE_SCHEMA = "nationwide_dispatch_delete";

    /**
     * 全国派单减员流程
     */
    public static final String DEL_NATIONWIDE_DISPATCH_WF = "del_nationwide_dispatch_wf";

    /**
     * 上海增员的表格名称
     */
    public static final String SH_ADD_EMPLOYEE_TABLE_NAME = "i4fvb_sh_add_employee";

    /**
     * 上海减员数据库名称
     */
    public static final String SH_DELETE_EMPLOYEE_TABLE_NAME = "i4fvb_sh_delete_employee";

    /**
     * 全国派单增员数据库名称
     */
    public static final String NATIONWIDE_DISPATCH_TABLE_NAME = "i4fvb_nationwide_dispatch";


    /**
     * 全国派单减员数据库名称
     */
    public static final String NATIONWIDE_DISPATCH_DELETE_TABLE_NAME = "i4fvb_nationwide_dispatch_delete";

    public static final String NATIONWIDE_DISPATCH = "nationwide_dispatch";

    /**
     * 批量预派表单名称
     */
    public static final String BATCH_PRE_DISPATCH_SCHEMA = "batch_pre_dispatch";

    /**
     * 批量撤离表单名称
     */
    public static final String BATCH_EVACUATION_SCHEMA = "batch_evacuation";

    /**
     * 草稿状态
     */
    public static final String DRAFT_STATUS = "DRAFT";

    /**
     * 生效状态
     */
    public static final String COMPLETED_STATUS = "COMPLETED";

    /**
     * 运行中状态
     */
    public static final String PROCESSING_STATUS = "PROCESSING";

    /**
     * 用户type
     */
    public static final Integer USER_TYPE = 3;

    /**
     * 部门type
     */
    public static final Integer DEPARTMENT_TYPE = 1;

    public static final Integer MAX_INSERT_NUM = 1000;

    /**
     * 增员表单Schema
     */
    public static final String ADD_EMPLOYEE_TABLE_NAME = "i4fvb_add_employee";

    /**
     * 增员表单Schema
     */
    public static final String ADD_EMPLOYEE_SCHEMA = "add_employee";

    /**
     * 增员表单流程
     */
    public static final String ADD_EMPLOYEE_SCHEMA_WF = "add_employee_wf";

    /**
     * 减员表单Schema
     */
    public static final String DELETE_EMPLOYEE_TABLE_NAME = "i4fvb_delete_employee";

    /**
     * 减员表单Schema
     */
    public static final String DELETE_EMPLOYEE_SCHEMA = "delete_employee";

    /**
     * 减员表单流程
     */
    public static final String DELETE_EMPLOYEE_SCHEMA_WF = "delete_employee_wf";

    /**
     * 社保停缴表单Schema
     */
    public static final String SOCIAL_SECURITY_CLOSE_SCHEMA = "social_security_close";

    /**
     * 社保停缴表单流程
     */
    public static final String SOCIAL_SECURITY_CLOSE_SCHEMA_WF = "social_security_close_wf";

    /**
     * 公积金停缴表单Schema
     */
    public static final String PROVIDENT_FUND_CLOSE_SCHEMA = "provident_fund_close";

    /**
     * 公积金停缴表单流程
     */
    public static final String PROVIDENT_FUND_CLOSE_SCHEMA_WF = "provident_fund_close_wf";

    /**
     * 入职通知表单Schema
     */
    public static final String ENTRY_NOTICE_SCHEMA = "entry_notice";

    /**
     * 入职通知流程
     */
    public static final String ENTRY_NOTICE_SCHEMA_WF = "entry_notice_wf";

    /**
     * 员工订单表单Schema
     */
    public static final String EMPLOYEE_ORDER_FORM_SCHEMA = "employee_order_form";

    /**
     * 社保申报表单Schema
     */
    public static final String SOCIAL_SECURITY_DECLARE_SCHEMA = "social_security_declare";

    /**
     * 社保申报表单流程
     */
    public static final String SOCIAL_SECURITY_DECLARE_SCHEMA_WF = "social_security_declare_wf";

    /**
     * 公积金申报表单Schema
     */
    public static final String PROVIDENT_FUND_DECLARE_SCHEMA = "provident_fund_declare";

    /**
     * 公积金申报表单流程
     */
    public static final String PROVIDENT_FUND_DECLARE_SCHEMA_WF = "provident_fund_declare_wf";

    /**
     * 员工订单表单》“社保公积金”子表绑定数据
     */
    public static final String SOCIAL_SECURITY_FUND_DETAIL = "social_security_fund_detail";

    /**
     * 社保申报表单》“社保详细”子表绑定数据
     */
    public static final String SOCIAL_SECURITY_DETAIL = "social_security_detail";

    /**
     * 公积金申报表单》“公积金详细”子表绑定数据
     */
    public static final String PROVIDENT_FUND_DETAIL = "provident_fund_detail";

    /**
     * 员工档案表单Schema
     */
    public static final String EMPLOYEE_FILES_SCHEMA = "employee_files";

    /**
     * 操作类型：提交submit
     */
    public static final String OPERATE_TYPE_SUBMIT = "submit";

    /**
     * 操作类型：驳回reject
     */
    public static final String OPERATE_TYPE_REJECT = "reject";

    /**
     * 社保：social_security
     */
    public static final String SOCIAL_SECURITY = "social_security";

    /**
     * 公积金：provident_fund
     */
    public static final String PROVIDENT_FUND = "provident_fund";

    public static final String[] PARSE_PATTERNS = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "HH:mm:ss"};

    public static final String ADMIN_ID = "2c9280a26706a73a016706a93ccf002b";

    /**
     * 验证码历史记录表单code
     */
    public static final String SMS_HISTORY_SCHEMA = "sms_history";

    public static final String PAY_METHOD = "代收代付";

    /**
     * 发起基数采集SCHEMA_CODE
     */
    public static final String SUBMIT_COLLECT_SCHEMA = "submit_collect";

    public static final String[] GENDER = {"男", "女", "未知性别"};

    public static final String[] ACCOUNT_CHARACTER = {"本地非农业户口（本地城镇）","外地非农业户口（外地城镇）","本地农业户口（本地农村）","外地农业户口（外地农村）","失地农工"," 农村(农民工) ","香港特别行政区居民"," 澳门特别行政区居民"," 台湾地区居民","华侨", "未取得永久居留权的外国人","取得永久居留权的外国人", "本地居民户口", "外地居民户口"};

    public static final String[] DOMICILE_PLACE = {"安徽省","合肥市","瑶海区","庐阳区","蜀山区","包河区","长丰县","肥东县","庐江县","经开区","新站区","巢湖市","合肥市市本级"};

    public static final String[] PERSON_CATEGORY = {"新就业","再就业","大中专毕业生","失业人员","复转退役军人","留学归国人员","农民工"};

    public static final String[] EDUCATION_DEGREE = {"博士研究生","硕士研究生","大学本科","大学专科","中等专科","职业高中","技工学校","普通中学","初级中学","小学","其他"};

    public static final String[] WORK_TYPE = {"单位负责人","企业管理人员","部门经理","其他企业管理人员","专业、技术人员","科研人员","工程技术人员","地质勘探工程技术人员","测绘工程技术人员","矿山工程技术人员","石油工程技术人员","冶金工程技术人员","化工工程技术人员","机械工程技术人员","兵器航空航天工程技术人员","电子工程技术人员","通信工程技术人员","计算机工程技术人员","电气工程技术人员","电力工程技术人员","邮政工程技术人员","广播影视工程技术人员","交通工程技术人员","民用航空工程技术人员","铁路工程技术人员","建筑工程技术人员","建材工程技术人员","林业、家具设计工程技术人员","水利工程技术人员","海洋工程技术人员","水产工程技术人员","纺织工程技术人员","食品工程技术人员","食品检验员","食品业务员","气象工程技术人员","地震工程技术人员","环境保护工程技术人员","安全工程技术人员","标准化、计量、质量工程技术人员","工业管理工程技术人员","生物工程技术人员","其他工程技术人员","农业技术人员","土壤肥料技术人员","植物保护技术人员","园艺技术人员","作物遗传育种栽培技术人员","兽医兽药技术人员","畜牧与草业技术人员","其他农业技术人员","飞机船舶技术人员","船舶指挥引航人员","其他飞机船舶技术人员","卫生专业技术人员","西医","中医","公共卫生医师","药剂师","医疗技术人员","护士","其他卫生专业技术人员","经济业务人员","经济计划人员","统计师","财会人员","审计师","国际商务人员","房地产业务人员","其他经济业务人员","金融业务人员","银行业务人员","保险业务人员","证券业务人员","其他金融业务人员","法律专业人员","律师","其他法律专业人员","教学人员","高等教育教师","中等职业教育教师","中学教师","小学教师","幼儿教师","特殊教育教师","其他教学人员","文学艺术工作者","文艺创作和评论人员","编导和音乐指挥人员","演员","乐器演奏员","影视制作及舞台专业人员","美术专业人员","工美装饰服装广告设计人员","装潢美术设计人员","服装设计师","室内装饰设计人员","广告设计人员","其他文学艺术工作者","体育工作者","新闻出版文化工作者","记者","编辑","播音员及节目主持人","翻译","图书资料档案业务人员","其他新闻出版、文化工作者","办事人员","行政办公人员","行政业务人员","秘书、打字员","其他行政办公人员","安全保卫和消防人员","治安保卫人员","其他安全保卫消防人员","邮政电信业务人员","邮政业务人员","电信业务人员、话务员","电信通信传输业务人员","其他邮政电信业务人员","商业、服务业人员","购销人员","营业人员、收银员","推销展销人员","采购人员","拍卖典当租赁业务人员","废旧物资回收利用人员","商品监督和市场管理员","医药商品购销员","其他购销人员","仓储人员","保管人员","储运人员","其他仓储人员","餐饮服务人员","中餐烹饪人员","西餐烹饪人员","调酒和茶艺人员","餐厅服务员、厨工","其他餐饮服务人员","饭店、旅游娱乐服务员","饭店服务人员","旅游游览场所服务员","健身娱乐场所服务员","其他旅游健身娱乐服务人员","运输服务人员","公路运输服务人员","铁路运输服务人员","航空运输服务人员","水上运输服务人员","其他运输服务人员","医疗卫生辅助服务人员","护理人员","其他医疗卫生辅助服务人员","社会服务人员","社会中介服务人员","物业管理人员","供水供热人员、锅炉工","美发美容人员","摄影服务人员","验光配镜人员","洗染、织补人员","浴池服务人员","家用机电产品维修人员","办公设备维修人员","保育、家庭服务员","清洁工","家庭教师","电梯工","其他社会服务人员","农林牧渔水利生产人员","种植业生产人员","大田作物生产人员","农业实验人员","园艺作物生产人员","中药材生产人员","农副林特产品加工工","其他种植业生产人员","林业及动植物保护人员","木材采运人员","其他林业及野生动植物保护人员","畜牧业生产人员","渔业生产人员","水产养殖人员","水产捕捞及有关人员","水产品加工工","其他渔业生产人员","水利设施管理养护人员","农林机械操作和能源开发人员","生产运输工人","勘测及矿物开采工","地质勘查人员","测绘人员","矿物开采工","矿物处理工","钻井人员","石油、天然气开采人员","盐业生产人员","其他勘测及矿物开采工","金属冶炼轧制工","金属冶炼工","金属轧制工","其他金属冶炼、轧制工","化工产品生产工","机械制造加工工","机械冷加工工","车工","铣工","刨插工","磨工","镗工","钻床工","加工中心操作工","制齿工","机械热加工工","铸造工","锻造工","冲压工","焊工","金属热处理工","特种加工设备操作工","冷作钣金加工工","工件表面处理加工工","磨料磨具制造加工人员","其他机械制造加工工","机电产品装配工","机械设备装配工","电气电子设备装配工","仪器仪表装配工","运输车辆装配工","其他机电产品装配工","机械设备修理工","机械设备维修工","仪器仪表修理工","其他机械设备修理工","电力设备装运检修工","电力设备安装工","专业电力设备检修工","普通电力设备检修工、电工","其他电力设备装运、检修及供电工","电子元器件制造装调工","电子元件、器件制造工","电子设备装配调试工","电子产品维修工","其他电子元器件与电子设备制造、装调维修工","橡胶塑料制品生产工","橡胶制品生产工","塑料制品加工工","其他橡胶和塑料制品生产工","纺织针织印染工","纤维预处理人员","纺纱人员","织造人员","针织人员","印染人员","纺织、针织、印染工","裁剪缝纫毛皮革制作工","裁剪缝纫工","鞋帽制作工","皮革、毛皮加工工","缝纫制品再加工人员","其他裁剪缝纫和毛皮革制作工","粮油食品饮料生产工","粮油食品饮料生产加工及饲料加工工","烟草制品加工工","药品生产制造工","木材人造板生产制作工","木材加工制作工","其他木材加工、人造板生产及木制品制作工","制浆造纸纸制品生产工","纸制品制作工","其他制浆、造纸和纸制品生产加工工","建筑材料生产加工工","玻璃陶瓷搪瓷生产工","玻璃陶瓷搪瓷生产加工工","广播影视品制作播放人员","广播影视设备操作人员","其他广播影视品制作播放及文物保护人员","制版印刷人员","工艺、美术品制作人员","文体用品乐器制作人员","建筑和工程施工人员","土石方施工人员","砌筑工","混凝土工","钢筋工","架子工","工程防水工","装饰、装修、油漆工","筑路、养护、维修人员","机械电气工程设备安装工、管工","电工","木工","其他工程施工人员","驾驶员和运输工人","机动车驾驶员","铁路、地铁运输设备操作及有关人员","船舶水手","起重装卸机械操作工","其他运输设备操作人员","环境监测废物处理人员","检验、计量人员","体力工人"};

    public static final  String[] CONTRACT_TERM_TYPE  = {"有固定期限","无固定期限","以完成一定工作为期限"};

    public static final  String[] WORK_FORM  = {"全日制用工","非全日制用工"};

    public static final String[] NATION = {"汉族","蒙古族","回族","藏族","维吾尔族","苗族","彝族","壮族","布依族","朝鲜族","满族","侗族","瑶族","白族","土家族","哈尼族","哈萨克族","傣族","黎族","傈傈族","佤族","畲族","高山族","拉祜族","水族","东乡族","纳西族","景颇族","柯尔克孜族","土族","达翰尔族","仫佬族","羌族","布朗族","撒拉族","毛南族","仡佬族","锡伯族","阿昌族","普米族","塔吉克族","怒族","乌孜别克族","俄罗斯族","鄂温克族","德昂族","保安族","裕固族","京族","塔塔尔族","独龙族","鄂伦春族","赫哲族","门巴族","珞巴族","基诺族","其他"};
    //---------------------------------------------------------------------------
    // 公积金停缴下拉框
    // 合同变更状态
    public static final String[] CONTRACT_STATUS = {"解除","终止"};

    // 合同终止原因
    public static final String[] CONTRACT_STOP_REASON = {"劳动合同期满","开始依法享受基本养老保险待遇","劳动者死亡，或者被人民法院宣告死亡或者宣告失踪","用人单位被依法宣告破产","用人单位被吊销营业执照、责令关闭、撤销或者用人单位决定提前解散的","法律、行政法规规定的其他情形"};

    //合同解除原因
    public static final String[] CONTRACT_TERMINATE_REASON = {"劳动者提前30天书面通知解除或试用期提案前3天通知解除","未按照劳动合同约定提供劳动保护或者劳动条件的","未及时足额支付劳动报酬的","未依法为劳动者缴纳社会保险费的","用人单位的规章制度违反法律、法规的规定，损害劳动者权益的","因《劳动合同法》第二十六条第一款规定的情形致使劳动合同无效的","法律、行政法规规定劳动者可以解除劳动合同的其他情形","用人单位以暴力、威胁或者非法限制人身自由的手段强迫劳动，或者作业危及人身安全的","在试用期间被证明不符合录用条件的","严重违反用人单位的规章制度的","严重失职，营私舞弊，给用人单位造成重大损害的","劳动者同时与其他用人单位建立劳动关系，对完成本单位的工作任务造成严重影响的","因《劳动合同法》第二十六条第一款第一项规定的情形致使劳动合同无效的","被依法追究刑事责任的","劳动者患病或者非因公负伤，在规定的医疗期满后不能从事原工作，也不能从事另行安排的","劳动者不能胜任工作，经过培训或者调整工作岗位，仍不能胜任工作的","劳动合同订立时所依据的客观情况发生重大变化，致使劳动合同无法履行","依照企业破产法规定进行重整的（裁员）","生产经营发生严重困难的（裁员）","企业转产、重大技术革新或者经营方式调整，经变更劳动合同后，仍需裁减人员的。（裁员）","其他因劳动合同订立时所依据的客观经济情况发生重大变化，致使劳动合同无法履行的（裁员）"};

    public static final String ID_CARD_CHINESE = "身份证";
}
