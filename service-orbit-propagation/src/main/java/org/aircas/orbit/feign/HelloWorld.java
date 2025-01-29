package org.aircas.orbit.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 库存服务接口 name:指定调用rest接口所对应的服务名 path:指定调用rest接口所在的StockController指定的@RequestMapping
 */
@Service
@FeignClient(name = "nacos-provider", path = "helloWorld")
public interface HelloWorld {

  @GetMapping("test")
  void getTest();
}
