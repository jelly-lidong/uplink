package com.common.model.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("t_payload")
@Schema(description = "卫星载荷信息")
public class Payload {

  @TableId(type = IdType.AUTO)
  @Schema(description = "载荷ID")
  private Long id;

  @Schema(description = "所属卫星ID")
  private Long satelliteId;

  @Schema(description = "载荷名称", example = "载荷1")
  private String payloadName;

  @Schema(description = "载荷类型", example = "光学相机")
  private String payloadType;

  @Schema(description = "载荷编号", example = "PAY-001")
  private String payloadCode;

  @Schema(description = "载荷状态", example = "正常工作")
  private String status;

  @Schema(description = "描述信息")
  private String description;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;
}