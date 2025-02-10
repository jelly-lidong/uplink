package com.common.model.response;

import lombok.Getter;

/**
 * 统一状态码
 */

@Getter
public enum ResultCode {
  SUCCESS(200, "操作成功"),
  FAILED(500, "操作失败"),
  VALIDATE_FAILED(400, "参数检验失败"),
  UNAUTHORIZED(401, "暂未登录或token已过期"),
  FORBIDDEN(403, "没有相关权限"),
  NOT_FOUND(404, "资源不存在"),

  // 业务错误码 1001-1999
  TASK_NOT_FOUND(1001, "任务不存在"),
  TASK_ALREADY_EXISTS(1002, "任务已存在"),
  TASK_STATUS_ERROR(1003, "任务状态错误"),
  TASK_DEPENDENCY_ERROR(1004, "任务依赖关系错误"),
  TASK_CIRCULAR_DEPENDENCY(1005, "存在循环依赖"),

  // 用户错误码 2001-2999
  USER_NOT_FOUND(2001, "用户不存在"),
  USER_ALREADY_EXISTS(2002, "用户已存在"),
  USER_PASSWORD_ERROR(2003, "密码错误"),

  // 权限错误码 3001-3999
  PERMISSION_DENIED(3001, "权限不足"),
  TOKEN_INVALID(3002, "无效的token"),
  TOKEN_EXPIRED(3003, "token已过期"),

  // 系统错误码 9001-9999
  SYSTEM_ERROR(9001, "系统错误"),
  NETWORK_ERROR(9002, "网络错误"),
  DATABASE_ERROR(9003, "数据库错误");

  private final int    code;
  private final String message;

  ResultCode(int code, String message) {
    this.code    = code;
    this.message = message;
  }

}