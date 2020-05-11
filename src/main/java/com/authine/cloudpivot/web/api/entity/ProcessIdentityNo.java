package com.authine.cloudpivot.web.api.entity;

import com.authine.cloudpivot.web.api.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.IdentityNo
 * @Date 2020/4/16 11:15
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessIdentityNo {
    String identityNo;
    String gender;
    Date birthday;

    public ProcessIdentityNo(String identityNo) {
        this.identityNo = identityNo;
        String birthdayStr;
        if (identityNo.length() == 18) {
            birthdayStr = identityNo.substring(6, 10) + "-" + identityNo.substring(10, 12) + "-" + identityNo.substring(12,
                    14);
            String sexCode = identityNo.substring(identityNo.length() - 4, identityNo.length() - 1);
            this.gender = Integer.parseInt(sexCode) % 2 == 0 ? "女" : "男";
            try {
                this.birthday = DateUtils.parseDate(birthdayStr, Constants.PARSE_PATTERNS);
            } catch (ParseException e) {
                throw new RuntimeException(birthdayStr + "生成生日出错！");
            }
        } else if (identityNo.length() == 15) {
            birthdayStr = "19" + identityNo.substring(6, 8) + "-" + identityNo.substring(8, 10) + "-" +
                    identityNo.substring(10, 12);
            String sexCode = identityNo.substring(identityNo.length() - 3, identityNo.length());
            this.gender = Integer.parseInt(sexCode) % 2 == 0 ? "女" : "男";
            try {
                this.birthday = DateUtils.parseDate(birthdayStr, Constants.PARSE_PATTERNS);
            } catch (ParseException e) {
                throw new RuntimeException(birthdayStr + "生成生日出错！");
            }
        }

    }
}
