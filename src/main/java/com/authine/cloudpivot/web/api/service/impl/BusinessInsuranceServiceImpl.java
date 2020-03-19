package com.authine.cloudpivot.web.api.service.impl;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.entity.AddBusinessInsurance;
import com.authine.cloudpivot.web.api.entity.ChildrenInformation;
import com.authine.cloudpivot.web.api.entity.DeleteBusinessInsurance;
import com.authine.cloudpivot.web.api.mapper.BusinessInsuranceMapper;
import com.authine.cloudpivot.web.api.service.BusinessInsuranceService;
import com.authine.cloudpivot.web.api.utils.CommonUtils;
import com.authine.cloudpivot.web.api.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.service.impl.BusinessInsuranceServiceImpl
 * @Date 2020/3/9 10:23
 **/
@Service
public class BusinessInsuranceServiceImpl implements BusinessInsuranceService {

    @Resource
    BusinessInsuranceMapper businessInsuranceMapper;

    @Override
    public void addBusinessInsurance(String ids) throws Exception {
        String idArr[] = ids.split(",");
        // 生成主表数据
        businessInsuranceMapper.addBusinessInsurance(idArr);
        // 生成子女信息表数据
        businessInsuranceMapper.addBusinessInsuranceChildrenInfo(idArr);
    }

    @Override
    public void delBusinessInsurance(String ids) throws Exception {
        String idArr[] = ids.split(",");
        businessInsuranceMapper.updateBenefitDeadlineAndRemarks(idArr);
    }

    @Override
    public void updateBusinessInsuranceInfo(String ids) throws Exception {
        String idArr[] = ids.split(",");
        businessInsuranceMapper.updateBusinessInsuranceInfo(idArr);
    }

    @Override
    public void updateEmployeeStatus(String clientName, String identityNo, String status) throws Exception {
        if (StringUtils.isBlank(status)) {
            throw new RuntimeException("没有获取到修改后的员工状态，更新补充福利员工状态失败");
        }
        if (StringUtils.isBlank(identityNo)) {
            throw new RuntimeException("没有获取到证件号码，更新补充福利员工状态为：”" + status + "“失败");
        }
        if (StringUtils.isBlank(clientName)) {
            throw new RuntimeException("没有获取到客户名称，更新补充福利员工状态为：”" + status + "“失败");
        }
        // 修改员工补充福利的员工状态
        businessInsuranceMapper.updateWelfareSupplementState(clientName, identityNo, status);
        // 修改商保人员信息的员工状态
        businessInsuranceMapper.updateBusinessInsuranceInfoEmployeeStatus(clientName, identityNo, status);
    }

    @Override
    public void addImport(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                          DepartmentModel dept) throws Exception {
        // 保存创建成功的主表id值，用于后面激活流程使用
        List<String> ids = new ArrayList <>();
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName, 2, 0, 12);
        if (fileList != null && fileList.size() > 0) {
            List<AddBusinessInsurance> parents = new ArrayList <>();
            List<ChildrenInformation> childrens = new ArrayList <>();
            // 业务员&&部门
            String salesman = "[{\"id\":\"" + user.getId() + "\",\"type\":3}]";
            String department = "[{\"id\":\"" + dept.getId() + "\",\"type\":1}]";
            // 主表的证件号码
            String identityNo = "";
            // 上一个有效主表数据的证件号码
            String lastIdentityNo = "";
            // 主表的主键id,子表对应的parentId
            String parentId = "";
            // 子表的子女名称
            String childName = "";
            // 子表的子女证件号码
            String childIdentityNo = "";

            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                identityNo = list.get(2);
                childName = list.get(11);
                childIdentityNo = list.get(12);
                if (StringUtils.isNotBlank(identityNo)) {
                    // 主表证件号码不为空，此时判断是否为新增的主表数据，即当前证件号码与上一个证件号码是否一致
                    if (identityNo.equals(lastIdentityNo)) {
                        // 该行数据与上一行数据默认为一个人，此时该行数据为增加子表数据行
                        ChildrenInformation child = createChildrenInformation(parentId, childName, childIdentityNo);
                        if (child != null) {
                            childrens.add(child);
                        }
                    } else {
                        // 此时主表证件号码与上一个证件号码不同，默认此时为新增主表数据行
                        // 更新子表的parentId, 并保存到ids里，用于后面激活流程使用
                        parentId = UUID.randomUUID().toString().replaceAll("-", "");
                        ids.add(parentId);
                        // 更新lastIdentityNo
                        lastIdentityNo = identityNo;
                        AddBusinessInsurance parent = new AddBusinessInsurance(parentId, user, dept, "DRAFT", salesman,
                                department, list.get(0), list.get(1), identityNo, list.get(3), list.get(4),
                                list.get(5), list.get(6), list.get(7), StringUtils.isNotBlank(list.get(8)) ?
                                Double.parseDouble(list.get(8)) : null, StringUtils.isNotBlank(list.get(9)) ?
                                DateUtils.parseDate(list.get(9), Constants.PARSE_PATTERNS) : null, list.get(10));
                        parents.add(parent);
                        // 创建该行数据的子表数据
                        ChildrenInformation child = createChildrenInformation(parentId, childName, childIdentityNo);
                        if (child != null) {
                            childrens.add(child);
                        }
                    }
                } else {
                    // 此时为增加子表数据
                    ChildrenInformation child = createChildrenInformation(parentId, childName, childIdentityNo);
                    if (child != null) {
                        childrens.add(child);
                    }
                }
            }

            // 生成主表数据
            insertAddBusinessInsurance(parents);
            // 生成子表数据
            insertChildrenInformation(childrens);
            // 激活主表数据的流程
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, dept.getId(), user.getId(),
                    "add_business_insurance_wf", ids, false);
        }
    }

    @Override
    public void deleteImport(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                             DepartmentModel dept) throws Exception {
        // 保存创建成功的主表id值，用于后面激活流程使用
        List<String> ids = new ArrayList <>();
        // 读取文件信息
        List <String[]> fileList = ExcelUtils.readFile(fileName, 1, 0, 4);
        if (fileList != null && fileList.size() > 0) {
            List<DeleteBusinessInsurance> delList = new ArrayList <>();
            // 业务员&&部门
            String salesman = "[{\"id\":\"" + user.getId() + "\",\"type\":3}]";
            String department = "[{\"id\":\"" + dept.getId() + "\",\"type\":1}]";
            String id = "";
            for (int i = 0; i < fileList.size(); i++) {
                List <String> list = Arrays.asList(fileList.get(i));
                id = UUID.randomUUID().toString().replaceAll("-", "");
                DeleteBusinessInsurance del = new DeleteBusinessInsurance(id, user, dept, "DRAFT", salesman,
                        department, list.get(0), list.get(1), list.get(2), StringUtils.isNotBlank(list.get(3)) ?
                        DateUtils.parseDate(list.get(3), Constants.PARSE_PATTERNS) : null, list.get(4));
                delList.add(del);
                ids.add(id);
            }

            // 生成主表数据
            insertDeleteBusinessInsurance(delList);
            // 激活主表数据的流程
            CommonUtils.startWorkflowInstance(workflowInstanceFacade, dept.getId(), user.getId(),
                    "delete_business_insurance_wf", ids, false);
        }
    }

    /**
     * 方法说明：批量插入DeleteBusinessInsurance数据
     * @param dels
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:56
     */
    private void insertDeleteBusinessInsurance(List<DeleteBusinessInsurance> dels) {
        // 拆分List,每500一组重新组合
        if (dels != null && dels.size() > 0) {
            List<List<DeleteBusinessInsurance>> groupList = new ArrayList <>();
            int listSize = dels.size();
            int toIndex = 500;
            for (int j = 0; j < dels.size(); j += 500) {
                if (j + 500 > listSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                    toIndex = listSize - j;
                }
                List<DeleteBusinessInsurance> newList = dels.subList(j, j + toIndex);
                groupList.add(newList);
            }
            for (int j = 0; j < groupList.size(); j ++) {
                businessInsuranceMapper.batchInsertDeleteBusinessInsurance(groupList.get(j));
            }
        }
    }

    /**
     * 方法说明：批量插入ChildrenInformation数据
     * @param childrens
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:56
     */
    private void insertChildrenInformation(List<ChildrenInformation> childrens) {
        // 拆分List,每500一组重新组合
        if (childrens != null && childrens.size() > 0) {
            List<List<ChildrenInformation>> groupList = new ArrayList <>();
            int listSize = childrens.size();
            int toIndex = 500;
            for (int j = 0; j < childrens.size(); j += 500) {
                if (j + 500 > listSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                    toIndex = listSize - j;
                }
                List<ChildrenInformation> newList = childrens.subList(j, j + toIndex);
                groupList.add(newList);
            }
            for (int j = 0; j < groupList.size(); j ++) {
                businessInsuranceMapper.batchInsertChildrenInformation(groupList.get(j));
            }
        }
    }

    /**
     * 方法说明：批量插入AddBusinessInsurance数据
     * @param parents
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:56
     */
    private void insertAddBusinessInsurance(List<AddBusinessInsurance> parents) {
        // 拆分List,每500一组重新组合
        if (parents != null && parents.size() > 0) {
            List<List<AddBusinessInsurance>> groupList = new ArrayList <>();
            int listSize = parents.size();
            int toIndex = 500;
            for (int j = 0; j < parents.size(); j += 500) {
                if (j + 500 > listSize) {        //作用为toIndex最后没有500条数据则剩余几条newList中就装几条
                    toIndex = listSize - j;
                }
                List<AddBusinessInsurance> newList = parents.subList(j, j + toIndex);
                groupList.add(newList);
            }
            for (int j = 0; j < groupList.size(); j ++) {
                businessInsuranceMapper.batchInsertAddBusinessInsurance(groupList.get(j));
            }
        }
    }


    /**
     * 方法说明：创建子表中子女实体数据
     * @param parentId 父级id
     * @param childName 子女名称
     * @param childIdentityNo 子女证件号码
     * @return com.authine.cloudpivot.web.api.entity.ChildrenInformation
     * @author liulei
     * @Date 2020/3/14 15:10
     */
    private ChildrenInformation createChildrenInformation(String parentId, String childName, String childIdentityNo) {
        if (StringUtils.isBlank(parentId)) {
            return null;
        }
        if (StringUtils.isNotBlank(childName) || StringUtils.isNotBlank(childIdentityNo)) {
            // 创建子表数据
            return new ChildrenInformation(UUID.randomUUID().toString().replaceAll("-", ""), parentId, null,
                    childName, childIdentityNo);
        } else {
            // 子女姓名和证件号码均为空，此时默认改行是空行
            return null;
        }
    }
}
