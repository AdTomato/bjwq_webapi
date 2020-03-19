package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description 商保减员实体
 * @ClassName com.authine.cloudpivot.web.api.entity.DeleteBusinessInsurance
 * @Date 2020/3/14 17:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteBusinessInsurance  extends BaseEntity{
    /** 业务员 */
    String salesman;
    /** 部门 */
    String department;
    /** 公司 */
    String clientName;
    /** 姓名 */
    String employeeName;
    /** 身份证号码 */
    String identityNo;
    /** 福利截止时间*/
    Date benefitDeadline;
    /** 备注*/
    String remarks;

    public DeleteBusinessInsurance(String id, UserModel user, DepartmentModel dept, String sequenceStatus,
                                   String salesman, String department, String clientName, String employeeName,
                                   String identityNo, Date benefitDeadline, String remarks) {
        setId(id);
        setName(employeeName);
        setCreater(user.getId());
        setCreatedDeptId(dept.getId());
        setOwner(user.getId());
        setOwnerDeptId(dept.getId());
        setCreatedTime(new Date());
        setModifier(user.getId());
        setModifiedTime(new Date());
        setSequenceStatus(sequenceStatus);
        setOwnerDeptQueryCode(dept.getQueryCode());
        this.salesman = salesman;
        this.department = department;
        this.clientName = clientName;
        this.employeeName = employeeName;
        this.identityNo = identityNo;
        this.benefitDeadline = benefitDeadline;
        this.remarks = remarks;
    }
}
