package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yupi.springbootinit.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Excel 相关工具类
 */
@Slf4j
public class ExcelUtils {
    /**
     * excel 转 csv
     * @param multipartFile
     * @param excelType
     * @return
     */
    public static String excelToCsv(MultipartFile multipartFile, String excelType) {
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        ExcelTypeEnum excelTypeEnum = null;
        switch (excelType.toLowerCase()) {
            case "xls":
                excelTypeEnum = ExcelTypeEnum.XLS;
                break;
            case "xlsx":
                excelTypeEnum = ExcelTypeEnum.XLSX;
                break;
            case "csv":
                excelTypeEnum = ExcelTypeEnum.CSV;
                break;
            default:
        }
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(excelTypeEnum)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException | NullPointerException e) {
            log.error("表格处理错误", e);
        }
        // 如果数据为空
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转换为 csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头(第一行)
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据(读取完表头之后，从第一行开始读取)
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        excelToCsv(null, "csv");
    }
}