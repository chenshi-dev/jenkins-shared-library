import com.chenshi.jenkinslibs.WechatWorkNotifier

def call(body) {
    new WechatWorkNotifier(script: this).sendMessage()
    return this
}