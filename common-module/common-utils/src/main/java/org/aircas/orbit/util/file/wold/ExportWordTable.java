package org.aircas.orbit.util.file.wold;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJcTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class ExportWordTable {

  public static void main(String[] args) {
    List<Map<Object, Object>> tableList = new ArrayList<Map<Object, Object>>();
    for (int i = 0; i < 2; i++) {
      Map<Object, Object> rowDataMapTbody = new HashedMap<>();
//			rowDataMapTbody.put("0", "rowNum:" + i + "/data1");
      rowDataMapTbody.put("1", "rowNum:" + i + "/data2");
//			rowDataMapTbody.put("2", "rowNum:" + i + "/data3");
      tableList.add(rowDataMapTbody);
    }
    File file = new File("E:\\workspace\\spring-cloud-alibaba-lidong\\doc\\test\\");
//		System.out.println(ExportWordTableSub.exportWordTableForOne(null,file,"exportWordTableForOne") ? "导出成功" : "导出失败");
    System.out.println(ExportWordTableSub.exportWordTableForOne(tableList, file, "exportWordTableForOne") ? "导出成功" : "导出失败");
    System.out.println(ExportWordTableSub.exportWordTableForTwo(file, "exportWordTableForTwo") ? "导出成功" : "导出失败");
  }
}

class ExportWordTableSub {

  /**
   * 术语与定义word表格导出
   *
   * @param tableList 表格数据 为空则导出一个4*3的空白表格
   * @return boolean
   * @throws Exception
   * @Date 2021-11-11
   */
  public static boolean exportWordTableForOne(List<Map<Object, Object>> tableList, File file, String fileName) {
    int colNum = 3;
    if (tableList == null) {
      tableList = new ArrayList<Map<Object, Object>>();
    }
    int rowNum = tableList.isEmpty() ? 4 : tableList.size() + 1;
    //表头
    String[] colDataTwo = {"序号", "术语", "术语定义"};

    XWPFDocument doc = new XWPFDocument();
    //创建段落================标题
    XWPFParagraph paragraph = doc.createParagraph();
    //设置对齐
    paragraph.setAlignment(ParagraphAlignment.CENTER);
    //相同属性得区域
    XWPFRun run = paragraph.createRun();
    //标题文本对齐方式
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    run.setColor("000000");
    run.setText("3  术语与定义");
    run.setFontFamily("楷体");
    run.setFontSize(18);
    run.setBold(true);
    //换行
    //run.addBreak();
    //表格标题
    paragraph = doc.createParagraph();
    //标题文本对齐方式
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    run = paragraph.createRun();
    run.setFontSize(14);
    run.setText("        以及下列术语和定义适用于文本文件");
    run.setFontFamily("楷体");
    //x*x 表格
    XWPFTable table = doc.createTable(rowNum, colNum);
    //表格属性
    CTTblPr tablePr = table.getCTTbl().addNewTblPr();
    //表格宽度
//        CTJc cTJc = tablePr.addNewJc();
    //居中
//        cTJc.setVal(STJc.CENTER);
    //列宽自动分割
    CTTblWidth tableWidth = tablePr.addNewTblW();
    //设置表格宽度
    tableWidth.setType(STTblWidth.DXA);
    tableWidth.setW(BigInteger.valueOf(9072));

    for (int i = 0, index = 0; i < rowNum; i++) {
      Map<Object, Object> rowMap = null;
      //当前行数据
      if (i != 0 && !tableList.isEmpty()) {
        rowMap = tableList.get(index);
        index++;
      }
      //创建行
      XWPFTableRow carRow = table.getRow(i);
      //设置单元格高度
      carRow.setHeight(550);
      //设置单元格宽度
      carRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2268));
      //以创建段落的方式给单元格赋值 当前行第一列
      List<XWPFParagraph> paragraphs = carRow.getCell(0).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      XWPFRun run1 = paragraphs.get(0).createRun();
      run1.setText("");
      run1.setColor("000000");
      if (i == 0) {
        run1.setText(colDataTwo[0]);
        run1.setFontSize(14);
        run1.setBold(true);
      } else if (!tableList.isEmpty()) {
        if (rowMap.containsKey("0")) {
          run1.setText(rowMap.get("0").toString());
        }
        run1.setFontFamily("楷体");
        run1.setFontSize(12);
      }
      carRow.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

      //设置当前行第二列单元格宽度
      carRow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2268));
      //当前行第二列
      paragraphs = carRow.getCell(1).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      run1 = paragraphs.get(0).createRun();
      run1.setText("");
      run1.setColor("000000");
      if (i == 0) {
        run1.setText(colDataTwo[1]);
        run1.setFontSize(14);
        run1.setBold(true);
      } else if (!tableList.isEmpty()) {
        if (rowMap.containsKey("1")) {
          run1.setText(rowMap.get("1").toString());
        }
        run1.setFontFamily("楷体");
        run1.setFontSize(12);
      }
      carRow.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

      //设置当前行第三列单元格宽度
      carRow.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2268));
      //当前行第三列
      paragraphs = carRow.getCell(2).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      run1 = paragraphs.get(0).createRun();
      run1.setText("");
      run1.setColor("000000");
      if (i == 0) {
        run1.setText(colDataTwo[2]);
        run1.setFontSize(14);
        run1.setBold(true);
      } else if (!tableList.isEmpty()) {
        if (rowMap.containsKey("2")) {
          run1.setText(rowMap.get("2").toString());
        }
        run1.setFontFamily("楷体");
        run1.setFontSize(12);
      }
      carRow.getCell(2).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

    }
    //文件输出流
    FileOutputStream os;
    try {
      os = new FileOutputStream(file.getPath() + "\\" + fileName + ".docx");
      doc.write(os);
      os.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  /**
   * 签署页word表格导出
   *
   * @return boolean
   * @throws Exception
   * @Date 2021-11-11
   */
  public static boolean exportWordTableForTwo(File file, String fileName) {
    int colNum = 4;
    int rowNum = 23;
    String[] colDataTwo = {"编制", "校对", "审核", "审定", "批准", "", ""};
    XWPFDocument doc = new XWPFDocument();
    //创建段落================标题
    XWPFParagraph paragraph = doc.createParagraph();
    //设置对齐
    paragraph.setAlignment(ParagraphAlignment.CENTER);
    //相同属性得区域
    XWPFRun run = paragraph.createRun();
    //标题文本对齐方式
    paragraph.setAlignment(ParagraphAlignment.CENTER);
    run.setColor("000000");
    run.setText("签  署  页");
    run.setFontFamily("华文楷体");
    run.setFontSize(32);
    run.setBold(true);
    //x*x 表格
    XWPFTable table = doc.createTable(rowNum, colNum);

    //表格属性
    CTTblPr tablePr = table.getCTTbl().addNewTblPr();
    //表格宽度
    CTJcTable ctJcTable = tablePr.addNewJc();
    //居中
//        ctJcTable.(STJc.CENTER);
    //列宽自动分割
    CTTblWidth tableWidth = tablePr.addNewTblW();
    //设置表格宽度
    tableWidth.setType(STTblWidth.DXA);
    tableWidth.setW(BigInteger.valueOf(9072));

    for (int i = 0, index = 0; i < rowNum; i++) {
      //创建行
      XWPFTableRow carRow = table.getRow(i);
      //设置单元格高度
      carRow.setHeight(550);
      //设置单元格宽度
      carRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1000));
      //以创建段落的方式给单元格赋值 第一列
      List<XWPFParagraph> paragraphs = carRow.getCell(0).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      XWPFRun run1 = paragraphs.get(0).createRun();
      if (i == 0) {
        run1.setText("签署");
        run1.setFontSize(12);
        run1.setFontFamily("宋体");
        run1.setColor("000000");
      } else if (i == 7) {
        run1.setText("会签");
        run1.setFontSize(12);
        run1.setFontFamily("宋体");
        run1.setColor("000000");
      }
      carRow.getCell(0).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

      //设置单元格宽度 第二列
      carRow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2268));
      //以创建段落的方式给单元格赋值
      paragraphs = carRow.getCell(1).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      run1 = paragraphs.get(0).createRun();
      if (i < 7) {
        run1.setText(colDataTwo[i]);
        run1.setFontSize(12);
        run1.setFontFamily("宋体");
        run1.setColor("E4393C");
      } else if (i >= 7 && i <= 8) {
        run1.setText("（会签部门）");
        run1.setFontSize(12);
        run1.setFontFamily("宋体");
        run1.setColor("E4393C");
      }

      carRow.getCell(1).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

      //设置单元格宽度
      carRow.getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2268));
      //以创建段落的方式给单元格赋值
      paragraphs = carRow.getCell(2).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      run1 = paragraphs.get(0).createRun();
      run1.setText("");

      //设置单元格宽度
      carRow.getCell(3).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(2268));
      //以创建段落的方式给单元格赋值
      paragraphs = carRow.getCell(3).getParagraphs();
      paragraphs.get(0).setAlignment(ParagraphAlignment.CENTER);
      paragraphs.get(0).setVerticalAlignment(TextAlignment.CENTER);
      run1 = paragraphs.get(0).createRun();
      run1.setText("");

    }
    //合并行
    mergeVertically(table, 0, 0, 6);
    mergeVertically(table, 0, 7, 22);
    //文件输出流
    FileOutputStream os;
    try {
      os = new FileOutputStream(file.getPath() + "\\" + fileName + ".docx");
      doc.write(os);
      os.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  /**
   * .合并行
   *
   * @param table
   * @param col     需要合并的列
   * @param fromRow 开始行
   * @param toRow   结束行
   * @Date 2021-11-11
   */
  public static void mergeVertically(XWPFTable table, int col, int fromRow, int toRow) {
    for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
      XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
      if (rowIndex == fromRow) {
        cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
      } else {
        cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
      }
    }
    //合并后垂直居中
    table.getRow(fromRow).getCell(col).setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
  }


  /**
   * .合并列
   *
   * @param table
   * @param row      需要合并的行
   * @param fromCell 开始列
   * @param toCell   结束列
   * @Date 2021-11-11
   */
  public static void mergeHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
    for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
      XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
      if (cellIndex == fromCell) {
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
      } else {
        cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
      }
    }
  }
}



