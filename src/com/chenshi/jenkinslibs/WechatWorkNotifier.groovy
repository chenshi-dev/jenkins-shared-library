package com.chenshi.jenkinslibs

import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

import com.chenshi.jenkinslibs.WechatWorkBot


class WechatWorkNotifier {

    // https://work.weixin.qq.com/help?doc_id=13376
    final static wechatMarkdownMessageMaxBytesLength = 4096
    CpsScript script

    private String buildChangeLogs(RunWrapper currentBuild, int availableBytesLength) {
        def changeLogs = ""
        def changeLogSets = currentBuild.changeSets

        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            def repoBrowser = changeLogSets[i].browser

            for (int j = 0; j < entries.length; j++) {
                def entry = entries[j]
                def commitUrl = repoBrowser.getChangeSetLink(entry)
                def log = "> ${entry.msg} (commit [${entry.commitId[0..7]}](${commitUrl})) by ${entry.author}\n"
                availableBytesLength -= log.getBytes('UTF-8').size()
                if (availableBytesLength < 0) {
                    return changeLogs.toString()
                }
                changeLogs <<= log
            }
        }

        return changeLogs.toString()
    }

    private String buildMessage(RunWrapper currentBuild) {
        def color
        if (currentBuild.currentResult == "SUCCESS") {
            color = "info"
        } else {
            color = "warning"
        }

        def startTime = new Date(currentBuild.startTimeInMillis).toString()

        def mainMsg = """部署结果：<font color=\"${color}\">${currentBuild.currentResult} ${currentBuild.projectName}:${currentBuild.number}</font>
部署日志：[${currentBuild.absoluteUrl}](${currentBuild.absoluteUrl})
开始时间：${startTime}
花费时间：${currentBuild.durationString.replace(' and counting', '')}
触发原因：${currentBuild.getBuildCauses()[0].shortDescription}
"""
        int availableBytesLength = wechatMarkdownMessageMaxBytesLength - mainMsg.getBytes('UTF-8').size()

        if (availableBytesLength < 50) {
            return mainMsg
        }

        def changeLogs = buildChangeLogs(currentBuild, availableBytesLength)
        if (changeLogs.size() == 0 ) {
            return mainMsg
        }

        mainMsg <<= "更新概要:\n"
        mainMsg <<= changeLogs
        return mainMsg.toString()
    }


    void sendMessage() {
        def bot = new WechatWorkBot(webhookKey: script.env.QY_WECHAT_BOT_WEBHOOK_KEY)
        def message = buildMessage(script.currentBuild)
        bot.doSend(message)
    }
}