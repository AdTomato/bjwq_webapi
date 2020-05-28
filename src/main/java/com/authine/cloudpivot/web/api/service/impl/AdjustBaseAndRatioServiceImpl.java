package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.dao.EmployeesMaintainDao;
import com.authine.cloudpivot.web.api.dto.EmployeeOrderFormDto;
import com.authine.cloudpivot.web.api.entity.*;
import com.authine.cloudpivot.web.api.mapper.AdjustBaseAndRatioMapper;
import com.authine.cloudpivot.web.api.mapper.PolicyCollectPayMapper;
import com.authine.cloudpivot.web.api.service.AdjustBaseAndRatioService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author liulei
 * @Description 调基调比ServiceImpl
 * @ClassName com.authine.cloudpivot.web.api.service.impl.AdjustBaseAndRatioServiceImpl
 * @Date 2020/4/13 17:06
 **/
@Service
public class AdjustBaseAndRatioServiceImpl implements AdjustBaseAndRatioService {

    @Resource
    private AdjustBaseAndRatioMapper adjustBaseAndRatioMapper;

    @Resource
    private PolicyCollectPayMapper policyCollectPayMapper;

    @Resource
    private EmployeesMaintainDao employeesMaintainDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

    @Override
    public AdjustBaseRatioTask getAdjustBaseRatioTaskById(String id) throws Exception {
        return adjustBaseAndRatioMapper.getAdjustBaseRatioTaskById(id);
    }

    @Override
    public void adjustImport(String fileName, AdjustBaseRatioTask task) throws Exception {
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName);
        if (fileList != null && fileList.size() > 2) {
            startImport(fileList, task);
        } else {
            throw new RuntimeException("文件中没有调整数据！");
        }
    }

    @Override
    public void adjustBaseAndRatio(AdjustBaseRatioTask task) throws Exception {
        // 根据任务id,获取调整详细
        List<AdjustBaseRatioDetails> details = adjustBaseAndRatioMapper.getAdjustBaseRatioDetailsByTaskId(task.getId());
        if (details != null && details.size() > 0) {
            startAdjust(task, details);
            adjustBaseAndRatioMapper.updateTaskAdjustInfo(task.getId());
        } else {
            throw new RuntimeException("调整任务没有对应的调整详细！");
        }
    }

    /**
     * 方法说明：开始调整
     * @param task 调整任务
     * @param detailsList 调整详细
     * @return void
     * @author liulei
     * @Date 2020/4/23 15:04
     */
    private void startAdjust(AdjustBaseRatioTask task, List<AdjustBaseRatioDetails> detailsList) throws Exception{
        boolean replaceRatio = false;
        if ("是".equals(task.getReplaceRatioOrNot())) {
            // 调整社保
            replaceRatio = true;
        }
        if ("社保".equals(task.getAdjustType()) && replaceRatio) {
            // 调整社保并替换比例
            if (StringUtils.isBlank(task.getProductId())) {
                throw new RuntimeException("没有调整社保比例的险种信息！");
            }
        }
        List<ProductBaseNum> baseNums = new ArrayList <>();
        Map<String, Integer> baseMap = new HashMap <>();

        int startTime = Integer.parseInt(sdf.format(task.getStartTime()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getStartTime());
        calendar.add(Calendar.DATE, -1);
        Date endTime = calendar.getTime();
        for (int i = 0; i < detailsList.size(); i++) {
            AdjustBaseRatioDetails details = detailsList.get(i);
            // 查询出当前被调整员工的订单数据
            EmployeeOrderFormDto formDto =
                    adjustBaseAndRatioMapper.getEmployeeOrderForm(details.getFirstLevelClientName(),
                            details.getSecondLevelClientName(), details.getIdentityNo(), details.getCity(),
                            details.getWelfareHandler(), task.getAdjustType());
            if (formDto == null) {
                continue;
            }
            String oldId = formDto.getId();
            String newId = UUID.randomUUID().toString().replaceAll("-", "");
            formDto.setId(newId);
            /*formDto.setStartTime(task.getStartTime());*/
            List<SocialSecurityFundDetail> list = formDto.getSocialSecurityFundDetails();
            if(list != null && list.size() > 0) {
                Double sbGjjSum = 0d;
                for (int j = 0; j < list.size(); j++) {
                    SocialSecurityFundDetail ssf = list.get(j);

                    // 重新赋值id，父级id,开始缴纳时间
                    ssf.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                    ssf.setParentId(newId);
                    ssf.setStartChargeTime(task.getStartTime());
                    if ("社保".equals(task.getAdjustType())) {
                        if (ssf.getNameHide().indexOf("公积金") >= 0) {
                            sbGjjSum += ssf.getSum();
                            continue;
                        } else {
                            // 从记录里面获取当前产品的基数信息
                            ProductBaseNum baseNum = this.getProductBaseNum(baseNums, baseMap, ssf.getProductName(),
                                    startTime);
                            if (baseNum == null) {
                                continue;
                            }
                            changeSocialSecurityFundDetail(details, ssf, baseNum, newId, task);
                            /*if (ssf.getNameHide().indexOf("养老") >= 0) {
                                formDto.setEndowment(ssf.getSum() + "");
                            } else if (ssf.getNameHide().indexOf("大病") >= 0) {
                                formDto.setCriticalIllness(ssf.getSum() + "");
                            } else if (ssf.getNameHide().indexOf("医疗") >= 0) {
                                formDto.setMedical(ssf.getSum() + "");
                            } else if (ssf.getNameHide().indexOf("失业") >= 0) {
                                formDto.setUnemployment(ssf.getSum() + "");
                            } else if (ssf.getNameHide().indexOf("工伤") >= 0 && ssf.getNameHide().indexOf("补充") < 0) {
                                formDto.setWorkRelatedInjury(ssf.getSum() + "");
                            } else if (ssf.getNameHide().indexOf("生育") >= 0) {
                                formDto.setChildbirth(ssf.getSum() + "");
                            }*/
                            sbGjjSum += ssf.getSum();
                        }
                    } else if("公积金".equals(task.getAdjustType())) {
                        if (ssf.getNameHide().indexOf("公积金") < 0) {
                            sbGjjSum += ssf.getSum();
                            continue;
                        } else {
                            // 从记录里面获取当前产品的基数信息
                            ProductBaseNum baseNum = this.getProductBaseNum(baseNums, baseMap, ssf.getProductName(), startTime);
                            if (baseNum == null) {
                                continue;
                            }
                            changeSocialSecurityFundDetail(details, ssf, baseNum, newId, task);
                            if (ssf.getNameHide().indexOf("补充") < 0) {
                                /*formDto.setHousingAccumulationFunds(ssf.getSum() + "");*/
                            }
                            sbGjjSum += ssf.getSum();
                        }
                    }
                }
                /*formDto.setSum(sbGjjSum);
                formDto.setTotal(sbGjjSum + formDto.getServiceFee());*/
                addEmployeeOrderForm(formDto, oldId, endTime, task.getAdjustType());
            } else {
                continue;
            }
        }
    }

    private void addEmployeeOrderForm(EmployeeOrderFormDto formDto, String oldId, Date endTime, String adjustType) throws Exception{
        adjustBaseAndRatioMapper.addEmployeeOrderForm(formDto);
        adjustBaseAndRatioMapper.updateOldEmployeeOrderForm(oldId, endTime);
        adjustBaseAndRatioMapper.addSocialSecurityFundDetail(formDto.getSocialSecurityFundDetails());
        adjustBaseAndRatioMapper.updateSocialSecurityFundDetail(oldId, endTime);
        /*employeesMaintainDao.updateDeclareEmployeeOrderFormId(oldId, formDto.getId());*/

        // 更新收费明细的是否比对为否
        /*adjustBaseAndRatioMapper.updateBillDetailsWhetherCompareToNo(sdf.format(formDto.getStartTime()),
                formDto.getFirstLevelClientName(), formDto.getSecondLevelClientName(), formDto.getIdentityNo());
        // 更新支付明细的是否比对为否
        adjustBaseAndRatioMapper.updatePaymentDetailsWhetherCompareToNo(sdf.format(formDto.getStartTime()),
                formDto.getFirstLevelClientName(), formDto.getSecondLevelClientName(), formDto.getIdentityNo(),
                adjustType);*/
    }

    private void changeSocialSecurityFundDetail(AdjustBaseRatioDetails details, SocialSecurityFundDetail ssf,
                                                ProductBaseNum baseNum, String newId, AdjustBaseRatioTask task) throws Exception{
        // 确认新的基数
        Double curBase = details.getNewBase();
        if (curBase > baseNum.getCompanyMaxBaseNum()) {
            curBase = baseNum.getCompanyMaxBaseNum();
        }
        if (curBase < baseNum.getCompanyMinBaseNum()) {
            curBase = baseNum.getCompanyMinBaseNum();
        }
        ssf.setBaseNum(curBase);
        // 判断是否需要更新比例
        if ("是".equals(task.getReplaceRatioOrNot())) {
            if ("公积金".equals(task.getAdjustType())){
                ssf.setCompanyRatio(details.getProvidentFundRatio());
                ssf.setEmployeeRatio(details.getProvidentFundRatio());
            } else if (task.getProductId().equals(ssf.getProductName())){
                // 是社保，且是当前调比的产品
                ssf.setCompanyRatio(task.getCompanyRatio());
                ssf.setEmployeeRatio(task.getEmployeeRatio());
            }
        }
        // 计算新的单位，个人，合计缴纳金额
        ssf.setCompanyMoney(ssf.getBaseNum() * ssf.getCompanyRatio() + ssf.getCompanySurchargeValue());
        ssf.setEmployeeMoney(ssf.getBaseNum() * ssf.getEmployeeRatio() + ssf.getEmployeeSurchargeValue());
        ssf.setSum(ssf.getCompanyMoney() + ssf.getEmployeeMoney());
    }

    private ProductBaseNum getProductBaseNum(List <ProductBaseNum> baseNums, Map <String, Integer> baseMap, String productId,
                                   int startTime) throws Exception {
        if (baseMap.get(productId) == null) {
            List <ProductBaseNum> curBaseNums =
                    policyCollectPayMapper.getProductBaseNumsByParentId(productId);
            if (curBaseNums != null && curBaseNums.size() > 0) {
                ProductBaseNum curBaseNum = getCurBaseNum(curBaseNums, startTime);
                baseNums.add(curBaseNum);
                baseMap.put(productId, baseNums.size() - 1);
            }
        }
        ProductBaseNum baseNum = baseNums.get(baseMap.get(productId));
        return baseNum;
    }

    /**
     * 方法说明：根据开始调整时间获取对应的产品基数
     * @param curBaseNums 产品基数
     * @param time 开始调整时间
     * @return com.authine.cloudpivot.web.api.entity.ProductBaseNum
     * @author liulei
     * @Date 2020/4/23 16:57
     */
    private ProductBaseNum getCurBaseNum(List<ProductBaseNum> curBaseNums, int time) throws Exception{
        if (curBaseNums != null && curBaseNums.size() > 0) {
            for (int i = 0; i < curBaseNums.size(); i++) {
                ProductBaseNum productBaseNum = curBaseNums.get(i);
                if (productBaseNum.getStartTime() == null) {
                    continue;
                }
                int startTime = Integer.parseInt(sdf.format(productBaseNum.getStartTime()));
                int endTime = productBaseNum.getEndTime() == null ? 100000000 : Integer.parseInt(sdf.format(productBaseNum.getEndTime()));
                if (time >= startTime && time <= endTime) {
                    return productBaseNum;
                }
            }
        }
        return null;
    }

    /**
     * 方法说明：处理导入的数据
     * @param fileList
     * @param task
     * @return void
     * @author liulei
     * @Date 2020/4/23 14:18
     */
    private void startImport(List<String[]> fileList, AdjustBaseRatioTask task) throws Exception{
        // excel数据
        List<Map <String, Object>> detailsList = new ArrayList <>();
        // 表头数据
        List<String> fields = Arrays.asList(fileList.get(0));
        for (int i = 2; i < fileList.size(); i++) {
            Map<String, Object> data = CommonUtils.getExcelRowData(fileList.get(i), fields);
            data.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
            data.put("creater", task.getCreater());
            data.put("createdDeptId", task.getCreatedDeptId());
            data.put("createdTime", new Date());
            data.put("modifier", task.getCreater());
            data.put("modifiedTime", new Date());
            data.put("sequenceStatus", "COMPLETED");

            data.put("task_id", task.getId());
            detailsList.add(data);
        }

        for (int j = 0; j < detailsList.size(); j += 500) {
            int size = detailsList.size();
            int toPasIndex = 500;
            if (j + 500 > size) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                toPasIndex = size - j;
            }
            List <Map<String, Object>> details = detailsList.subList(j, j + toPasIndex);
            adjustBaseAndRatioMapper.addAdjustBaseRatioDetails(details);
            adjustBaseAndRatioMapper.updateAdjustBaseRatioDetailsOwner(details);
        }
    }
}
