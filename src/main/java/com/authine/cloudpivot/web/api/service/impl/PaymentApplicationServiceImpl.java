package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.BizObjectFacade;
import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.dto.EmployeeFilesDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.PaymentApplicationMapper;
import com.authine.cloudpivot.web.api.mapper.SystemManageMapper;
import com.authine.cloudpivot.web.api.service.PaymentApplicationService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description 支付申请ServiceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.PaymentApplicationServiceImpl
 * @Date 2020/3/20 14:57
 **/
@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {

    @Resource
    private PaymentApplicationMapper paymentApplicationMapper;

    @Resource
    private SystemManageMapper systemManageMapper;

    @Override
    public void mergeData(List <String> ids, String supplierId, UserModel user, DepartmentModel dept,
                          WorkflowInstanceFacade workflowInstanceFacade, BizObjectFacade bizObjectFacade) throws Exception {
        // 判断当前数据是否均是草稿状态
        if (!isAllDraft(ids)) {
            throw new RuntimeException("所选数据中存在不是“草稿”状态的数据，请刷新列表之后在执行操作！");
        }
        // 获取供应商信息
        Supplier supplier = systemManageMapper.getSupplierById(supplierId);
        if (supplier == null) {
            throw new RuntimeException("没有查询到供应商信息！");
        }

        // 合并多条数据
        mergeMultipleData(ids, supplier, user, dept, workflowInstanceFacade, bizObjectFacade, "多对一");

    }

    /**
     * 方法说明：合并多条数据
     * @param ids  合并数据id
     * @param supplier 供应商
     * @param user
     * @param dept
     * @param workflowInstanceFacade
     * @param bizObjectFacade
     * @return void
     * @author liulei
     * @Date 2020/3/25 9:52
     */
    private void mergeMultipleData(List <String> ids, Supplier supplier, UserModel user,
                                   DepartmentModel dept, WorkflowInstanceFacade workflowInstanceFacade,
                                   BizObjectFacade bizObjectFacade, String paymentType) throws Exception{
        // 查询支付申请信息
        List<PaymentApplication> paymentApplications = paymentApplicationMapper.getPaymentApplicationByIds(ids);
        if (paymentApplications != null && paymentApplications.size() == ids.size()) {
            // 创建合并后的支付申请
            PaymentApplication paymentApplication = new PaymentApplication(paymentApplications, supplier, user, dept, paymentType);
            this.insertPaymentApplication(paymentApplication, user.getId(), dept.getId(), workflowInstanceFacade);

            // 查询原有的支付客户明细
            List <PaymentClientDetails> details = paymentApplicationMapper.getPaymentClientDetailsByParentIds(ids);
            if (details != null && details.size() > 0) {
                // 生成支付客户明细
                paymentApplicationMapper.insertPaymentClientDetails(details, paymentApplication.getId());
            }

            //支付明细账单关联修改为新的id
            paymentApplicationMapper.updatePaymentDetailsPaymentApplicationId(paymentApplication.getId(), ids);
            // 删除原支付客户明细数据
            //paymentApplicationMapper.deletePaymentClientDetailsByParentIds(ids);
            // 删除原支付申请数据
            bizObjectFacade.removeBizObjects(user.getId(), "payment_application", ids);


        } else {
            throw new RuntimeException("查询待合并的支付申请数据出错！");
        }

    }

    @Override
    public void createPaymentApplicationData(UserModel user, DepartmentModel dept, BizObjectFacade bizObjectFacade,
                                             WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        // 当前账单年月
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String billYear = sdf.format(new Date());
        // 生成社保支付数据
        createPaymentApplicationSocialSecurityFundData(billYear, user, dept, bizObjectFacade, workflowInstanceFacade,
                "社保");
        // 生成公积金支付数据
        createPaymentApplicationSocialSecurityFundData(billYear, user, dept, bizObjectFacade, workflowInstanceFacade,
                "公积金");
    }

    @Override
    public void importOneTimeFee(String fileName, UserModel user, DepartmentModel dept) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            // 生成临时表数据
            insertTempData(fileList, user, dept, sourceId, "");

            // 更新社保一次性收费数据
            paymentApplicationMapper.updatePaymentDetailsOneTimeFee(sourceId, "社保");
            // 更新客户明细表数据
            paymentApplicationMapper.updatePaymentClientDetailsOneTimeFee(sourceId, "社保");
            // 更新支付申请数据
            paymentApplicationMapper.updatePaymentApplicationOneTimeFee(sourceId, "社保");

            // 新增社保一次性收费数据
            paymentApplicationMapper.insertPaymentDetailsOneTimeFee(sourceId, "社保");

            // 更新公积金一次性收费数据
            paymentApplicationMapper.updatePaymentDetailsOneTimeFee(sourceId, "公积金");
            // 更新客户明细表数据
            paymentApplicationMapper.updatePaymentClientDetailsOneTimeFee(sourceId, "公积金");
            // 更新支付申请数据
            paymentApplicationMapper.updatePaymentApplicationOneTimeFee(sourceId, "公积金");

            // 新增公积金一次性收费数据
            paymentApplicationMapper.insertPaymentDetailsOneTimeFee(sourceId, "公积金");

            // 删除支付明细临时表数据
            paymentApplicationMapper.deletePaymentDetailsTempBySourceId(sourceId);
            // 更新支付明细表sourceId=null
            paymentApplicationMapper.updatePaymentDetailsSourceIdToNull(sourceId);
            // 更新支付客户表sourceId=null
            paymentApplicationMapper.updatePaymentClientDetailsSourceIdToNull(sourceId);
            // 更新支付申请表sourceId=null
            paymentApplicationMapper.updatePaymentApplicationSourceIdToNull(sourceId);
        }

    }

    @Override
    public void importPaymentDetails(String fileName, UserModel user, DepartmentModel dept, String systemType,
                                     WorkflowInstanceFacade workflowInstanceFacade) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
            // 生成支付明细表表数据
            insertTempData(fileList, user, dept, sourceId, systemType);

            paymentApplicationMapper.insertPaymentApplicationBySourceId(sourceId, user.getId(), user.getName(),
                    dept.getId(), dept.getQueryCode(), Constants.DRAFT_STATUS);
            // 新增客户明细表数据
            // 生成支付客户明细表数据(此时parentId设为null)
            paymentApplicationMapper.insertPaymentClientDetailsBySourceId(sourceId);
            // 给支付客户明细数据客户代码等赋值
            paymentApplicationMapper.giveClientDetailsAssignmentClientCode(sourceId);

            // 给客户明细表parentId赋值
            paymentApplicationMapper.updatePaymentClientDetailsParentIdBySourceId(sourceId);

            // 给支付明细表关联支付申请表单字段赋值
            paymentApplicationMapper.updatePaymentDetailsByClientDetails(sourceId);

            // 更新支付明细表sourceId=null
            paymentApplicationMapper.updatePaymentDetailsSourceIdToNull(sourceId);
            // 更新支付客户表sourceId=null
            paymentApplicationMapper.updatePaymentClientDetailsSourceIdToNull(sourceId);
            // 更新支付申请表sourceId=null
            paymentApplicationMapper.updatePaymentApplicationSourceIdToNull(sourceId);
        }
    }

    /**
     * 方法说明：导入支付明细表生成临时表数据
     * @param fileList 导入文件数据
     * @param user 当前用户
     * @param dept 当前部门
     * @param sourceId 来源id
     * @return void
     * @author liulei
     * @Date 2020/3/30 14:34
     */
    private void insertTempData(List<String[]> fileList, UserModel user, DepartmentModel dept, String sourceId, String dataType) throws Exception{
        String tableName = "mutual_system".equals(dataType) || "national_system".equals(dataType) ?
                "i4fvb_payment_details" : "i4fvb_payment_details_temp";
        List<String> fields = Arrays.asList(fileList.get(0));
        List<Map<String, Object>> dataList = new ArrayList <>();
        Map<String, Object> data = new HashMap <>();
        for (int i = 2; i < fileList.size(); i++) {
            List <String> list = Arrays.asList(fileList.get(i));
            data = new HashMap <>();
            for(int j = 0; j < fields.size(); j++) {
                data.put(fields.get(j), list.get(j));
            }
            data.put("source_id", sourceId);
            data.put("data_type", dataType);
            data = setSystemField(data, UUID.randomUUID().toString().replaceAll("-", ""), user, dept,
                    Constants.COMPLETED_STATUS);
            data.put("name", data.get("bill_year") == null ? "" : data.get("bill_year").toString() + data.get(
                    "employee_name") == null ? "" : data.get("employee_name").toString());
            dataList.add(data);
        }
        // 生成临时表数据
        for (int j = 0; j < dataList.size(); j += 500) {
            int size = dataList.size();
            int toPasIndex = 500;
            if (j + 500 > size) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                toPasIndex = size - j;
            }
            List <Map<String, Object>> newList = dataList.subList(j, j + toPasIndex);
            paymentApplicationMapper.batchInsertPaymentDetailsTemp(newList, tableName);
        }
    }

    /**
     * 方法说明：生成社保公积金支付数据
     * @param billYear 账单年月
     * @param user
     * @param dept
     * @param bizObjectFacade
     * @param workflowInstanceFacade
     * @param dataType 类型
     * @return void
     * @author liulei
     * @Date 2020/3/25 10:09
     */
    private void createPaymentApplicationSocialSecurityFundData(String billYear, UserModel user, DepartmentModel dept,
                                                              BizObjectFacade bizObjectFacade,
                                                              WorkflowInstanceFacade workflowInstanceFacade,
                                                              String dataType) throws Exception {
        String sourceId = UUID.randomUUID().toString().replaceAll("-", "");
        // 查询需要生成公积金的数据
        List <EmployeeFilesDto> list = new ArrayList <>();
        if ("公积金".equals(dataType)) {
            list = paymentApplicationMapper.getPaymentApplicationAccumulationFundData(billYear);
        } else {
            list = paymentApplicationMapper.getPaymentApplicationSocialSecurityData(billYear);
        }

        if (list != null && list.size() > 0) {
            int listSize = list.size();
            int toIndex = 500;
            for (int j = 0; j < list.size(); j += 500) {
                if (j + 500 > listSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                    toIndex = listSize - j;
                }
                List <EmployeeFilesDto> newList = list.subList(j, j + toIndex);

                this.processingData(newList, billYear, user, dept, sourceId, dataType);
            }
            // 判断之前是否存在一次性收费数据,存在则更新数据
            paymentApplicationMapper.updatePaymentDetailsByTemp(sourceId);
            // 生成新增的数据
            paymentApplicationMapper.insertPaymentDetailsByTemp(sourceId);
            // 生成支付客户明细表数据(此时parentId设为null)
            paymentApplicationMapper.insertPaymentClientDetailsBySourceId(sourceId);
            // 给支付客户明细数据客户代码等赋值
            paymentApplicationMapper.giveClientDetailsAssignmentClientCode(sourceId);

            // 获取新增省内支付申请数据
            List <PaymentApplication> pas = new ArrayList <>();
            List <PaymentApplication> pa1 = paymentApplicationMapper.getSnPaymentApplicationBySourceId(sourceId);
            // 新增省外支付申请数据
            //List <PaymentApplication> pa2 = paymentApplicationMapper.getSwPaymentApplicationBySourceId(sourceId);

            pas.addAll(processPaymentApplicationList(pa1, billYear, user, dept, dataType));
            //pas.addAll(processPaymentApplicationList(pa2, billYear, user, dept, dataType));
            if (pas != null && pas.size() > 0) {
                for (int j = 0; j < pas.size(); j += 500) {
                    int pasSize = pas.size();
                    int toPasIndex = 500;
                    if (j + 500 > pasSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                        toPasIndex = pasSize - j;
                    }
                    List <PaymentApplication> newList = pas.subList(j, j + toPasIndex);
                    paymentApplicationMapper.batchInsertPaymentApplication(newList);
                }

                // 给客户明细表parentId赋值
                paymentApplicationMapper.updatePaymentClientDetailsParentIdBySourceId(sourceId);

                // 给支付明细表关联支付申请表单字段赋值
                paymentApplicationMapper.updatePaymentDetailsByClientDetails(sourceId);

                // 查询之前是否存在相对应的支付申请数据数据
                List <Map <String, Object>> repeatList =
                        paymentApplicationMapper.getRepeatPaymentApplicationData(sourceId);
                if (repeatList != null && repeatList.size() > 0) {
                    List <List <String>> merges = new ArrayList <>();
                    List <String> notMerges = new ArrayList <>();

                    for (int i = 0; i < repeatList.size(); i++) {
                        String id = repeatList.get(i).get("id") == null ? "" : repeatList.get(i).get("id").toString();
                        String yId = repeatList.get(i).get("yId") == null ? "" :
                                repeatList.get(i).get("yId").toString();
                        if (StringUtils.isBlank(id)) {
                            continue;
                        }
                        if (StringUtils.isBlank(yId)) {
                            notMerges.add(id);
                        }

                        List <String> merge = new ArrayList <>();
                        merge.add(id);
                        merge.add(yId);

                        merges.add(merge);
                    }

                    if (merges != null && merges.size() > 0) {
                        for (int i = 0; i < merges.size(); i++) {
                            // 合并数据
                            mergeMultipleData(merges.get(i), new Supplier(), user, dept, workflowInstanceFacade,
                                    bizObjectFacade, "一对一");
                        }
                    }
                    if (notMerges != null && notMerges.size() > 0) {
                        // 激活流程
                        CommonUtils.startWorkflowInstance(workflowInstanceFacade, dept.getId(), user.getId(),
                                "payment_application_wf", notMerges, false);
                    }
                }
            }
        }

        // 删除支付明细临时表数据
        paymentApplicationMapper.deletePaymentDetailsTempBySourceId(sourceId);
        // 更新支付明细表sourceId=null
        paymentApplicationMapper.updatePaymentDetailsSourceIdToNull(sourceId);
        // 更新支付客户表sourceId=null
        paymentApplicationMapper.updatePaymentClientDetailsSourceIdToNull(sourceId);
        // 更新支付申请表sourceId=null
        paymentApplicationMapper.updatePaymentApplicationSourceIdToNull(sourceId);
    }

    /**
     * 方法说明：处理支付申请数据，赋值系统字段
     * @param list 支付申请List
     * @param billYear 账单年月
     * @param user 当前用户
     * @param dept 当前部门
     * @param dataType 数据类型
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.PaymentApplication>
     * @author liulei
     * @Date 2020/3/25 9:29
     */
    private List <PaymentApplication> processPaymentApplicationList(List <PaymentApplication> list, String billYear,
                                                                    UserModel user, DepartmentModel dept,
                                                                    String dataType) {
        List <PaymentApplication> paList = new ArrayList <>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                PaymentApplication pa = list.get(0);
                pa.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                pa.setName(user.getName());
                pa.setCreater(user.getId());
                pa.setCreatedDeptId(dept.getId());
                pa.setOwner(user.getId());
                pa.setOwnerDeptId(dept.getId());
                pa.setCreatedTime(new Date());
                pa.setModifier(user.getId());
                pa.setModifiedTime(new Date());
                pa.setSequenceStatus(Constants.DRAFT_STATUS);
                pa.setOwnerDeptQueryCode(dept.getQueryCode());
                pa.setBillYear(billYear);
                pa.setDataType(dataType);
                pa.setPaymentType("一对一");

                paList.add(pa);
            }
        }
        return paList;
    }

    /**
     * 方法说明：处理数据，并生成支付明细数据
     * @param list
     * @param billYear 账单年月
     * @param user 当前用户
     * @param dept 当前部门
     * @param sourceId
     * @param processType
     * @return void
     * @author liulei
     * @Date 2020/3/25 9:30
     */
    private void processingData(List <EmployeeFilesDto> list, String billYear, UserModel user, DepartmentModel dept,
                                String sourceId, String processType) throws Exception {
        List <Map <String, Object>> paymentDetails = new ArrayList <>();
        List<String> employeeFilesIds = new ArrayList <>();
        for (int i = 0; i < list.size(); i ++) {
            EmployeeFilesDto filesDto = list.get(i);
            if (filesDto.getEmployeeOrderFormDto() == null) {
                continue;
            }
            List<SocialSecurityFundDetail> details = filesDto.getEmployeeOrderFormDto().getSocialSecurityFundDetails();
            if (details == null || details.size() == 0) {
                continue;
            }
            Map <String, Object> map = getPaymentDetailsByEmployeeFilesDto(filesDto, processType);
            map.put("bill_year", billYear);
            map.put("business_year", billYear);
            map.put("data_type", processType);
            map.put("source_id", sourceId);
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            map = setSystemField(map, id, user, dept, Constants.COMPLETED_STATUS);
            map.put("name", billYear + filesDto.getEmployeeName());

            paymentDetails.add(map);

            employeeFilesIds.add(filesDto.getId());
        }

        if (paymentDetails != null && paymentDetails.size() > 0) {
            // 生成临时表数据
            paymentApplicationMapper.batchInsertPaymentDetailsTemp(paymentDetails, "i4fvb_payment_details_temp");
            // 更新员工档案的支付申请标记
            paymentApplicationMapper.updateEmployeeFilesPaymentApplication(employeeFilesIds, processType, billYear);
        }
    }

    /**
     * 方法说明：系统字段设置
     * @param map
     * @param id
     * @param user
     * @param dept
     * @param sequenceStatus
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author liulei
     * @Date 2020/3/25 9:32
     */
    private Map <String, Object> setSystemField(Map <String, Object> map, String id, UserModel user,
                                                DepartmentModel dept, String sequenceStatus) {
        map.put("id", id);
        map.put("creater", user.getId());
        map.put("createdDeptId", dept.getId());
        map.put("owner", user.getId());
        map.put("ownerDeptId", dept.getId());
        map.put("createdTime", new Date());
        map.put("modifier", user.getId());
        map.put("modifiedTime", new Date());
        map.put("sequenceStatus", sequenceStatus);
        map.put("ownerDeptQueryCode", dept.getQueryCode());

        return map;
    }

    /**
     * 方法说明：获取生成的支付明细数据
     * @param filesDto
     * @param type
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author liulei
     * @Date 2020/3/25 9:31
     */
    private Map<String, Object> getPaymentDetailsByEmployeeFilesDto(EmployeeFilesDto filesDto, String type) throws Exception {
        Map<String, Object> map = new HashMap <>();
        map.put("entrust_unit", filesDto.getEntrustedUnit());
        map.put("client_name", filesDto.getClientName());
        map.put("employee_name", filesDto.getEmployeeName());
        map.put("id_no", filesDto.getIdNo());

        if ("公积金".equals(type)) {
            // 支付方式
            String paymentMethod = null;
            String welfareHandler = filesDto.getProvidentFundArea();
            if (StringUtils.isNotBlank(welfareHandler)) {
                if (welfareHandler.indexOf("大库") >= 0) {
                    paymentMethod = "代收代付";
                } else if (welfareHandler.indexOf("单立户") >= 0) {
                    paymentMethod = "托收";
                }
            }

            // 公积金企业缴纳合计,公积金个人缴纳合计,公积金缴纳合计
            Double providentEnterprise = 0.0,providentPersonal= 0.0,providentTotal = 0.0;
            map.put("delegated_area", filesDto.getProvidentFundCity());
            List<SocialSecurityFundDetail> details = filesDto.getEmployeeOrderFormDto().getSocialSecurityFundDetails();

            for (int i = 0; i < details.size(); i++) {
                SocialSecurityFundDetail detail = details.get(i);
                String productName = detail.getNameHide();
                if (StringUtils.isBlank(productName)) {
                    continue;
                } else if (productName.indexOf("补充") >= 0) {
                    map.put("b_provident_enterprise_base", detail.getBaseNum());
                    map.put("b_provident_enterprise_ratio", detail.getCompanyRatio());
                    map.put("b_provident_enterprise_add", detail.getCompanySurchargeValue());
                    map.put("b_provident_enterprise_pay", detail.getCompanyMoney());

                    map.put("b_provident_personal_base", detail.getBaseNum());
                    map.put("b_provident_personal_ratio", detail.getEmployeeRatio());
                    map.put("b_provident_personal_attach", detail.getEmployeeSurchargeValue());
                    map.put("b_provident_personal_pay", detail.getEmployeeMoney());

                    map.put("b_provident_subtotal", detail.getSum());
                    map.put("b_provident_payment_method", paymentMethod);
                } else {
                    map.put("provident_enterprise_base", detail.getBaseNum());
                    map.put("provident_enterprise_ratio", detail.getCompanyRatio());
                    map.put("provident_enterprise_attach", detail.getCompanySurchargeValue());
                    map.put("provident_enterprise_pay", detail.getCompanyMoney());

                    map.put("provident_personal_base", detail.getBaseNum());
                    map.put("provident_personal_ratio", detail.getEmployeeRatio());
                    map.put("provident_personal_attach", detail.getEmployeeSurchargeValue());
                    map.put("provident_personal_pay", detail.getEmployeeMoney());

                    map.put("provident_subtotal", detail.getSum());
                    map.put("provident_payment_method", paymentMethod);
                }
                if (!"托收".equals(paymentMethod)) {
                    providentEnterprise += (detail.getCompanyMoney() == null ? 0.0 : detail.getCompanyMoney());
                    providentPersonal += (detail.getEmployeeMoney() == null ? 0.0 : detail.getEmployeeMoney());
                    providentTotal += (detail.getSum() == null ? 0.0 : detail.getSum());
                }
            }
            map.put("provident_enterprise", providentEnterprise);
            map.put("provident_personal", providentPersonal);
            map.put("provident_total", providentTotal);
            map.put("social_provident_total", providentTotal);
            map.put("total_charge", providentTotal);
        } else {
            // 支付方式
            String paymentMethod = null;
            String welfareHandler = filesDto.getSocialSecurityArea();
            if (StringUtils.isNotBlank(welfareHandler)) {
                if (welfareHandler.indexOf("大库") >= 0) {
                    paymentMethod = "代收代付";
                } else if (welfareHandler.indexOf("单立户") >= 0) {
                    paymentMethod = "托收";
                }
            }
            // 社保数据
            map.put("service_fee_total", filesDto.getEmployeeOrderFormDto().getServiceFee());// 服务费放到社保里面
            map.put("delegated_area", filesDto.getSocialSecurityCity());
            // 公积金企业缴纳合计,公积金个人缴纳合计,公积金缴纳合计
            Double socialSecurityEnterprise = 0.0,socialSecurityPersonal= 0.0,socialSecurityTotal = 0.0;
            List<SocialSecurityFundDetail> details = filesDto.getEmployeeOrderFormDto().getSocialSecurityFundDetails();
            if (details == null || details.size() == 0) {
                return map;
            }
            for (int i = 0; i < details.size(); i++) {
                SocialSecurityFundDetail detail = details.get(i);
                String productName = detail.getNameHide();
                if (StringUtils.isBlank(productName)) {
                    continue;
                } else if (productName.indexOf("养老") >= 0) {
                    map.put("pension_enterprise_base", detail.getBaseNum());
                    map.put("pension_enterprise_ratio", detail.getCompanyRatio());
                    map.put("pension_enterprise_attach", detail.getCompanySurchargeValue());
                    map.put("pension_enterprise_pay", detail.getCompanyMoney());

                    map.put("pension_personal_base", detail.getBaseNum());
                    map.put("pension_personal_ratio", detail.getEmployeeRatio());
                    map.put("pension_personal_attach", detail.getEmployeeSurchargeValue());
                    map.put("pension_personal_pay", detail.getEmployeeMoney());

                    map.put("pension_subtotal", detail.getSum());
                    map.put("pension_payment_method", paymentMethod);
                } else if (productName.indexOf("医疗") >= 0) {
                    if (productName.indexOf("大病") >= 0) {
                        map.put("d_medical_enterprise_base", detail.getBaseNum());
                        map.put("d_medical_enterprise_ratio", detail.getCompanyRatio());
                        map.put("d_medical_enterprise_attach", detail.getCompanySurchargeValue());
                        map.put("d_medical_enterprise_pay", detail.getCompanyMoney());

                        map.put("d_medical_personal_base", detail.getBaseNum());
                        map.put("d_medical_personal_ratio", detail.getEmployeeRatio());
                        map.put("d_medical_personal_attach", detail.getEmployeeSurchargeValue());
                        map.put("d_medical_personal_pay", detail.getEmployeeMoney());

                        map.put("d_medical_subtotal", detail.getSum());
                        map.put("d_medical_payment_method", paymentMethod);
                    } else {
                        map.put("medical_enterprise_base", detail.getBaseNum());
                        map.put("medical_enterprise_ratio", detail.getCompanyRatio());
                        map.put("medical_enterprise_attach", detail.getCompanySurchargeValue());
                        map.put("medical_enterprise_pay", detail.getCompanyMoney());

                        map.put("medical_personal_base", detail.getBaseNum());
                        map.put("medical_personal_ratio", detail.getEmployeeRatio());
                        map.put("medical_personal_attach", detail.getEmployeeSurchargeValue());
                        map.put("medical_personal_pay", detail.getEmployeeMoney());

                        map.put("medical_subtotal", detail.getSum());
                        map.put("medical_payment_method", paymentMethod);
                    }

                } else if (productName.indexOf("失业") >= 0) {
                    map.put("unemp_enterprise_base", detail.getBaseNum());
                    map.put("unemp_enterprise_ratio", detail.getCompanyRatio());
                    map.put("unemp_enterprise_attach", detail.getCompanySurchargeValue());
                    map.put("unemp_enterprise_pay", detail.getCompanyMoney());

                    map.put("unemp_personal_base", detail.getBaseNum());
                    map.put("unemp_personal_ratio", detail.getEmployeeRatio());
                    map.put("unemp_personal_attach", detail.getEmployeeSurchargeValue());
                    map.put("unemp_personal_pay", detail.getEmployeeMoney());

                    map.put("unemp_subtotal", detail.getSum());
                    map.put("unemp_payment_method", paymentMethod);
                } else if (productName.indexOf("工伤") >= 0) {
                    if (productName.indexOf("补充") >= 0) {
                        map.put("b_injury_enterprise_base", detail.getBaseNum());
                        map.put("b_injury_enterprise_ratio", detail.getCompanyRatio());
                        map.put("b_injury_enterprise_attach", detail.getCompanySurchargeValue());
                        map.put("b_injury_enterprise_pay", detail.getCompanyMoney());

                        map.put("b_injury_personal_base", detail.getBaseNum());
                        map.put("b_injury_personal_ratio", detail.getEmployeeRatio());
                        map.put("b_injury_personal_attach", detail.getEmployeeSurchargeValue());
                        map.put("b_injury_personal_pay", detail.getEmployeeMoney());

                        map.put("b_injury_subtotal", detail.getSum());
                        map.put("b_injury_payment_method", paymentMethod);
                    } else {
                        map.put("injury_enterprise_base", detail.getBaseNum());
                        map.put("injury_enterprise_ratio", detail.getCompanyRatio());
                        map.put("injury_enterprise_attach", detail.getCompanySurchargeValue());
                        map.put("injury_enterprise_pay", detail.getCompanyMoney());

                        map.put("injury_personal_base", detail.getBaseNum());
                        map.put("injury_personal_ratio", detail.getEmployeeRatio());
                        map.put("injury_personal_attach", detail.getEmployeeSurchargeValue());
                        map.put("injury_personal_pay", detail.getEmployeeMoney());

                        map.put("injury_subtotal", detail.getSum());
                        map.put("injury_payment_method", paymentMethod);
                    }
                } else if (productName.indexOf("生育") >= 0) {
                    map.put("fertility_enterprise_base", detail.getBaseNum());
                    map.put("fertility_enterprise_ratio", detail.getCompanyRatio());
                    map.put("fertility_enterprise_attach", detail.getCompanySurchargeValue());
                    map.put("fertility_enterprise_pay", detail.getCompanyMoney());

                    map.put("fertility_personal_base", detail.getBaseNum());
                    map.put("fertility_personal_ratio", detail.getEmployeeRatio());
                    map.put("fertility_personal_attach", detail.getEmployeeSurchargeValue());
                    map.put("fertility_personal_pay", detail.getEmployeeMoney());

                    map.put("fertility_subtotal", detail.getSum());
                    map.put("fertility_payment_method", paymentMethod);
                } else if (productName.indexOf("综合") >= 0) {
                    map.put("complex_enterprise_base", detail.getBaseNum());
                    map.put("complex_enterprise_ratio", detail.getCompanyRatio());
                    map.put("complex_enterprise_attach", detail.getCompanySurchargeValue());
                    map.put("complex_enterprise_pay", detail.getCompanyMoney());

                    map.put("complex_personal_base", detail.getBaseNum());
                    map.put("complex_personal_ratio", detail.getEmployeeRatio());
                    map.put("complex_personal_attach", detail.getEmployeeSurchargeValue());
                    map.put("complex_personal_pay", detail.getEmployeeMoney());

                    map.put("complex_subtotal", detail.getSum());
                    map.put("complex_payment_method", paymentMethod);
                }
                if (!"托收".equals(paymentMethod)) {
                    socialSecurityEnterprise += (detail.getCompanyMoney() == null ? 0.0 : detail.getCompanyMoney());
                    socialSecurityPersonal += (detail.getEmployeeMoney() == null ? 0.0 : detail.getEmployeeMoney());
                    socialSecurityTotal += (detail.getSum() == null ? 0.0 : detail.getSum());
                }
            }
            map.put("social_security_enterprise", socialSecurityEnterprise);
            map.put("social_security_personal", socialSecurityPersonal);
            map.put("social_security_total", socialSecurityTotal);
            map.put("social_provident_total", socialSecurityTotal);
            map.put("total_charge",
                    socialSecurityTotal + (filesDto.getEmployeeOrderFormDto().getServiceFee() == null ? 0.0 :
                            filesDto.getEmployeeOrderFormDto().getServiceFee()));
        }

        return map;
    }

    /**
     * 方法说明：创建支付申请
     * @param paymentApplication 支付申请
     * @param userId
     * @param deptId
     * @param workflowInstanceFacade
     * @return void
     * @author liulei
     * @Date 2020/3/23 14:03
     */
    private void insertPaymentApplication(PaymentApplication paymentApplication, String userId, String deptId,
                                          WorkflowInstanceFacade workflowInstanceFacade) throws Exception{
        if (paymentApplication == null) {
            throw new RuntimeException("创建支付申请失败！");
        }
        // 生成新的支付申请
        paymentApplicationMapper.insertPaymentApplication(paymentApplication);
        // 激活支付申请流程，返回流程实例id
        String workflowId = workflowInstanceFacade.startWorkflowInstance(deptId, userId,
                "payment_application_wf", paymentApplication.getId(), false);
        System.out.println("启动流程:" + workflowId);
    }

    /**
     * 方法说明：判断是否都是“草稿”状态
     * @param ids 支付申请ids
     * @return boolean
     * @author liulei
     * @Date 2020/3/20 15:15
     */
    private boolean isAllDraft(List<String> ids) throws Exception{
        int notDraftAmount = paymentApplicationMapper.getNotDraftAmount(ids);
        return notDraftAmount > 0 ? false : true;
    }
}
