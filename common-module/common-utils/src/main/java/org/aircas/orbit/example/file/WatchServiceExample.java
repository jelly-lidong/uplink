package org.aircas.orbit.example.file;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class WatchServiceExample {

  public static void main(String[] args) {
    try {
      // 获取文件系统的 WatchService
      WatchService watchService = FileSystems.getDefault().newWatchService();

      // 监听的文件夹路径
      Path path = Paths.get("path/to/directory");

      // 为文件夹注册监听事件
      path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

      System.out.println("监听文件夹: " + path);

      // 无限循环监听文件变化
      while (true) {
        // 获取下一个文件变化事件
        WatchKey key = watchService.take();

        // 遍历所有变化事件
        for (WatchEvent<?> event : key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          // 获取文件名称
          Path fileName = (Path) event.context();

          System.out.println("事件类型: " + kind + ". 文件名称: " + fileName);
        }

        // 重置 WatchKey
        boolean valid = key.reset();
        if (!valid) {
          break;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}