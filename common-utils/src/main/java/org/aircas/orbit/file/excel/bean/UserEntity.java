package org.aircas.orbit.file.excel.bean;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson.JSONArray;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UserEntity {

  @ExcelProperty(value = "姓名")
  private String name;

  @ExcelProperty(value = "年龄")
  private int age;

  @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
  @ExcelProperty(value = "操作时间")
  private Date time;


  public static void main(String[] args) throws FileNotFoundException {

//        write();

    read();
  }

  private static void read() throws FileNotFoundException {
    //同步读取文件内容
    List<UserEntity> list;
    try (FileInputStream inputStream = new FileInputStream("E:\\workspace\\spring-cloud-alibaba-lidong\\doc\\test\\easyexcel-export-user1.xlsx")) {
      list = EasyExcel.read(inputStream).head(UserEntity.class).sheet().doReadSync();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    System.out.println(JSONArray.toJSONString(list));
  }

  private static void write() throws FileNotFoundException {
    List<UserEntity> dataList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      UserEntity userEntity = new UserEntity();
      userEntity.setName("张三" + i);
      userEntity.setAge(20 + i);
      userEntity.setTime(new Date(System.currentTimeMillis() + i));
      dataList.add(userEntity);
    }
    //定义文件输出位置
    FileOutputStream outputStream = new FileOutputStream("E:\\workspace\\spring-cloud-alibaba-lidong\\doc\\test\\easyexcel-export-user1.xlsx");
    EasyExcel.write(outputStream, UserEntity.class).sheet("用户信息").doWrite(dataList);
  }

}
