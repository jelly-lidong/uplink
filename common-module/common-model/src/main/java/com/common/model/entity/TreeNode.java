package com.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class TreeNode<T extends TreeNode<?>> {
    @Schema(description = "父ID")
    private Long      parentId;
    @TableField(exist = false)
    @Schema(description = "子列表")
    private List<T> children;
}
