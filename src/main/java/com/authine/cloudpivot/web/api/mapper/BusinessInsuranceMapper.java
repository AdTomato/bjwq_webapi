package com.authine.cloudpivot.web.api.mapper;

import com.authine.cloudpivot.web.api.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulei
 * @Description 商保Mapper
 * @ClassName com.authine.cloudpivot.web.api.mapper.BusinessInsuranceMapper
 * @Date 2020/3/9 10:34
 **/
public interface BusinessInsuranceMapper {
    /**
     * 方法说明：修改对应人员信息
     * @param info
     * @return void
     * @author liulei
     * @Date 2020/5/7 13:56
     */
    void updateBusinessInsuranceInfo(BusinessInsuranceInfo info);

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
     * @param firstLevelClientName 客户名称
     * @param secondLevelClientName 客户名称
     * @param identityNo 身份证号码
     * @param status 修改后的员工状态（在职，已离职）
     * @return void
     * @author liulei
     * @Date 2020/3/10 10:22
     */
    void updateBusinessInsuranceInfoEmployeeStatus(String firstLevelClientName, String secondLevelClientName,
                                                   String identityNo, String status);

    /**
     * 方法说明：批量插入AddBusinessInsurance数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:08
     */
    void batchInsertAddBusinessInsurance(List<Map <String, Object>> list);

    /**
     * 方法说明：批量插入商保变更数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/14 16:08
     */
    void batchInsertUpdateBusinessInsurance(List<Map <String, Object>> list);

    /**
     * 方法说明：批量插入DeleteBusinessInsurance数据
     * @param list
     * @return void
     * @author liulei
     * @Date 2020/3/14 17:30
     */
    void batchInsertDeleteBusinessInsurance(List<Map <String, Object>> list);

    List<AddBusinessInsurance> getAddBusinessInsurance(String[] idArr);

    /**
     * 方法说明：根据身份证号码查询人员信息
     *
     * @param mainIdentityNo
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @return java.lang.String
     * @author liulei
     * @Date 2020/5/7 11:06
     */
    BusinessInsuranceInfo getBusinessInsuranceInfoId(String mainIdentityNo, String firstLevelClientName, String secondLevelClientName);

    /**
     * 方法说明：生成人员信息中附属保险人信息
     * @param child 附属保险人信息
     * @return void
     * @author liulei
     * @Date 2020/5/7 11:19
     */
    void addBusinessInsuranceInfoChild(BusinessInsuranceInfoChild child);

    /**
     * 方法说明：生成人员信息
     * @param info 商保增员数据
     * @return void
     * @author liulei
     * @Date 2020/5/7 11:19
     */
    void addBusinessInsuranceInfo(BusinessInsuranceInfo info);

    /**
     * 方法说明：获取减员信息
     * @param idArr
     * @return java.util.List<com.authine.cloudpivot.web.api.entity.DeleteBusinessInsurance>
     * @author liulei
     * @Date 2020/5/7 13:35
     */
    List<DeleteBusinessInsurance> getDeleteBusinessInsurance(String[] idArr);

    /**
     * 方法说明：获取附属人信息
     * @param identityNo
     * @param firstLevelClientName
     * @param secondLevelClientName
     * @return java.lang.String
     * @author liulei
     * @Date 2020/5/7 13:47
     */
    BusinessInsuranceInfoChild getChildrenInfoId(String identityNo, String firstLevelClientName, String secondLevelClientName);

    void updateChildrenInfo(BusinessInsuranceInfoChild child);

    List<UpdateBusinessInsurance> getUpdateBusinessInsurance(String[] idArr);

    void updateBusinessInsuranceInfoChildEmployeeStatus(String firstLevelClientName, String secondLevelClientName,
                                                        String identityNo, String status);
}
