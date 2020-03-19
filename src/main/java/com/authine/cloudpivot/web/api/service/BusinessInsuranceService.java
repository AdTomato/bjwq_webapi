package com.authine.cloudpivot.web.api.service;

import com.authine.cloudpivot.engine.api.facade.WorkflowInstanceFacade;
import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;

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
     * @param clientName 客户名称
     * @param identityNo 身份证号码
     * @param status 修改后的员工状态（在职，已离职）
     * @return void
     * @author liulei
     * @Date 2020/3/10 10:15
     */
    void updateEmployeeStatus(String clientName, String identityNo, String status) throws Exception;

    /**
     * 方法说明：商保增员导入
     *  导入模板：
     *  公司	姓名	身份证号码	银行卡号	开户行	账号所有人姓名	被保险人手机号	商保套餐等级	商保服务费
     *  商保生效日	商保套餐内容  子女姓名	子女证件号码
     * @param workflowInstanceFacade
     * @param fileName  文件名称
     * @param user 当前人即业务员
     * @param dept 当前部门
     * @return void
     * @author liulei
     * @Date 2020/3/13 17:15
     */
    void addImport(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                   DepartmentModel dept) throws Exception;

    /**
     * 方法说明：商保减员导入
     * @param workflowInstanceFacade
     * @param fileName  文件名称
     * @param user 当前人即业务员
     * @param dept 当前部门
     * @return void
     * @author liulei
     * @Date 2020/3/14 17:33
     */
    void deleteImport(WorkflowInstanceFacade workflowInstanceFacade, String fileName, UserModel user,
                      DepartmentModel dept) throws Exception;
}
