package com.example.banklite;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class BankLiteController {
  @GetMapping("/health")
  public Map<String,String> health() { return Map.of("status","UP"); }
}
