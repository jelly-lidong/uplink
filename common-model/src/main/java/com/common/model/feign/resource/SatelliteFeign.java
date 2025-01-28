package com.common.model.feign.resource;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.common.model.entity.resource.Satellite;
import com.common.model.response.Result;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "satellite-service")
public interface SatelliteFeign {

  @ApiOperation("分页查询卫星列表")
  @GetMapping
  Result<IPage<Satellite>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
      @RequestParam(required = false) String keyword, @RequestParam(required = false) String status, @RequestParam(required = false) List<String> tags);

}
