package mc.mec.bungeediscordchat.commands.PrivateMessage

import mc.mec.bungeediscordchat.BungeeDiscordChat
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object TellCommand : Command("btell") {
    override fun execute(sender: CommandSender?, args: Array<out String>?) {
        if (sender == null) { return }
        if (args?.size!! < 1) {
            sendMessage(sender, ChatColor.RED.toString() + "使用方法 : /${this.name} <player> <message>")

            return
        }

        if (args[0].equals(sender.name)) {
            sendMessage(sender, ChatColor.RED.toString() + "自分自身にメッセージを送ることはできません。")
        }

        val parent = BungeeDiscordChat.instance
        val receiver = parent.proxy.getPlayer(args[0])
        if (receiver == null) {
            sendMessage(sender, ChatColor.RED.toString() + "送信先のプレイヤーが見つかりません。")
            return
        }

        val str = StringBuilder()
        for (i in 1..(args.size)) {
            str.append(args[i] + " ")
        }

        val message = str.toString().trim()
        sendMessage(receiver, "<${sender.name}> " + message)
    }

    /**
     * 指定した対象にメッセージを送信する
     * @param receiver 送信先
     * @param message メッセージ
     */
    fun sendMessage(receiver: CommandSender, message: String?) {
        if (message == null) return
        receiver.sendMessage(message)
    }
}