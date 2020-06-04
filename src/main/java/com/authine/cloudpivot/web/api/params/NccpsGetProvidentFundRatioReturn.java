package com.authine.cloudpivot.web.api.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 客户城市征缴个性化设置获取公积金比例问题
 * @author wangyong
 * @time 2020/6/4 15:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NccpsGetProvidentFundRatioReturn {

    List<Double> providentFundUnitRatio;

    List<Double> providentFundIndividualRatio;

}
