package org.aircas.resource.controller;

import lombok.RequiredArgsConstructor;
import org.aircas.resource.feign.HelloWorld;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@CrossOrigin
@RestController("test")
public class TestController {

  private final HelloWorld helloWorld;

  @GetMapping("01")
  public void test() {
    helloWorld.getTest();
  }

}
