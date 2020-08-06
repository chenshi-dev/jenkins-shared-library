package com.chenshi.jenkinslibs

import groovy.json.JsonOutput


class WechatWorkBot {
    static final WECHATWORKBOTAPI = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key="
    String webhookKey

    void doSend(String message) {
        // refer https://work.weixin.qq.com/help?doc_id=13376
        def data = [
            msgtype: "markdown",
            markdown: [
                "content": message
            ]
        ]
        def body = JsonOutput.toJson(data)

        def api = "${WECHATWORKBOTAPI}${webhookKey}"
        def conn = new URL(api).openConnection()

        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setDoOutput(true)
        conn.getOutputStream().write(body.getBytes("UTF-8"))
        conn.getResponseCode()
    }
}