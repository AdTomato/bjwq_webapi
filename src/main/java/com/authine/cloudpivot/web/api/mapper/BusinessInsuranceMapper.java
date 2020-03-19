package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.AddBusinessInsurance;
import com.authine.cloudpivot.web.api.entity.ChildrenInformation;
import com.authine.cloudpivot.web.api.entity.DeleteBusinessInsurance;

import java.util.List;

/**
 * @author liulei
 * @Description 商保Mapper
 * @ClassName com.authine.cloudpivot.web.api.mapper.BusinessInsuranceMapper
 * @Date 2020/3/9 10:34
 **/
public interface BusinessInsuranceMapper {

    /**
     * 方法说明：根据商保增员表单id,生成人员信息表单
     * @param idArr 商保增员表单id数组
     * @return void
     * @author liulei
     * @Date 2020/3/9 10:38
     */
    void addBusinessInsurance(String[] idArr);

    /**
     * 方法说明：生成子女信息表数据
     * @param idArr 商保增员表单id数组,该id为子女表的parentId
     * @return void
     * @author liulei
     * @Date 2020/3/9 11:21
     */
    void addBusinessInsuranceChildrenInfo(String[] idArr);

    /**
     * 方法说明：根据商保减员表单id修改对应人员信息表单的福利截止时间，备注
     * @param idArr 商保减员表单id数组
     * @return void
     * @author liulei
     * @Date 2020/3/9 13:30
     */
    void updateBenefitDeadlineAndRemarks(String[] idArr);

    /**
     * 方法说明：根据商保变更表单id,修改人员信息表单的商保信息（商保套餐等级，服务费，生效日，套餐内容）
     * @param idArr 商保变更表单id,多个id使用“,”隔开
     * @return void
     * @author liulei
     * @Date 2020/3/9 13:43
     */
    int updateBusinessInsuranceInfo(String[] idArr);

    /**
     * 方法说明：根据客户名称，身份证号码修改员工补充福利的员工状态
     * @param clientName 客户名称
     * @param identityNo 身份证号码
     * @param status 修改后的员工状态（在职，已离职）
     * @return void
     * @author liulei
     * @Date 2020/3/10 10:22
     */
    void updateWelfareSupplementState(String clientName, String identityNo, String status);

    /**
     * 方法说明：根据客户名称，身份证号码修改商保人员信息的员工状态
     * @param clientName 客户名称
     * @param identityNo 身份证号码
     * @param status 修改后的员工状态（在职，已离职）
     * @return void
     * @author liulei
     * @Date 2020/3/10 10:22
     */
    void updateBusinessInsuranceInfoEmployeeStatus(String clientName, String identityNo, String status);

    /**
     * 方法说明：批量插入AddBusinessInsurance数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:08
     */
    void batchInsertAddBusinessInsurance(List<AddBusinessInsurance> list);

    /**
     * 方法说明：批量插入ChildrenInformation数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:08
     */
    void batchInsertChildrenInformation(List<ChildrenInformation> list);

    /**
     * 方法说明：批量插入DeleteBusinessInsurance数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/14 17:30
     */
    void batchInsertDeleteBusinessInsurance(List<DeleteBusinessInsurance> list);
}
