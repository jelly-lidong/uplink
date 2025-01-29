package org.aircas.orbit.file.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONArray;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicEasyExcelExportUtils {

  private static final Logger log = LoggerFactory.getLogger(DynamicEasyExcelExportUtils.class);

  private static final String DEFAULT_SHEET_NAME = "sheet1";

  /**
   * 动态生成导出模版(单表头)
   *
   * @param headColumns 列名称
   * @return excel文件流
   */
  public static byte[] exportTemplateExcelFile(List<String> headColumns) {
    List<List<String>> excelHead = new ArrayList<>(Collections.emptyList());
    headColumns.forEach(columnName -> excelHead.add(Collections.singletonList(columnName)));
    return exportTemplateExcelFileCustomHead(excelHead);
  }

  /**
   * 动态生成模版(复杂表头)
   */
  public static byte[] exportTemplateExcelFileCustomHead(List<List<String>> excelHead) {
    return createExcelFile(excelHead, new ArrayList<>());
  }

  /**
   * 动态导出文件（通过map方式计算）
   */
  public static byte[] exportExcelFile(LinkedHashMap<String, String> headColumnMap, List<Map<String, Object>> dataList) {
    //获取列名称
    List<List<String>> excelHead = new ArrayList<>();
    if (MapUtils.isNotEmpty(headColumnMap)) {
      //key为匹配符，value为列名，如果多级列名用逗号隔开
      headColumnMap.forEach((key, value) -> excelHead.add(Arrays.asList(value.split(","))));
    }
    List<List<Object>> excelRows = new ArrayList<>();
    if (MapUtils.isNotEmpty(headColumnMap) && CollectionUtils.isNotEmpty(dataList)) {
      for (Map<String, Object> dataMap : dataList) {
        List<Object> rows = new ArrayList<>();
        headColumnMap.forEach((key, value) -> {
          if (dataMap.containsKey(key)) {
            Object data = dataMap.get(key);
            rows.add(data);
          }
        });
        excelRows.add(rows);
      }
    }
    return createExcelFile(excelHead, excelRows);
  }


  /**
   * 生成文件（自定义头部排列）
   */
  public static byte[] customerExportExcelFile(List<List<String>> rowHeads, List<List<Object>> excelRows) {
    //将行头部转成easyexcel能识别的部分
    List<List<String>> excelHead = transferHead(rowHeads);
    return createExcelFile(excelHead, excelRows);
  }

  /**
   * 生成文件
   */
  private static byte[] createExcelFile(List<List<String>> excelHead, List<List<Object>> excelRows) {
    try {
      if (CollectionUtils.isNotEmpty(excelHead)) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .head(excelHead)
            .sheet(DEFAULT_SHEET_NAME)
            .doWrite(excelRows);
        return outputStream.toByteArray();
      }
    } catch (Exception e) {
      log.error("动态生成excel文件失败，headColumns：{}，excelRows：{}", JSONArray.toJSONString(excelHead), JSONArray.toJSONString(excelRows), e);
    }
    return null;
  }

  /**
   * 将行头部转成easyexcel能识别的部分
   */
  public static List<List<String>> transferHead(List<List<String>> rowHeads) {
    //将头部列进行反转
    List<List<String>> realHead = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(rowHeads)) {
      Map<Integer, List<String>> cellMap = new LinkedHashMap<>();
      //遍历行
      for (List<String> cells : rowHeads) {
        //遍历列
        for (int i = 0; i < cells.size(); i++) {
          if (cellMap.containsKey(i)) {
            cellMap.get(i).add(cells.get(i));
          } else {
            cellMap.put(i, Collections.singletonList(cells.get(i)));
          }
        }
      }
      //将列一行一行加入realHead
      cellMap.forEach((key, value) -> realHead.add(value));
    }
    return realHead;
  }

  /**
   * 导出文件测试
   */
  public static void main(String[] args) throws IOException {
    //导出包含数据内容的文件（方式一）
    LinkedHashMap<String, String> headColumnMap = new LinkedHashMap<>();
    headColumnMap.put("className", "班级");
    headColumnMap.put("name", "学生信息,姓名");
    headColumnMap.put("sex", "学生信息,性别");
    List<Map<String, Object>> dataList = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Map<String, Object> dataMap = new HashMap<>();
      dataMap.put("className", "一年级");
      dataMap.put("name", "张三" + i);
      dataMap.put("sex", "男");
      dataList.add(dataMap);
    }
    byte[] stream1 = exportExcelFile(headColumnMap, dataList);
    FileOutputStream outputStream1 = new FileOutputStream("E:\\workspace\\spring-cloud-alibaba-lidong\\doc\\test\\easyexcel-export-user5.xlsx");
    outputStream1.write(stream1);
    outputStream1.close();

    //导出包含数据内容的文件（方式二）
    //头部，第一层
    List<String> head1 = new ArrayList<>();
    head1.add("第一行头部列1");
    head1.add("第一行头部列1");
    head1.add("第一行头部列1");
    head1.add("第一行头部列1");
    //头部，第二层
    List<String> head2 = new ArrayList<>();
    head2.add("第二行头部列1");
    head2.add("第二行头部列1");
    head2.add("第二行头部列2");
    head2.add("第二行头部列2");
    //头部，第三层
    List<String> head3 = new ArrayList<>();
    head3.add("第三行头部列1");
    head3.add("第三行头部列2");
    head3.add("第三行头部列3");
    head3.add("第三行头部列4");

    //封装头部
    List<List<String>> allHead = new ArrayList<>();
    allHead.add(head1);
    allHead.add(head2);
    allHead.add(head3);

    //封装数据体
    //第一行数据
    List<Object> data1 = Arrays.asList(1, 1, 1, 1);
    //第二行数据
    List<Object> data2 = Arrays.asList(2, 2, 2, 2);
    List<List<Object>> allData = Arrays.asList(data1, data2);

    byte[] stream2 = customerExportExcelFile(allHead, allData);
    FileOutputStream outputStream2 = new FileOutputStream("E:\\workspace\\spring-cloud-alibaba-lidong\\doc\\test\\easyexcel-export-user6.xlsx");
    outputStream2.write(stream2);
    outputStream2.close();
  }
}

