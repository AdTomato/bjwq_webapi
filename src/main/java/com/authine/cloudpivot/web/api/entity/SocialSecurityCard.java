package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.engine.api.model.organization.DepartmentModel;
import com.authine.cloudpivot.engine.api.model.organization.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.SocialSecurityCard
 * @Date 2020/3/17 9:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialSecurityCard extends BaseEntity{
    /** 城市,是关联表单*/
    String city;
    /** 线下递交资料清单*/
    String submissionList;
    /**VIP客户*/
    String isVip;
    /** 紧急交办*/
    String isEmerg;
    /** 是否上传办卡材料（是，否）*/
    String uploadInfo;
    /** 业务是否已收卡*/
    String isGet;
    /** 是否接收办卡材料*/
    String receive;
    /** 业务员*/
    String salesman;
    /** 经办人*/
    String operator;
    /** 姓名*/
    String employeeName;
    /** 公司*/
    String companyName;
    /** 身份证号*/
    String identityNo;
    /** 状态(在职,离职)*/
    String employeeStatus;
    /** 手机号码*/
    String mobile;
    /** 联系地址*/
    String address;
    /** 办理状态（未办理，办理中，办理成功，办理失败）*/
    String processSelect;
    /** 办理反馈*/
    String processDetail;
    /** 社保卡号*/
    String sinCard;
    /** 有提交线下材料*/
    String haveSubmissionList;
    /** 是否为运行部发起*/
    String isClass;
    /** 业务跟进记录*/
    String trackLong;
    /** 业务跟进隐藏*/
    String trackHide;
    /** 发卡状态*/
    String issueSelect;
    /** 发卡反馈*/
    String issueDetail;
    /** 业务跟进*/
    String trackDetail;

    public SocialSecurityCard(String id, UserModel user, DepartmentModel dept, String sequenceStatus, String city,
                String isEmerg, String salesman, String employeeName, String companyName,
                String identityNo, String employeeStatus) {
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
        this.city = city;
        this.isEmerg = isEmerg;
        this.salesman = salesman;
        this.employeeName = employeeName;
        this.companyName = companyName;
        this.identityNo = identityNo;
        this.employeeStatus = employeeStatus;
    }
}
