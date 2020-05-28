package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import com.authine.cloudpivot.web.api.entity.QueryInfo;

/**
 * @author liulei
 * @Description 商保service
 * @ClassName com.authine.cloudpivot.web.api.service.BusinessInsuranceService
 * @Date 2020/3/9 10:22
 **/
public interface BusinessInsuranceService {
    /**
     * 方法说明：根据商保增员表单id,生成人员信息表单
     * @param ids 商保增员表单id,多个id使用“,”隔开
     * @return void
     * @author liulei
     * @Date 2020/3/9 10:29
     */
    void addBusinessInsurance(String ids) throws Exception;

    /**
     * 方法说明：根据商保减员表单id，修改对应人员信息表单的福利截止时间，备注
     * @param ids 商保减员表单id,多个id使用“,”隔开
     * @return void
     * @author liulei
     * @Date 2020/3/9 13:21
     */
    void delBusinessInsurance(String ids) throws Exception;

    /**
     * 方法说明：根据商保变更表单id,修改人员信息表单的商保信息（商保套餐等级，服务费，生效日，套餐内容）
     * @param ids 商保变更表单id,多个id使用“,”隔开
     * @return void
     * @author liulei
     * @Date 2020/3/9 13:41
     */
    void updateBusinessInsuranceInfo(String ids) throws Exception;

    /**
     * 方法说明：根据客户名称，身份证号码修改员工补充福利（商保，体检，代发福利）的员工状态
     * @param firstLevelClientName 客户名称
     * @param secondLevelClientName 客户名称
     * @param identityNo 身份证号码
     * @param status 修改后的员工状态（在职，已离职）
     * @return void
     * @author liulei
     * @Date 2020/3/10 10:15
     */
    void updateEmployeeStatus(String firstLevelClientName, String secondLevelClientName, String identityNo,
                              String status) throws Exception;

    /**
     * 方法说明：导入商保数据
     * @param fileName
     * @param user
     * @param dept
     * @param wfCode
     * @param workflowInstanceFacade
     * @return void
     * @author liulei
     * @Date 2020/5/7 15:55
     */
    void importData(String fileName, UserModel user, DepartmentModel dept, String wfCode,
                    WorkflowInstanceFacade workflowInstanceFacade) throws Exception;

    QueryInfo getQueryInfo(String identityNo) throws Exception;
}
