# Jenkins pipeline shared library

## 使用

1. `wechatWorkNotify()` 推送构建结果到 Jenkins 全局环境变量 `QY_WECHAT_BOT_WEBHOOK_KEY` 指定的企业微信机器人，示例：

```groovy
    post {
        always {
            wechatWorkNotify()
        }
```

## 参考

1. [Jenkins shared-libraries 文档](https://www.jenkins.io/doc/book/pipeline/shared-libraries/)
