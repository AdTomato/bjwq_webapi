package com.authine.cloudpivot.web.api.controller;

import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.web.api.controller.base.BaseController;
import com.authine.cloudpivot.web.api.service.PolicyPlatformService;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:wangyong
 * @Date:2020/3/13 20:19
 * @Description: 政策平台
 */
@RestController
@RequestMapping("/controller/policyPlatform")
public class PolicyPlatformController extends BaseController {

    @Autowired
    PolicyPlatformService policyPlatformService;

    /**
     * @param type: 类型
     * @param city: 城市
     * @Author: wangyong
     * @Date: 2020/3/13 21:13
     * @return: com.authine.cloudpivot.web.api.view.ResponseResult<java.lang.Object>
     * @Description: 获取一个城市所属相应类型政策的url地址
     */
    @GetMapping("/getDetailUrl")
    public ResponseResult<Object> getPolicyPlatformDetailUrl(@RequestParam String type, @RequestParam String city) {
        Map<String, String> result = getSchemaCodeAndObjectId(type, city);
        if (null == result || StringUtil.isEmpty(result.get("objectId"))) {
            // 没有取得数据
            return this.getErrResponseResult(null, 404L, "没有取得政策数据");
        }

        StringBuffer sb = new StringBuffer();
        String schema = result.get("schema");
        String objectId = result.get("objectId");
        sb.append("/form/detail?");
        sb.append("sheetCode=" + schema + "&");  // sheetCode=injury_policy&
        sb.append("objectId=" + objectId + "&");  // objectId=416aea6bbf2e4a5593c1c504b66896cc&
        sb.append("schemaCode=" + schema + "&");  // schemaCode=injury_policy&
        sb.append("isWorkFlow=false&");
        sb.append("return=/application/policy_platform/application-list/" + schema + "?");  // injury_policy?
        sb.append("parentId=402881c16f880495016fa7dd47d029d9&");
        sb.append("code=" + schema + "&");  // code=injury_policy&
        sb.append("openMode&");
        sb.append("pcUrl&");
        sb.append("queryCode=&");
        sb.append("iframeAction=detail");
        return this.getErrResponseResult(sb.toString(), ErrCode.OK.getErrCode(), ErrCode.OK.getErrMsg());
    }

    /**
     * @param type: 类型
     * @Author: wangyong
     * @Date: 2020/3/13 20:29
     * @return: java.lang.String
     * @Description: 返回该类型该城市所对应的政策数据id
     */
    private Map<String, String> getSchemaCodeAndObjectId(String type, String city) {
        Map<String, String> result = new HashMap<>();
        switch (type) {
            case "工伤保险":
                result.put("schema", "injury_policy");
                result.put("objectId", policyPlatformService.getInjuryPolicyObjectIdByCity(city));
                break;
            case "生育保险":
                result.put("schema", "maternity_insurance");
                result.put("objectId", policyPlatformService.getMaternityInsuranceObjectIdByCity(city));
                break;
            case "失业保险":
                result.put("schema", "unemployment_insurance");
                result.put("objectId", policyPlatformService.getUnemploymentInsuranceObjectIdByCity(city));
                break;
            case "养老保险":
                result.put("schema", "pension");
                result.put("objectId", policyPlatformService.getPensionObjectIdByCity(city));
                break;
            case "医疗保险":
                result.put("schema", "medical_insurance");
                result.put("objectId", policyPlatformService.getMedicalInsuranceObjectIdByCity(city));
                break;
            case "省直公积金":
                result.put("schema", "provincial_direct_providen");
                result.put("objectId", policyPlatformService.getProvincialDirectProvidenObjectIdByCity(city));
                break;
            case "市直公积金":
                result.put("schema", "municipal_provident_fund");
                result.put("objectId", policyPlatformService.getMunicipalProvidentFundObjectIdByCity(city));
                break;
            default:
                result = null;
                break;
        }

        return result;
    }

}
