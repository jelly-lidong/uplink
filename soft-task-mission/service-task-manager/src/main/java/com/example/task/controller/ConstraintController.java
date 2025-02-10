package com.example.task.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.model.entity.task.Constraint;
import com.common.model.enums.ConstraintType;
import com.common.model.response.Result;
import com.example.task.service.ConstraintService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "约束管理接口")
@CrossOrigin
@RestController
@RequestMapping("/task/constraints")
@RequiredArgsConstructor
public class ConstraintController {

  private final ConstraintService constraintService;

  @ApiOperation("约束类型列表")
  @GetMapping("/types")
  public Result<ConstraintType[]> types() {
    return Result.success(ConstraintType.values());
  }


  @ApiOperation("创建约束")
  @PostMapping
  public Result<Constraint> create(@RequestBody Constraint constraint) {
    return Result.success(constraintService.create(constraint));
  }

  @ApiOperation("更新约束")
  @PutMapping("/{id}")
  public Result<Constraint> update(@PathVariable Long id, @RequestBody Constraint constraint) {
    constraint.setId(id);
    return Result.success(constraintService.update(constraint));
  }

  @ApiOperation("删除约束")
  @DeleteMapping("/{id}")
  public Result<Void> delete(@PathVariable Long id) {
    constraintService.delete(id);
    return Result.success();
  }

  @ApiOperation("获取约束详情")
  @GetMapping("/{id}")
  public Result<Constraint> getById(@PathVariable Long id) {
    return Result.success(constraintService.getById(id));
  }

  @ApiOperation("分页查询约束列表")
  @GetMapping
  public Result<IPage<Constraint>> page(
      @RequestParam(defaultValue = "1") Integer current,
      @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String constraintType) {
    return Result.success(constraintService.page(new Page<>(current, size), keyword, constraintType));
  }

  @ApiOperation("根据约束类型查询")
  @GetMapping("/by-type")
  public Result<List<Constraint>> getByConstraintType(@RequestParam String constraintType) {
    return Result.success(constraintService.getByConstraintType(constraintType));
  }
} 