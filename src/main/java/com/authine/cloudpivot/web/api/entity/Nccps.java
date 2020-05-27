package com.authine.cloudpivot.web.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liulei
 * @Description
 * @ClassName com.authine.cloudpivot.web.api.entity.Nccps
 * @Date 2020/5/11 13:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nccps extends BaseEntity {
    String subordinateDepartment;
    String firstLevelClientName;
    String secondLevelClientName;
    List <NccpsOrderCutOffTime> nccpsOrderCutOffTimes;
    List <NccpsProvidentFundRatio> nccpsProvidentFundRatios;
    List <NccpsWorkInjuryRatio> nccpsWorkInjuryRatios;
}
