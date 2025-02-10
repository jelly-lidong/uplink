package com.common.model.entity.resource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@TableName(value = "t_satellite_group", autoResultMap = true)
@Schema(description = "卫星信息")
public class SatelliteGroup {

  @TableId(type = IdType.AUTO)
  @Schema(description = "卫星ID")
  private Long id;

  @Schema(description = "卫星组名称")
  private String groupName;

  @Schema(description = "卫星组描述")
  private String groupDesc;

  @Schema(description = "卫星组状态")
  private Integer groupStatus;

  @Schema(description = "卫星组创建时间")
  private String createTime;

  @Schema(description = "卫星组更新时间")
  private String updateTime;

  @TableField(exist = false)
  @Schema(description = "卫星列表")
  private List<Satellite> satellites;
}
