package com.authine.cloudpivot.web.api.controller;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.authine.cloudpivot.engine.api.model.application.AppFunctionModel;
import com.authine.cloudpivot.engine.api.model.bizform.BizFormHeaderModel;
import com.authine.cloudpivot.engine.api.model.bizform.BizFormModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPermGroupModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPermPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizPropertyModel;
import com.authine.cloudpivot.engine.api.model.bizmodel.BizSchemaModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryChildColumnModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryColumnModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryHeaderModel;
import com.authine.cloudpivot.engine.api.model.bizquery.BizQueryModel;
import com.authine.cloudpivot.engine.api.model.runtime.*;
import com.authine.cloudpivot.engine.api.model.system.PairSettingModel;
import com.authine.cloudpivot.engine.component.query.api.FilterExpression;
import com.authine.cloudpivot.engine.component.query.api.Page;
import com.authine.cloudpivot.engine.component.query.api.helper.PageableImpl;
import com.authine.cloudpivot.engine.component.query.api.helper.Q;
import com.authine.cloudpivot.engine.enums.ErrCode;
import com.authine.cloudpivot.engine.enums.status.SequenceStatus;
import com.authine.cloudpivot.engine.enums.type.*;
import com.authine.cloudpivot.web.api.constants.Constants;
import com.authine.cloudpivot.web.api.controller.app.BizSchemaController;
import com.authine.cloudpivot.web.api.controller.base.BaseQueryRuntimeController;
import com.authine.cloudpivot.web.api.dubbo.DubboConfigService;
import com.authine.cloudpivot.web.api.enums.Option;
import com.authine.cloudpivot.web.api.exception.PortalException;
import com.authine.cloudpivot.web.api.exception.ResultEnum;
import com.authine.cloudpivot.web.api.handler.CustomizedOrigin;
import com.authine.cloudpivot.web.api.helper.ExportExcelHelper;
import com.authine.cloudpivot.web.api.helper.FileOperateHelper;
import com.authine.cloudpivot.web.api.task.RunTimeThreadTask;
import com.authine.cloudpivot.web.api.util.IdWorker;
import com.authine.cloudpivot.web.api.util.NumericConvertUtils;
import com.authine.cloudpivot.web.api.utils.SystemDataSetUtils;
import com.authine.cloudpivot.web.api.view.PageVO;
import com.authine.cloudpivot.web.api.view.ResponseResult;
import com.authine.cloudpivot.web.api.view.runtime.FileOperationResult;
import com.authine.cloudpivot.web.api.view.runtime.FilterVO;
import com.authine.cloudpivot.web.api.view.runtime.QueryDataVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.curator.shaded.com.google.common.base.Joiner;
import org.hibernate.validator.internal.util.Contracts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 重写列表查询接口
 * @author wangyong
 * @time 2020/5/28 16:56
 */
@Api(value = "查询列表数据接口", tags = "二次重构::列表查询接口")
@RestController
@RequestMapping("/api/runtime/query")
@Slf4j
@CustomizedOrigin(level = 1)
public class MyQueryRuntimeController extends BaseQueryRuntimeController {

    private static final String FILE_NAME_NOT_EMPTY = "文件名称不能为空";
    private static final String BIZ_SCHEMA_CODE_NOT_EMPTY = "模型编码不能为空";

    @Autowired
    private RunTimeThreadTask springThreadTask;

    @Autowired
    private DubboConfigService dubboConfigService;

    @Autowired
    private IdWorker idworker;

    @ApiOperation(value = "查询数据接口")
    @PostMapping("/list")
    public ResponseResult<PageVO<BizObjectModel>> list(@RequestBody QueryDataVO queryData) {
        validateNotEmpty(queryData.getSchemaCode(), "模型编码不能为空");
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(queryData.getSchemaCode());
        parseFilterVo(queryData, bizSchema);
        if (log.isDebugEnabled()) {
            log.debug("用于查询的条件为：【{}】", (Object) (queryData == null ? null : (queryData.getFilters() == null ? null : JSON.toJSONString(queryData.getFilters()))));
        }
        String userId = getUserId();
        log.debug("用户id为={}", userId);
        Page<BizObjectModel> data = queryBizObject(userId, queryData, false, true);
        if (data == null || CollectionUtils.isEmpty(data.getContent())) {
            this.getOkResponseResult(new PageVO<>(org.springframework.data.domain.Page.empty()), "获取数据成功");
        }
        if (Objects.nonNull(queryData.getMobile()) && queryData.getMobile()) {
            UserFavoritesModel favorite = new UserFavoritesModel();
            favorite.setUserId(getUserId());
            favorite.setBizObjectKey(queryData.getSchemaCode());
            favorite.setBizObjectType(BizObjectType.BIZ_MODEL);
            getUserSettingFacade().addUserFavoriteBizModel(favorite);
        }
        return this.getOkResponseResult(new PageVO<>(data), "获取数据成功");
    }

    @ApiOperation(value = "查询反向关联列表")
    @PostMapping("/list_reverse_sheet")
    public ResponseResult<PageVO<BizObjectModel>> listReverseSheet(@RequestBody QueryDataVO queryData) {
        validateNotEmpty(queryData.getSchemaCode(), "模型编码不能为空");
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(queryData.getSchemaCode());
        parseFilterVo(queryData, bizSchema);
        if (log.isDebugEnabled()) {
            log.debug("用于查询的条件为：【{}】", (Object) (queryData == null ? null : (queryData.getFilters() == null ? null : JSON.toJSONString(queryData.getFilters()))));
        }
        Page<BizObjectModel> data = queryBizObject(getUserId(), queryData, false, false);
        if (data == null || CollectionUtils.isEmpty(data.getContent())) {
            this.getOkResponseResult(new PageVO<>(org.springframework.data.domain.Page.empty()), "获取数据成功");
        }
        return this.getOkResponseResult(new PageVO<>(data), "获取数据成功");
    }


    @ApiOperation(value = "列表模板导出接口")
    @PostMapping("/export_template")
    public void exportTemplate(@RequestBody Map<String, String> map, HttpServletResponse response) {
        String schemaCode = map.get("schemaCode");
        validateCode(schemaCode, BizSchemaController.SCHEMA_CODE_INVALID_MSG);
        final AppFunctionModel functionModel = getAppManagementFacade().getAppFunctionByCode(schemaCode);
        if (functionModel == null) {
            throw new PortalException(ErrCode.APP_FUNCTION_MODEL_NOTEXIST.getErrCode(), ErrCode.APP_FUNCTION_MODEL_NOTEXIST.getErrMsg());
        }
        BizSchemaModel bizSchemaModel = getAppManagementFacade().getBizSchemaBySchemaCode(schemaCode, true);
        String name = bizSchemaModel.getName();
        //固定表头加载顺序，先加载非子表数据项->无内容的子表数据项->有内容的子表数据项
        List<BizQueryColumnModel> columns = getColumns(bizSchemaModel);
        List<BizQueryColumnModel> headerColumns = getHeaderColumns(columns);
        if (CollectionUtils.isEmpty(columns)) {
            throw new PortalException(ErrCode.BIZ_QUERY_NOT_EXIST.getErrCode(), "列表配置信息不存在");
        }
        List<String> headers = headerColumns.stream().map(BizQueryColumnModel::getName).collect(Collectors.toList());
        FileOperateHelper.exportData(response, headers, name, null, headerColumns);
    }


    @ApiOperation(value = "列表数据导出接口")
    @ApiImplicitParam(name = "queryDataVO", value = "导出数据查询模型", required = true, dataType = "query")
    @PostMapping("/export_data")
    public void exportData(@RequestBody QueryDataVO queryData, HttpServletResponse response) {
        long startTimeExport = System.currentTimeMillis();
        String userId = this.getUserId();
        String queryCode = queryData.getQueryCode();
        if (StringUtils.isEmpty(queryCode)) {
            //如果列表编码为空则获取默认第一个列表
            List<BizQueryHeaderModel> bizQueryHeaders = getAppManagementFacade().getBizQueryHeaders(queryData.getSchemaCode());
            queryCode = bizQueryHeaders.get(0).getCode();
        }
        //拿到列表对象，去拿数值数据项的配置信息

        List<BizQueryColumnModel> columns = queryData.getColumns();
        String schemaCode = queryData.getSchemaCode();
        //获取后台配置数据项权限并过滤数据项
        if (CollectionUtils.isEmpty(columns)) {
            log.info("该模型没有可展示数据项");
        }
        permissionData(userId, columns, schemaCode);
        queryData.setMobile(false);
        BizSchemaModel bizSchemaModel = getAppManagementFacade().getBizSchemaBySchemaCode(queryData.getSchemaCode(), true);
        List<BizPropertyModel> propertyModels = bizSchemaModel.getProperties();
        //根据传入的objectIds，为空，导出所有；否则，导出选中的
        List<BizObjectModel> bizObjects = searchObjectId(queryData, userId, propertyModels);
        parseFilterVo(queryData, bizSchemaModel);
        propertyModels = getAllPropertyModels(propertyModels);
        int size = bizObjects.size();
        log.info("bizObject的数量：--{}", size);
        //处理导出的数据项
        machesProperty(columns, bizObjects, queryData, queryCode);
        Map<String, Map<String, Object>> childNumricalFormat = Maps.newHashMap();
        List<Map<String, Object>> datas = getDatas(columns, bizObjects, queryData, childNumricalFormat);
        ExportExcelHelper.exportData(response, columns, datas, propertyModels, childNumricalFormat);
        long endTimeExport = System.currentTimeMillis();
        log.info("导出总时间：--{}", endTimeExport - startTimeExport);
    }

    //子表属性
    private List<BizPropertyModel> getAllPropertyModels(List<BizPropertyModel> bizPropertyModels) {
        List<BizPropertyModel> propertyModels = Lists.newArrayList();
        for (BizPropertyModel bizPropertyModel : bizPropertyModels) {
            if (BizPropertyType.CHILD_TABLE.equals(bizPropertyModel.getPropertyType())) {
                if (bizPropertyModel.getSubSchema() != null) {
                    propertyModels.addAll(getAllPropertyModels(bizPropertyModel.getSubSchema().getProperties()));
                }
            }
            propertyModels.add(bizPropertyModel);
        }
        return propertyModels;
    }


    /**
     * 拿到实际的业务id，选择全部或者勾选部分
     *
     * @param userId    登录用户
     * @param queryData 展示的列表模型数据抽象
     * @param
     * @return void
     */
    //迭代模式下拿数据
    private List<BizObjectModel> searchObjectId(QueryDataVO queryData, String userId, List<BizPropertyModel> properties) {
        List<BizObjectModel> bizObjects = new LinkedList<>();
        String schemaCode = queryData.getSchemaCode();
        List<String> objectIds = Lists.newArrayList();
        objectIds = queryData.getObjectIds();
        if (CollectionUtils.isEmpty(objectIds)) {
            List<BizObjectModel> bizObjectModels = queryExportDatas(userId, queryData, false, true, properties);
            bizObjects.addAll(bizObjectModels);
        } else {
            objectIds = queryData.getObjectIds();
            if (CollectionUtils.isNotEmpty(objectIds)) {
                Iterator<String> iterator = objectIds.iterator();
                while (iterator.hasNext()) {
                    String objectId = iterator.next();
                    BizObjectCreatedModel bizObject = getBizObjectFacade().getBizObject(userId, schemaCode, objectId);
                    BizObjectModel bizObjectModel = bizObject;
                    bizObjects.add(bizObjectModel);
                }
            }
        }
        return bizObjects;
    }

    /**
     * 获取需要导出的列表数据信息权限，并过滤
     *
     * @param userId     登录用户
     * @param columns    展示字段
     * @param schemaCode 模型编码
     * @return void
     */

    private void permissionData(String userId, List<BizQueryColumnModel> columns, String schemaCode) {
        List<BizPermGroupModel> bizPermGroups = getAppManagementFacade().getBizPermGroupsByUserIdAndSchemaCode(userId, schemaCode);

        if (isAdmin(userId) || isSysManager(userId) || CollectionUtils.isEmpty(bizPermGroups)) {
            log.info("权限组未设置，拥有所有权限!");
        } else {
            List<BizPermPropertyModel> permPropertyModels = bizPermGroups.stream().map(BizPermGroupModel::getPermProperties).reduce(new ArrayList<>(), (all, item) -> {
                all.addAll(item);
                return all;
            });
            Set<String> propertyPermCodes = new HashSet<>();
            //获取所有已配置的数据项
            //查看权限
            propertyPermCodes = permPropertyModels.stream().filter(t -> t.getVisible() && t.getBizPermType().equals(BizPermType.CHECK) && t.getVisible()).map(BizPermPropertyModel::getPropertyCode).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(columns)) {
                return;
            }
            List<String> propertyTypes = Arrays.stream(DefaultPropertyType.values()).map(DefaultPropertyType::getCode).collect(Collectors.toList());
            propertyPermCodes.addAll(propertyTypes);
/*            Iterator<BizQueryColumnModel> iterators = columns.iterator();
            while (iterators.hasNext()) {
                BizQueryColumnModel column = iterators.next();
                if (column.getPropertyType().equals(BizPropertyType.CHILD_TABLE)) {
                    if (!(propertyPermCodes.contains(column.getPropertyCode()))) {
//                        columns.remove(column);
                        log.info("该子表无可见权限");
                    } else {
                        List<BizQueryChildColumnModel> childColumns = column.getChildColumns();
                        Iterator<BizQueryChildColumnModel> iterator = childColumns.iterator();
                        while (iterator.hasNext()) {
                            synchronized (childColumns) {
                                BizQueryChildColumnModel childColumn = iterator.next();
                                if (!(propertyPermCodes.contains(childColumn.getPropertyCode()))) {
                                    iterator.remove();
                                }
                            }
                        }
                    }
                }
                if (!(propertyPermCodes.contains(column.getPropertyCode()))) {
                    iterators.remove();
                }
            }*/

            List<BizQueryColumnModel> newList = new LinkedList<>();
            for (BizQueryColumnModel column : columns) {
                if (propertyPermCodes.contains(column.getPropertyCode())) {
                    if (column.getPropertyType().equals(BizPropertyType.CHILD_TABLE)) {
                        List<BizQueryChildColumnModel> childColumns = column.getChildColumns();
                        if (childColumns != null) {
                            for (BizQueryChildColumnModel childColumn : childColumns) {
                                if (propertyPermCodes.contains(childColumn.getPropertyCode())) {
                                    newList.add(column);
                                }
                            }
                        }
                    } else {
                        newList.add(column);
                    }
                }
            }
            if (newList.size() > 0) {
                columns.clear();
                columns.addAll(newList);
            }

        }
    }

    /**
     * 获取需要导出的列表数据信息
     *
     * @param columns    展示字段
     * @param bizObjects 业务数据对象
     * @return List
     */
    protected List<Map<String, Object>> getDatas(List<BizQueryColumnModel> columns, List<BizObjectModel> bizObjects, QueryDataVO queryData, Map<String, Map<String, Object>> childNumricalFormat) {
        log.info("columns" + columns);
        List<Map<String, Object>> datas = new ArrayList<>();
        BizSchemaModel bizSchemaModel = getAppManagementFacade().getBizSchemaBySchemaCode(queryData.getSchemaCode(), true);
        String formCode = null;
        if (StringUtils.isEmpty(formCode)) {
            List<BizFormHeaderModel> bizFormHeaders = getAppManagementFacade().getBizForms(queryData.getSchemaCode());
            if (CollectionUtils.isNotEmpty(bizFormHeaders)) {
                formCode = bizFormHeaders.get(0).getCode();
            }
        }
        BizFormModel sheetModel = getAppManagementFacade().getBizForm(queryData.getSchemaCode(), formCode);
        for (BizObjectModel bizObject : bizObjects) {
            //有序
            Map<String, Object> map = new LinkedHashMap<>();
            for (BizQueryColumnModel column : columns) {
                Map<String, Object> format = Maps.newHashMap();
                if (column.getPropertyType().equals(BizPropertyType.CHILD_TABLE)) {
                    Map<String, Object> data = bizObject.getData();
                    List<Map<String, Object>> objectChildDatas = (List<Map<String, Object>>) data.get(column.getPropertyCode());
                    List<BizQueryChildColumnModel> childColumns = column.getChildColumns();
                    String subSchemaCode = column.getPropertyCode();
                    parseChildDatas(objectChildDatas, queryData, childColumns, subSchemaCode, bizSchemaModel, sheetModel, childNumricalFormat, format);
                    map.put(column.getPropertyCode(), objectChildDatas);
                } else {
                    map.put(column.getPropertyCode(), bizObject.getData().get(column.getPropertyCode()));
                }
            }
            datas.add(map);
        }
        return datas;
    }


    /**
     * 处理主表数据项导出的格式
     *
     * @param columns 展示字段
     * @param data    业务数据对象
     * @return
     */
    protected void machesProperty(List<BizQueryColumnModel> columns, List<BizObjectModel> data, QueryDataVO queryData, String queryCode) {

        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        if (CollectionUtils.isEmpty(columns)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(data)) {
            parseAddressAndReferences(data, columns, true, queryCode, queryData.getSchemaCode());
            if (!queryData.getMobile()) {
                //处理PC端选人
                parseSpecialPropertiess(data, true);
            } else {
                //处理移动端用户图像
                disposeUserInfos(data);
            }
        }

    }


    /**
     * 处理子表数据项导出的格式
     *
     * @param childColumns  展示字段
     * @param maps          业务数据对象
     * @param subSchemaCode
     * @return
     */

    private void parseChildDatas(List<Map<String, Object>> maps, QueryDataVO queryData, List<BizQueryChildColumnModel> childColumns, String subSchemaCode,
                                 BizSchemaModel bizSchemaModel, BizFormModel sheetModel, Map<String, Map<String, Object>> childNumricalFormat, Map<String, Object> format) {
        List<BizPropertyModel> bizProperties = bizSchemaModel.getProperties().stream().filter(t -> t.getCode().equals(subSchemaCode)).collect(Collectors.toList()).get(0).getSubSchema().getProperties().stream().collect(Collectors.toList());
        List<BizPropertyModel> collect = bizProperties.stream().filter(t -> t.getPropertyType().equals(BizPropertyType.NUMERICAL)).collect(Collectors.toList());
        List<BizPropertyModel> collectAddress = bizProperties.stream().filter(t -> t.getPropertyType().equals(BizPropertyType.ADDRESS)).collect(Collectors.toList());
        Map<String, String> mapTemplate = new HashMap<>();
        Map<String, String> mapTemplateAddress = new HashMap<>();

        //地址
        if (CollectionUtils.isNotEmpty(collect) || CollectionUtils.isNotEmpty(collectAddress)) {
            List<Map> childColumnList = super.getChildColumns(sheetModel.getPublishedAttributesJson(), subSchemaCode);
            for (BizPropertyModel bizPropertyModel : collect) {
                if (CollectionUtils.isNotEmpty(childColumnList)) {
                    for (Map childColumn : childColumnList) {
                        if (childColumn.get("key").toString().equals(bizPropertyModel.getCode())) {
                            Map map2 = JSON.parseObject(childColumn.get("options").toString(), Map.class);
                            Object format1 = map2.get("format");
                            if (Objects.isNull(format1)) {
                                continue;
                            }
                            mapTemplate.put(bizPropertyModel.getCode(), format1.toString());
                        }
                    }
                }
            }
            for (BizPropertyModel bizPropertyModel : collectAddress) {
                for (BizQueryChildColumnModel childColumn : childColumns) {
                    if (CollectionUtils.isNotEmpty(childColumnList)) {
                        if (childColumn.getPropertyCode().equals(bizPropertyModel.getCode())) {
                            mapTemplateAddress.put(bizPropertyModel.getCode(), childColumn.getPropertyCode());
                        }
                    }
                }
            }
        }

        for (BizQueryChildColumnModel columnModel : childColumns) {
            for (Map<String, Object> data : maps) {
                if (columnModel.getPropertyType().equals(BizPropertyType.ADDRESS)) {
                    String json = null;
                    if (data.get(columnModel.getPropertyCode()) != null) {
                        json = data.get(columnModel.getPropertyCode()).toString();
                    }
                    if ("{}".equals(json)) {
                        json = null;
                        data.put(columnModel.getPropertyCode(), null);
                    }
                    if (StringUtils.isNotEmpty(json)) {
                        Map map = JSONObject.parseObject(json, Map.class);
                        String name = "";
                        if (map.get("provinceName") != null) {
                            name = name + map.get("provinceName");
                        }
                        if (map.get("cityName") != null) {
                            name = name + map.get("cityName");
                        }
                        if (map.get("districtName") != null) {
                            name = name + map.get("districtName");
                        }
                        if (map.get("address") != null) {
                            name = name + map.get("address");
                        }
                        data.put(columnModel.getPropertyCode(), name);
                    }
                }
                //关联表单
                if (columnModel.getPropertyType().equals(BizPropertyType.WORK_SHEET)) {
                    if (data.get(columnModel.getPropertyCode()) != null) {
                        Object o = data.get(columnModel.getPropertyCode());
                        String newSchemaCode = JSONObject.parseObject(JSONObject.toJSONString(o)).getString("schemaCode");
                        String id = JSONObject.parseObject(JSONObject.toJSONString(o)).getString("id");
                        if (StringUtils.isNotEmpty(newSchemaCode) && StringUtils.isNotEmpty(id)) {
                            //TOdo:
                            BizObjectCreatedModel bizObject2 = getBizObjectFacade().getBizObject(getUserId(), newSchemaCode, id);
                            Object name = bizObject2.get(DefaultPropertyType.NAME.getCode());
                            data.put(columnModel.getPropertyCode(), name == "null" ? null : name);
                        } else {
                            data.put(columnModel.getPropertyCode(), "");
                        }
                    }
                }

                //数值类型
                if (columnModel.getPropertyType().equals(BizPropertyType.NUMERICAL)) {
                    if (data.get(columnModel.getPropertyCode()) == null) {
                        continue;
                    }
                    DecimalFormat df = null;
                    String code = columnModel.getPropertyCode();
                    String displatFormat = mapTemplate.get(code);
                    if (StringUtils.isEmpty(displatFormat)) {
                        displatFormat = "null";
                        format.put(columnModel.getPropertyCode(), displatFormat);
                        childNumricalFormat.put(subSchemaCode, format);
                        continue;
                    }
//                    if (StringUtils.isEmpty(mapTemplate.get(columnModel.getPropertyCode()))){
//                        displatFormat = "null";
//                        format.put(columnModel.getPropertyCode(),displatFormat);
//                        childNumricalFormat.put(subSchemaCode,format);
//                        continue;
//                    }
                    switch (mapTemplate.get(columnModel.getPropertyCode())) {
                        case "integer":
                            df = new DecimalFormat("###,##0");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "tenths":
                            df = new DecimalFormat("###,##0.0");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "percentile":
                            df = new DecimalFormat("###,##0.00");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "Millimeter":
                            df = new DecimalFormat("###,##0.000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "tenThousand":
                            df = new DecimalFormat("###,##0.0000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "ratio":
                            df = new DecimalFormat("###,##0%");
                            BigDecimal ratioResult = (BigDecimal) data.get(columnModel.getPropertyCode());
                            ratioResult = ratioResult.divide(new BigDecimal("100"));
                            data.put(columnModel.getPropertyCode(), df.format(ratioResult));
                            break;
                        case "ratio.tenths":
                            df = new DecimalFormat("###,##0.0" + "%");
                            BigDecimal ratioTenths = (BigDecimal) data.get(columnModel.getPropertyCode());
                            ratioTenths = ratioTenths.divide(new BigDecimal("100"));
                            data.put(columnModel.getPropertyCode(), df.format(ratioTenths));
                            break;
                        case "ratio.percentile":
                            df = new DecimalFormat("###,##0.00" + "%");
                            BigDecimal ratioPercentile = (BigDecimal) data.get(columnModel.getPropertyCode());
                            ratioPercentile = ratioPercentile.divide(new BigDecimal("100"));
                            data.put(columnModel.getPropertyCode(), df.format(ratioPercentile));
                            break;
                        case "ratio.Millimeter":
                            df = new DecimalFormat("###,##0.000" + "%");
                            BigDecimal ratioPercentile1 = (BigDecimal) data.get(columnModel.getPropertyCode());
                            ratioPercentile1 = ratioPercentile1.divide(new BigDecimal("100"));
                            data.put(columnModel.getPropertyCode(), df.format(ratioPercentile1));
                            break;
                        case "ratio.tenThousand":
                            df = new DecimalFormat("###,##0.0000" + "%");
                            BigDecimal ratioPercentile2 = (BigDecimal) data.get(columnModel.getPropertyCode());
                            ratioPercentile2 = ratioPercentile2.divide(new BigDecimal("100"));
                            data.put(columnModel.getPropertyCode(), df.format(ratioPercentile2));
                            break;
                        case "RMB.percentile":
                            df = new DecimalFormat("¥#,##0.00");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "RMB.Millimeter":
                            df = new DecimalFormat("¥#,##0.000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "RMB.tenThousand":
                            df = new DecimalFormat("¥#,##0.0000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "dollar.percentile":
                            df = new DecimalFormat("$#,##0.00");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "dollar.Millimeter":
                            df = new DecimalFormat("$#,##0.000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "dollar.tenThousand":
                            df = new DecimalFormat("$#,##0.0000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "euro.percentile":
                            df = new DecimalFormat("€#,##0.00");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "euro.Millimeter":
                            df = new DecimalFormat("€#,##0.000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "euro.tenThousand":
                            df = new DecimalFormat("€#,##0.0000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "HK.percentile":
                            df = new DecimalFormat("HK$#,##0.00");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "HK.Millimeter":
                            df = new DecimalFormat("HK$#,##0.000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        case "HK.tenThousand":
                            df = new DecimalFormat("HK$#,##0.0000");
                            data.put(columnModel.getPropertyCode(), df.format(data.get(columnModel.getPropertyCode())));
                            break;
                        default:
                            break;
                    }
                }
            }
            if (columnModel.getPropertyType().equals(BizPropertyType.SELECTION)) {
                childParseSpecialPropertiess(maps, queryData, true, columnModel);
            }
            if (columnModel.getPropertyType().equals(BizPropertyType.LOGICAL)) {
                childParselogicalPropertiess(maps, true, columnModel);
            }

        }

    }


    /**
     * 处理PC端子表逻辑控件展示
     *
     * @param datas       分页数据
     * @param columnModel 子表的选人控件字段
     * @param isExport    是否导出
     */
    private void childParselogicalPropertiess(List<Map<String, Object>> datas, Boolean isExport, BizQueryChildColumnModel columnModel) {

        if (CollectionUtils.isNotEmpty(datas)) {
            for (int i = 0; i < datas.size(); i++) {
                Map<String, Object> data = datas.get(i);
                String propertyCode = columnModel.getPropertyCode();
                Object logical = data.get(propertyCode);
                if (logical == null) {
                    String logic = "null";
                    logical = logic;
                }
                log.info("" + logical);
                log.info("data" + data);
                if (StringUtils.isEmpty(logical.toString())) {
                    data.put(columnModel.getPropertyCode(), "否");
                } else {
                    switch (logical.toString()) {
                        case "1":
                            data.put(columnModel.getPropertyCode(), "是");
                            break;
                        case "0":
                            data.put(columnModel.getPropertyCode(), "否");
                            break;
                        case "true":
                            data.put(columnModel.getPropertyCode(), "是");
                            break;
                        case "false":
                            data.put(columnModel.getPropertyCode(), "否");
                            break;
                        default:
                            data.put(columnModel.getPropertyCode(), "否");
                            break;
                    }
                }
            }
        }
    }

    /**
     * 设置用户显示图像
     *
     * @param data 分页数据
     */
    private void disposeUserInfos(List<BizObjectModel> data) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        for (BizObjectModel bizObject : data) {
            List<SelectionValue> selectionValues = (List<SelectionValue>) bizObject.get(DefaultPropertyType.CREATER.getCode());
            if (CollectionUtils.isNotEmpty(selectionValues)) {
                bizObject.put("imgUrl", selectionValues.get(0).getImgUrl());
                bizObject.put("originator", selectionValues.get(0).getName());
            }
        }
    }

    /**
     * 处理位置、数值、日期、关联表单数据
     *
     * @param data
     */
    protected void parseAddressAndReferences(List<BizObjectModel> data, List<BizQueryColumnModel> columns, Boolean isExport, String queryCode, String schemaCode) {
        if (CollectionUtils.isEmpty(columns) || CollectionUtils.isEmpty(data)) {
            return;
        }
        if (isExport) {
            //获取模板信息
            BizQueryModel bizQuery = getAppManagementFacade().getBizQuery(schemaCode, queryCode, ClientType.PC);
            columns = columns.stream().filter(t -> t.getPropertyType().equals(BizPropertyType.ADDRESS) || t.getPropertyType().equals(BizPropertyType.WORK_SHEET) || t.getPropertyType().equals(BizPropertyType.NUMERICAL) || t.getPropertyType().equals(BizPropertyType.DATE)).collect(Collectors.toList());
            for (BizQueryColumnModel columnModel : columns) {
                for (BizObjectModel bizObjectModel : data) {
                    if (columnModel.getPropertyType().equals(BizPropertyType.ADDRESS)) {
                        parseAddress(bizObjectModel, columnModel);
                    }
                    if (columnModel.getPropertyType().equals(BizPropertyType.WORK_SHEET) && !("业务对象ID".equals(columnModel.getName()))) {
                        if (bizObjectModel.get(columnModel.getPropertyCode()) == null) {
                            continue;
                        }
                        if (bizObjectModel.getData().get(columnModel.getPropertyCode()) != null) {
                            Object o1 = bizObjectModel.getData().get(columnModel.getPropertyCode());
                            String newSchemaCode = JSONObject.parseObject(JSONObject.toJSONString(o1)).getString("schemaCode");
                            String id = JSONObject.parseObject(JSONObject.toJSONString(o1)).getString("id");
                            if (StringUtils.isNotEmpty(newSchemaCode) && StringUtils.isNotEmpty(id)) {
                                //TOdo:
                                BizObjectCreatedModel bizObject1 = getBizObjectFacade().getBizObject(getUserId(), newSchemaCode, id);
                                Object o2 = bizObject1.get(DefaultPropertyType.NAME.getCode());
                                bizObjectModel.put(columnModel.getPropertyCode(), o2 == "null" ? null : o2);
                            } else {
                                bizObjectModel.put(columnModel.getPropertyCode(), "");
                            }

                        }
                    }
                    if (columnModel.getPropertyType().equals(BizPropertyType.NUMERICAL)) {
                        parseNumber(bizObjectModel, columnModel, bizQuery);
                    }
                    if (columnModel.getPropertyType().equals(BizPropertyType.DATE)) {
                        parseDate(bizObjectModel, columnModel, bizQuery);
                    }
                }
            }
        }
    }

    //处理地址数据
    private void parseAddress(BizObjectModel bizObjectModel, BizQueryColumnModel columnModel) {
        String json = null;
        if (bizObjectModel.get(columnModel.getPropertyCode()) != null) {
            json = bizObjectModel.get(columnModel.getPropertyCode()).toString();
        }
        if ("{}".equals(json)) {
            json = null;
            bizObjectModel.put(columnModel.getPropertyCode(), null);
        }
        if (StringUtils.isNotEmpty(json)) {
            Map map = JSONObject.parseObject(json, Map.class);
            String name = "";
            if (map.get("provinceName") != null) {
                name = name + map.get("provinceName");
            }
            if (map.get("cityName") != null) {
                name = name + map.get("cityName");
            }
            if (map.get("districtName") != null) {
                name = name + map.get("districtName");
            }
            if (map.get("address") != null) {
                name = name + map.get("address");
            }
            bizObjectModel.put(columnModel.getPropertyCode(), name);
        }
    }

    //处理数值数据
    private void parseNumber(BizObjectModel bizObjectModel, BizQueryColumnModel columnModel, BizQueryModel bizQuery) {

        List<BizQueryColumnModel> queryColumns = bizQuery.getQueryColumns();
        BizQueryColumnModel bizQueryColumnMode = null;
        for (BizQueryColumnModel bizQueryColumnModel : queryColumns) {
            boolean exits = bizQueryColumnModel.getPropertyType().equals(BizPropertyType.NUMERICAL);
            if (exits && Objects.equals(columnModel.getPropertyCode(), bizQueryColumnModel.getPropertyCode())) {
                bizQueryColumnMode = bizQueryColumnModel;
            }
        }
        if (bizObjectModel.getData().get(columnModel.getPropertyCode()) == null) {
            return;
        }
        DecimalFormat df = null;
        if (bizQueryColumnMode == null) {
            df = new DecimalFormat("##0.######");
            bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
            return;
        }
        Integer displayFormat = bizQueryColumnMode.getDisplayFormat();
        if (displayFormat == null) {
            displayFormat = 0;
        }
        switch (displayFormat) {
            case 1:
                df = new DecimalFormat("###,##0");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 2:
                df = new DecimalFormat("###,##0.0");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 3:
                df = new DecimalFormat("###,##0.00");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 11:
                df = new DecimalFormat("###,##0.000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 12:
                df = new DecimalFormat("###,##0.0000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 4:
                df = new DecimalFormat("###,##0%");
                BigDecimal ratioResult = (BigDecimal) bizObjectModel.get(columnModel.getPropertyCode());
                ratioResult = ratioResult.divide(new BigDecimal("100"));
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(ratioResult));
                break;
            case 5:
                df = new DecimalFormat("###,##0.0" + "%");
                BigDecimal ratioTenths = (BigDecimal) bizObjectModel.get(columnModel.getPropertyCode());
                ratioTenths = ratioTenths.divide(new BigDecimal("100"));
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(ratioTenths));
                break;
            case 6:
                df = new DecimalFormat("###,##0.00" + "%");
                BigDecimal ratioPercentile = (BigDecimal) bizObjectModel.get(columnModel.getPropertyCode());
                ratioPercentile = ratioPercentile.divide(new BigDecimal("100"));
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(ratioPercentile));
                break;
            case 13:
                df = new DecimalFormat("###,##0.000" + "%");
                BigDecimal ratioPercentile1 = (BigDecimal) bizObjectModel.get(columnModel.getPropertyCode());
                ratioPercentile1 = ratioPercentile1.divide(new BigDecimal("100"));
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(ratioPercentile1));
                break;
            case 14:
                df = new DecimalFormat("###,##0.0000" + "%");
                BigDecimal ratioPercentile2 = (BigDecimal) bizObjectModel.get(columnModel.getPropertyCode());
                ratioPercentile2 = ratioPercentile2.divide(new BigDecimal("100"));
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(ratioPercentile2));
                break;
            case 7:
                df = new DecimalFormat("¥#,##0.00");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 15:
                df = new DecimalFormat("¥#,##0.000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 16:
                df = new DecimalFormat("¥#,##0.0000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 8:
                df = new DecimalFormat("$#,##0.00");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 17:
                df = new DecimalFormat("$#,##0.000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 18:
                df = new DecimalFormat("$#,##0.0000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 9:
                df = new DecimalFormat("€#,##0.00");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 19:
                df = new DecimalFormat("€#,##0.000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 20:
                df = new DecimalFormat("€#,##0.0000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 10:
                df = new DecimalFormat("HK$#,##0.00");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 21:
                df = new DecimalFormat("HK$#,##0.000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 22:
                df = new DecimalFormat("HK$#,##0.0000");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            default:
                break;
        }

    }

    //处理日期数据
    private void parseDate(BizObjectModel bizObjectModel, BizQueryColumnModel columnModel, BizQueryModel bizQuery) {
        List<BizQueryColumnModel> queryColumns = bizQuery.getQueryColumns();
        BizQueryColumnModel bizQueryColumnMode = null;
        for (BizQueryColumnModel bizQueryColumnModel : queryColumns) {
            String propertyCode = columnModel.getPropertyCode();
            String propertyCode1 = bizQueryColumnModel.getPropertyCode();
            if (StringUtils.isEmpty(propertyCode) || StringUtils.isEmpty(propertyCode1)) {
                continue;
            }
            boolean exits = bizQueryColumnModel.getPropertyType().equals(BizPropertyType.DATE) && Objects.equals(propertyCode, propertyCode1);
            if (exits) {
                if (bizQueryColumnModel == null) {
                    continue;
                }
                bizQueryColumnMode = bizQueryColumnModel;
            }
        }
        if (bizObjectModel.getData().get(columnModel.getPropertyCode()) == null) {
            return;
        }
        SimpleDateFormat df = null;
        if (bizQueryColumnMode == null) {
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
            return;
        }
        Integer displayFormat = bizQueryColumnMode.getDisplayFormat();
        if (displayFormat == null) {
            displayFormat = 0;
        }
        switch (displayFormat) {
            case 1:
                df = new SimpleDateFormat("yyyy-MM-dd") {
                };
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 2:
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 3:
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            case 4:
                df = new SimpleDateFormat("yyyy-MM");
                bizObjectModel.put(columnModel.getPropertyCode(), df.format(bizObjectModel.getData().get(columnModel.getPropertyCode())));
                break;
            default:
                break;
        }
    }

    /*
     *
     *
     * */

    /**
     * 处理PC端选人控件展示
     *
     * @param data 分页数据
     */
    private void parseSpecialPropertiess(List<BizObjectModel> data, Boolean isExport) {
        //获选人数据项
        BizObjectModel objectModel = data.get(0);
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(objectModel.getSchemaCode());
        if (isExport) {
            List<String> selectCodes = getPropertyCodeList(objectModel, BizPropertyType.SELECTION);
            if (CollectionUtils.isEmpty(selectCodes)) {
                return;
            }
            log.debug("需要处理的选人数据项为{}", JSON.toJSONString(selectCodes));
            Set<String> dataCodes = null;
            for (String selectCode : selectCodes) {
                for (BizObjectModel bizObject : data) {
                    if (CollectionUtils.isEmpty(dataCodes)) {
                        dataCodes = bizObject.getData().keySet();
                        log.debug("需要展示的数据项为{}", JSON.toJSONString(dataCodes));
                    }
                    if (!dataCodes.contains(selectCode)) {
                        log.debug("不需要展示的选人数据项为{}", selectCode);
                        break;
                    }
                    if (bizObject.get(selectCode) == null) {
                        continue;
                    }
                    List<SelectionValue> selectionValues = (List<SelectionValue>) bizObject.get(selectCode);
                    bizObject.put(selectCode, getUserNameList(selectionValues));
                }
            }
            //处理单据状态
            if (data.get(0).getData().keySet().contains(DefaultPropertyType.SEQUENCE_STATUS.getCode())) {
                for (BizObjectModel bizObjectModel : data) {
                    bizObjectModel.put(DefaultPropertyType.SEQUENCE_STATUS.getCode(), parseSequenceStatus(bizObjectModel));
                }
            }
            //获取逻辑型
            List<BizPropertyModel> logicalModels = bizSchema.getProperties().stream().filter(t -> t.getPropertyType().equals(BizPropertyType.LOGICAL)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(logicalModels)) {
                List<String> logicals = logicalModels.stream().map(BizPropertyModel::getCode).collect(Collectors.toList());
                Set<String> logicalDataCodes = null;
                for (String logical : logicals) {
                    for (BizObjectModel bizObjectModel : data) {
                        if (CollectionUtils.isEmpty(logicalDataCodes)) {
                            logicalDataCodes = bizObjectModel.getData().keySet();
                        }
                        if (!logicalDataCodes.contains(logical)) {
                            break;
                        }
                        if (bizObjectModel.get(logical) == null) {
                            continue;
                        }
                        bizObjectModel.put(logical, disposeLogics(bizObjectModel.getString(logical)));
                    }

                }
            }
        }

    }

    /**
     * 处理PC端子表选人控件展示
     *
     * @param datas 分页数据
     */
    private void childParseSpecialPropertiess(List<Map<String, Object>> datas, QueryDataVO queryData, Boolean isExport, BizQueryChildColumnModel columnModel) {
        if (CollectionUtils.isNotEmpty(datas)) {
            for (int i = 0; i < datas.size(); i++) {
                Map<String, Object> data = datas.get(i);
                List<SelectionValue> selectionValues = (List<SelectionValue>) data.get(columnModel.getPropertyCode());
//               List<String> nameList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(selectionValues)) {
//                   for (Object selectionValue : selectionValues) {
////                       String name = JSONObject.parseObject(JSONObject.toJSONString(selectionValue)).getString("name");
////                       if (StringUtils.isNotEmpty(name)){
////                           nameList.add(name);
////                       }
//                   }
                    Object name = getUserNameList(selectionValues);
                    if (Objects.nonNull(name)) {
                        log.info("nameList" + name);
//                       String names = Joiner.on(";").join(nameList);
                        log.info("names" + name);
                        if (Objects.nonNull(name)) {
                            data.put(columnModel.getPropertyCode(), name);
                        } else {
                            data.put(columnModel.getPropertyCode(), "");
                        }
                    } else {
                        data.put(columnModel.getPropertyCode(), "");
                    }
                } else {
                    data.put(columnModel.getPropertyCode(), "");
                }
            }
        }
    }

    /**
     * 从列表数据中获取到用户名
     *
     * @return Object
     */
    public Object getUserNameList(List<SelectionValue> selectionValues) {
        if (selectionValues == null) {
            return "";
        }
        List<SelectionValue> unitType = selectionValues.stream().filter(selectionValue -> selectionValue != null && Objects.equals(selectionValue.getType(), UnitType.DEPARTMENT)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(unitType)) {
            return selectionValues.stream().filter(selectionValue -> selectionValue != null && StringUtils.isNotEmpty(selectionValue.getName())).map(SelectionValue::getName).collect(Collectors.joining(";"));
        }
        Map<String, String> deptMap = selectionValues.stream().filter(selectionValue -> ((selectionValue != null) && StringUtils.isNotEmpty(selectionValue.getName()) && StringUtils.isNotEmpty(selectionValue.getId()) && Objects.equals(selectionValue.getType(), UnitType.DEPARTMENT))).collect(Collectors.toMap(SelectionValue::getId, SelectionValue::getName));
        Set<String> deptIds = deptMap.keySet();
        List<String> nameList = Lists.newArrayList();
        for (String id : deptIds) {
            Map<String, String> pathMap = getOrganizationFacade().getParentDepartNamesById(id);
            String path = pathMap.get(id);
            nameList.add(path);
        }
        List<String> userNameList = selectionValues.stream().filter(selectionValue -> ((selectionValue != null) && StringUtils.isNotEmpty(selectionValue.getName()) && StringUtils.isNotEmpty(selectionValue.getId()) && Objects.equals(selectionValue.getType(), UnitType.USER))).map(SelectionValue::getName).collect(Collectors.toList());
        nameList.addAll(userNameList);
        if (CollectionUtils.isEmpty(nameList)) {
            return "";
        }
        if (nameList.get(0) == null) {
            return "";
        }
        return Joiner.on(";").skipNulls().join(nameList);
    }

    /**
     * 处理审批意见
     *
     * @param logic
     * @return
     */
    public Object disposeLogics(String logic) {
        switch (logic) {
            case "1":
                return "是";
            case "0":
                return "否";
            case "true":
                return "是";
            case "false":
                return "否";
            default:
                return "否";
        }

    }

    /**
     * 处理单据状态
     *
     * @param bizObjectModel
     * @return
     */
    private Object parseSequenceStatus(BizObjectModel bizObjectModel) {
        switch (bizObjectModel.get(DefaultPropertyType.SEQUENCE_STATUS.getCode()).toString()) {
            case "DRAFT":
                return SequenceStatus.DRAFT.getName();
            case "PROCESSING":
                return SequenceStatus.PROCESSING.getName();
            case "CANCELLED":
                return SequenceStatus.CANCELLED.getName();
            case "COMPLETED":
                return SequenceStatus.COMPLETED.getName();
            case "CANCELED":
                return SequenceStatus.CANCELED.getName();

            default:
                return null;
        }
    }


    /**
     * 处理子表单据状态
     *
     * @param bizObjectModel
     * @return
     */
    private Object parseChildSequenceStatus(Map<String, Object> bizObjectModel) {
        switch (bizObjectModel.get(DefaultPropertyType.SEQUENCE_STATUS.getCode()).toString()) {
            case "DRAFT":
                return SequenceStatus.DRAFT.getName();
            case "PROCESSING":
                return SequenceStatus.PROCESSING.getName();
            case "CANCELLED":
                return SequenceStatus.CANCELLED.getName();
            case "COMPLETED":
                return SequenceStatus.COMPLETED.getName();
            default:
                return null;

        }
    }


    private List<String> getChildPropertyCodeList(Map<String, Object> objectModel, BizPropertyType propertyType, QueryDataVO queryDataVO) {
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(queryDataVO.getSchemaCode());
        List<BizPropertyModel> bizPropertyModels = bizSchema.getProperties().stream().filter(t -> t.getPropertyType().equals(propertyType)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bizPropertyModels)) {
            return bizPropertyModels.stream().map(BizPropertyModel::getCode).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    /*
     *
     *
     * */
    private List<String> getPropertyCodeList(BizObjectModel objectModel, BizPropertyType propertyType) {
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(objectModel.getSchemaCode());
        List<BizPropertyModel> bizPropertyModels = bizSchema.getProperties().stream().filter(t -> t.getPropertyType().equals(propertyType)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bizPropertyModels)) {
            return bizPropertyModels.stream().map(BizPropertyModel::getCode).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }


    @ApiOperation(value = "列表文件上传")
    @PostMapping("/upload_file")
    public String importFile(MultipartFile file) {
        return FileOperateHelper.uploadFile(file);
    }

    @ApiOperation(value = "列表数据导入接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "模型编码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "schemaCode", value = "模型编码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryCode", value = "列表编码", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "queryField", value = "查询字段", required = false, dataType = "String", paramType = "query")
    })
    @GetMapping("/import_data")
    public ResponseResult<FileOperationResult> importData(@NotBlank(message = FILE_NAME_NOT_EMPTY) @RequestParam String fileName,
                                                          @NotBlank(message = BIZ_SCHEMA_CODE_NOT_EMPTY) @RequestParam String schemaCode,
                                                          @RequestParam(required = false) String queryCode,
                                                          @RequestParam(required = false) String queryField) {
        String userId = this.getUserId();
        log.debug("用户id为={}", userId);
        FileOperationResult fileOperationResult = new FileOperationResult();
        fileOperationResult.setSuccessCount(0);
        fileOperationResult.setErrorCount(0);
        fileOperationResult.setOperationResult(true);
        springThreadTask.importDatas(userId, fileName, schemaCode, queryCode, queryField);
        return this.getOkResponseResult(fileOperationResult, "操作成功");
    }


    @ApiOperation(value = "获取列表导入进度")
    @GetMapping("/import_progress")
    public ResponseResult<FileOperationResult> importProgess() {
        FileOperationResult fileOperationResult;
        fileOperationResult = (FileOperationResult) BaseQueryRuntimeController.mapFileOperateInfo.get(getUserId());
        if (fileOperationResult == null) {
            FileOperationResult fileOperation = new FileOperationResult();
            fileOperation.setOperationResult(false);
            return this.getOkResponseResult(fileOperation, "数据导入进度结果查询");
        }
        return this.getOkResponseResult(fileOperationResult, "数据导入进度结果查询");
    }

    @ApiOperation(value = "检查删除数据接口")
    @ApiImplicitParam(name = "params", value = "{\n" +
            "\"ids\":[\"id1\",\"id2\"],\n" +
            "\"schemaCode\":\"模型编码\"\n" +
            "}", required = true, dataType = "Map")
    @PostMapping("/checkForRemoveBizObject")
    public ResponseResult<List<CheckResultForRemoveBizObject>> checkForRemoveBizObject(@RequestBody Map<String, Object> params) {
        List<String> ids = MapUtils.isNotEmpty(params) ? (List<String>) params.get("ids") : null;
        String schemaCode = StringUtils.isNotEmpty(params.get("schemaCode").toString()) ? params.get("schemaCode").toString() : null;
        validateParams(ids, schemaCode);
        String userId = this.getUserId();
        //过滤有权限删除的数据
        List<String> noDeleteIds = Lists.newArrayList();
        disposeDeletePermissions(userId, schemaCode, ids, noDeleteIds);
        if (CollectionUtils.isNotEmpty(ids) || CollectionUtils.isNotEmpty(noDeleteIds)) {
            List<CheckResultForRemoveBizObject> data = getBizObjectFacade().checkForRemoveBizObject(schemaCode, ids, noDeleteIds);
            return this.getOkResponseResult(data, "删除数据检查成功");
        }
        return this.getErrResponseResult(null, -1L, "没有可被删除的数据");
    }

    private void validateParams(List<String> ids, String schemaCode) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new PortalException(ResultEnum.ILLEGAL_PARAMETER_ERR.getErrCode(), "id不能为空");
        }
        if (StringUtils.isEmpty(schemaCode)) {
            throw new PortalException(ResultEnum.ILLEGAL_PARAMETER_ERR.getErrCode(), "模型编码不能为空");
        }
    }

    @ApiOperation(value = "列表数据删除接口")
    @ApiImplicitParam(name = "params", value = "{\n" +
            "\"ids\":[\"id1\",\"id2\"],\n" +
            "\"schemaCode\":\"模型编码\"\n" +
            "}", required = true, dataType = "Map")
    @DeleteMapping("/delete_data")
    public ResponseResult<Map<String, Object>> deleteData(@RequestBody Map<String, Object> params) {
        List<String> ids = MapUtils.isNotEmpty(params) ? (List<String>) params.get("ids") : null;
        String schemaCode = StringUtils.isNotEmpty(params.get("schemaCode").toString()) ? params.get("schemaCode").toString() : null;
        validateParams(ids, schemaCode);
        String userId = this.getUserId();

        int allCount = ids.size();
        //过滤有权限删除的数据
        List<String> noDeleteIds = Lists.newArrayList();
        disposeDeletePermissions(userId, schemaCode, ids, noDeleteIds);
        int filterCount = 0;
        Map<String, Object> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ids)) {
            filterCount = ids.size();
            getBizObjectFacade().removeBizObjects(userId, schemaCode, ids);
        }
        map.put("errorCount", allCount - filterCount);
        map.put("successCount", filterCount);
        return this.getOkResponseResult(map, "删除列表数据成功");
    }

    @ApiOperation(value = "列表错误信息导出")
    @ApiImplicitParam(name = "params", value = "{\n" +
            "\"fileName\":\"文件名\",\n" +
            "}", required = true, dataType = "Map")
    @PostMapping("/download_result")
    public void downloadErrorData(@NotBlank(message = BIZ_SCHEMA_CODE_NOT_EMPTY) @RequestBody Map<String, Object> params, HttpServletResponse response) {
        String fileName = params.get("fileName").toString();
        validateNotEmpty(fileName, "文件名不能为空");
        FileOperateHelper.downloadErrorInfo(response, fileName);
    }

    /**
     * 处理数据项类型
     *
     * @param queryData
     * @param bizSchema
     */
    private void parseFilterVo(QueryDataVO queryData, BizSchemaModel bizSchema) {
        if (CollectionUtils.isNotEmpty(bizSchema.getProperties())) {
            Map<String, List<BizPropertyModel>> listMap = bizSchema.getProperties().stream().collect(Collectors.groupingBy(BizPropertyModel::getCode));
            if (CollectionUtils.isNotEmpty(queryData.getFilters())) {
                for (FilterVO filterVO : queryData.getFilters()) {
                    if (filterVO.getPropertyType() == null) {
                        filterVO.setPropertyType(listMap.get(filterVO.getPropertyCode()).get(0).getPropertyType());
                    }
                }
            }
        }
    }

    /**
     * 批量生成短链短码
     * 该接口不考虑重复请求或相同pairValue，单次请求生成一批断码
     *
     * @param pairValueList
     * @return
     */
    @ApiOperation(value = "批量生成短链短码", notes = "批量生成短链短码")
    @PostMapping("/genShortCodes")
    @ApiImplicitParam(name = "pairValueList", value = "[\n" +
            "    {\n" +
            "        \"pairValue\": \"{\\\"sheetCode\\\":\\\"sahngpin\\\",\\\"schemaCode\\\":\\\"department_planning_clerk\\\",\\\"id\\\":\\\"89f3b6c77b7043219e01d10453fdb60a\\\"}\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"pairValue\": \"{\\\"sheetCode\\\":\\\"sahngpin\\\",\\\"schemaCode\\\":\\\"department_planning_clerk\\\",\\\"id\\\":\\\"38b04c72e2e747ecb52e1f99d56fe397\\\"}\"\n" +
            "    }\n" +
            "]", required = true, dataType = "List")
    public ResponseResult<List<PairSettingModel>> genShortCodes(@RequestBody List<PairSettingModel> pairValueList) {
        int len = pairValueList.size();
        Set<String> sets = Sets.newHashSet();
        for (; ; ) {
            String pairCode = NumericConvertUtils.toOtherNumberSystem(idworker.nextId(), 62);
            PairSettingModel settingModel = dubboConfigService.getBizObjectFacade().findByPairCode(pairCode);
            String tempCode = Objects.nonNull(settingModel) ? settingModel.getPairCode() : null;
            if (Objects.equals(pairCode, tempCode)) {
                log.info("短码重复：{}={}", pairCode, tempCode);
                continue;
            }
            log.info("+++++++++++pairCode：{}", sets.contains(pairCode));
            sets.add(pairCode);
            if (len == sets.size()) {
                break;
            }
        }
        log.info("===========sets：{}", sets);
        for (PairSettingModel pairSettingModel : pairValueList) {
            sets.forEach(t -> {
                pairSettingModel.setPairCode(t);
            });
        }
        List<PairSettingModel> modelList = dubboConfigService.getBizObjectFacade().savePairCodes(pairValueList);
        return getOkResponseResult(modelList, "批量生成短链短码成功");
    }

    /**
     * 根据短码返回短链参数
     *
     * @param pairCode
     * @return
     */
    @ApiOperation(value = "根据短码返回短链参数", notes = "根据短码返回短链参数")
    @GetMapping("/getShortCode")
    @ApiImplicitParam(name = "pairCode", value = "短码", required = true, dataType = "String", paramType = "query")
    public ResponseResult<PairSettingModel> getShortCode(@RequestParam String pairCode) {
        validateNotEmpty(pairCode, "参数短码不能为空");
        PairSettingModel settingModel = dubboConfigService.getBizObjectFacade().findByPairCode(pairCode);
        return getOkResponseResult(settingModel, "获取短码成功");
    }

    /**
     * 根据模型编码获取列表头信息
     *
     * @param schemaCode
     * @return
     */
    @ApiOperation(value = "根据模型编码获取列表头信息", notes = "根据模型编码获取列表头信息,返回全部PC/APP端可见列表类型")
    @GetMapping("/getHeaders")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemaCode", value = "模型编码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "clientType", value = "客户端类型", required = false, dataType = "ClientType", paramType = "query")
    })
    public ResponseResult<List<BizQueryHeaderModel>> getHeaders(String schemaCode, ClientType clientType) {
        log.debug("\n调用引擎接口【getBizQueryHeaders】获取列表配置信息，参数：schemaCode=【{}】,clientType=【{}】", schemaCode, clientType);
        validateNotEmpty(schemaCode, "必传参数schemaCode不能为空");
        /*final AppFunctionModel functionModel = getAppManagementFacade().getAppFunctionByCode(schemaCode);
        validateAppPermission(functionModel.getAppCode());*/
        List<BizQueryHeaderModel> headerModels = getAppManagementFacade().getBizQueryHeaders(schemaCode);
        if (CollectionUtils.isNotEmpty(headerModels)) {
            //过滤 已发布 并 排序
            headerModels = headerModels.stream().filter(header -> Objects.equals(Boolean.TRUE, header.getPublish())).sorted(Comparator.comparingInt(BizQueryHeaderModel::getSortKey)).collect(Collectors.toList());

            //返回PC/APP端数据
            headerModels = Objects.equals(clientType, ClientType.APP) ? headerModels.stream().filter(header -> Objects.equals(Boolean.TRUE, header.getShowOnMobile())).collect(Collectors.toList()) : headerModels.stream().filter(header -> Objects.equals(Boolean.TRUE, header.getShowOnPc())).collect(Collectors.toList());

            return getOkResponseResult(headerModels, "根据模型编码获取列表头信息成功");
        }
        return getOkResponseResult(Collections.EMPTY_LIST, "根据模型编码获取列表头信息成功");
    }


    @ApiOperation(value = "查询数据接口")
    @PostMapping("/listSkipQueryList")
    public ResponseResult<PageVO<BizObjectModel>> listSkipQuery(@RequestBody QueryDataVO queryData) {
        validateNotEmpty(queryData.getSchemaCode(), "模型编码不能为空");
        BizSchemaModel bizSchema = getAppManagementFacade().getBizSchemaBySchemaCode(queryData.getSchemaCode());
        bizSchema.setPublished(false);
        parseFilterVo(queryData, bizSchema);
        if (log.isDebugEnabled()) {
            log.debug("用于查询的条件为：【{}】", (Object) (queryData == null ? null : (queryData.getFilters() == null ? null : JSON.toJSONString(queryData.getFilters()))));
        }
        String userId = getUserId();
        log.debug("用户id为={}", userId);
        log.info("调用重构的查询方法");

        Page<BizObjectModel> data = queryBizObjectSkipQuery(queryData);
        if (data == null || CollectionUtils.isEmpty(data.getContent())) {
            this.getOkResponseResult(new PageVO<>(org.springframework.data.domain.Page.empty()), "获取数据成功");
        }
        if (Objects.nonNull(queryData.getMobile()) && queryData.getMobile()) {
            UserFavoritesModel favorite = new UserFavoritesModel();
            favorite.setUserId(getUserId());
            favorite.setBizObjectKey(queryData.getSchemaCode());
            favorite.setBizObjectType(BizObjectType.BIZ_MODEL);
            getUserSettingFacade().addUserFavoriteBizModel(favorite);
        }
        return this.getOkResponseResult(new PageVO<>(data), "获取数据成功");
    }

    private Page<BizObjectModel> queryBizObjectSkipQuery(QueryDataVO queryData) {
        List<FilterVO> filterVOList = queryData.getFilters();
        List<FilterExpression> filters = new ArrayList<>();
        for (FilterVO filterVO : filterVOList) {
            Option op = filterVO.getOp();
            if (op == null) {
                continue;
            }
            FilterExpression.Op filterOP = getFilterExpressionOP(op);
//            选人控件包含不包含特殊处理
            if ((filterVO.getOp() == Option.Like || filterVO.getOp() == Option.NotLike) && filterVO.getPropertyType() == BizPropertyType.SELECTION) {
                if (StringUtils.isNotEmpty(filterVO.getValue())) {
                    List<Map> mapList = JSONObject.parseArray(filterVO.getValue(), Map.class);
                    for (Map map : mapList) {
                        FilterExpression.Item items = Q.it(filterVO.getPropertyCode(), filterOP, map.get("id").toString());
                        filters.add(items);
                    }
                }
            } else if ((filterVO.getOp() == Option.Eq || filterVO.getOp() == Option.NotEq)
                    && filterVO.getPropertyType() == BizPropertyType.SELECTION
                    && isDefaultProperty(filterVO.getPropertyCode())) {
                if (StringUtils.isNotEmpty(filterVO.getValue())) {
                    List<Map> mapList = JSONObject.parseArray(filterVO.getValue(), Map.class);
                    StringBuilder sb = new StringBuilder();
                    for (Map map : mapList) {
                        sb.append(map.get("id").toString());
                    }
                    FilterExpression.Item items = Q.it(filterVO.getPropertyCode(), filterOP, sb.toString());
                    filters.add(items);
                }
            } else if ((filterVO.getOp() == Option.Eq || filterVO.getOp() == Option.NotEq)
                    && (filterVO.getPropertyType() == BizPropertyType.SHORT_TEXT || filterVO.getPropertyType() == BizPropertyType.LONG_TEXT)
                    && StringUtils.isEmpty(filterVO.getValue())) {
                FilterExpression.Or or = Q.or(Q.it(filterVO.getPropertyCode(), filterOP, ""), Q.it(filterVO.getPropertyCode(), filterOP, null));
                filters.add(or);
            } else if (filterVO.getPropertyType() == BizPropertyType.LOGICAL) {
                FilterExpression.Item items = Q.it(filterVO.getPropertyCode(), filterOP, filterVO.getPropertyValue().get(0));
                filters.add(items);
            } else if (filterVO.getPropertyType() == BizPropertyType.DATE) {
                FilterExpression.Item items = Q.it(filterVO.getPropertyCode(), filterOP, getDate(filterVO.getValue()));
                filters.add(items);
            } else {
                FilterExpression.Item items = Q.it(filterVO.getPropertyCode(), filterOP, filterVO.getValue());
                filters.add(items);
            }
        }
        //过滤掉草稿作废取消数据
        filters.add(Q.it(DefaultPropertyType.SEQUENCE_STATUS.getCode(), FilterExpression.Op.NotEq, SequenceStatus.DRAFT.toString()));
        filters.add(Q.it(DefaultPropertyType.SEQUENCE_STATUS.getCode(), FilterExpression.Op.NotEq, SequenceStatus.CANCELED.toString()));
        filters.add(Q.it(DefaultPropertyType.SEQUENCE_STATUS.getCode(), FilterExpression.Op.NotEq, SequenceStatus.CANCELLED.toString()));
        //权限条件
        FilterExpression authFilter = FilterExpression.empty;
        String userId = getUserId();
        String schemaCode = queryData.getSchemaCode();
        if (SystemDataSetUtils.isCanShowAllData(schemaCode)) {
            log.info("当前表单需要全部显示");
            userId = Constants.ADMIN_ID;
        }
        authFilter = getAuthFilters(userId, schemaCode);
        if (!authFilter.equals(FilterExpression.empty)) {
            filters.add(authFilter);
        }
        FilterExpression filterExprTemplate = FilterExpression.empty;
        if (filters.size() > 1) {
            filterExprTemplate = Q.and(filters);
            log.debug("获取的相关条件为{}", JSON.toJSONString(filterExprTemplate));
        } else if (filters.size() == 1) {
            filterExprTemplate = filters.get(0);
            log.debug("获取的相关条件为{}", JSON.toJSONString(filterExprTemplate));
        }
        BizObjectQueryModel bizObjectQueryModel = new BizObjectQueryModel();
        bizObjectQueryModel.setFilterExpr(filterExprTemplate);
        bizObjectQueryModel.setSchemaCode(queryData.getSchemaCode());
        bizObjectQueryModel.setOptions(queryData.getOptions());
        bizObjectQueryModel.setGroupByFields(queryData.getGroupByFields());
        //分页信息
        int page = queryData.getPage() == null ? 0 : queryData.getPage();
        //int page = 0;
        int size = queryData.getSize();
        PageableImpl pageable = new PageableImpl(page * size, size);
        bizObjectQueryModel.setPageable(pageable);

        Page<BizObjectModel> data = getBizObjectFacade().queryBizObjectSkipQuery(bizObjectQueryModel);
        return data;
    }

    private Date getDate(Object value) {
        if (value == null || StringUtils.isEmpty(value.toString())) {
            return null;
        }
        String[] pattern = {"yyyy-MM", "yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss"};
        Date date;
        try {
            date = DateUtils.parseDate(value.toString(), pattern);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return date;
    }

    private boolean isDefaultProperty(String propertyCode) {
        for (DefaultPropertyType propertyType : DefaultPropertyType.values()) {
            if (propertyType.getCode().equalsIgnoreCase(propertyCode)) {
                return true;
            }
        }
        return false;
    }

    private FilterExpression.Op getFilterExpressionOP(Option op) {
        switch (op) {
            case Eq:
                return FilterExpression.Op.Eq;
            case NotEq:
                return FilterExpression.Op.NotEq;
            case Gt:
                return FilterExpression.Op.Gt;
            case Lt:
                return FilterExpression.Op.Lt;
            case Like:
                return FilterExpression.Op.Like;
            case NotLike:
                return FilterExpression.Op.NotLike;
        }
        return null;
    }

    @ApiOperation(value = "导入失败重试", notes = "导入失败重试")
    @PostMapping("/second_import_data")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "headColumns", value = "列表数据模型对象集合", required = true, dataType = "List", paramType = "query"),
//            @ApiImplicitParam(name = "data", value = "失败重试的数据", required = true, dataType = "List", paramType = "query"),
//            @ApiImplicitParam(name = "queryField", value = "查询字段", required = true, dataType = "String", paramType = "query"),
//    })
    public ResponseResult<FileOperationResult> getImportData(@RequestBody FileOperationResult operationResult) {

        return secondImportData(operationResult);
    }

}