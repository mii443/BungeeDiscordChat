package mc.mec.bungeediscordchat

import mc.mec.bungeediscordchat.commands.ReplyCommand
import mc.mec.bungeediscordchat.commands.TellCommand
import mc.mec.bungeediscordchat.japanize.JapanizeType
import mc.mec.bungeediscordchat.japanize.Japanizer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.text.SimpleDateFormat
import java.util.*


class BungeeDiscordChat : Plugin(), Listener{
    companion object{
        private const val prefix = "§b§l[BungeeDiscordChat]§r"
    }

    var dic = HashMap<String?, String?> ()
    var lunachat:Boolean = false
    var discord = DiscordBot()

    override fun onEnable() {
        /**
        for (command in arrayOf(
                "tell", "msg", "message", "m", "w", "t")) {
            proxy.pluginManager.registerCommand(
                    this, TellCommand(this, command))
        }
        for (command in arrayOf("reply", "r")) {
            proxy.pluginManager.registerCommand(
                    this, ReplyCommand(this, command))
        }
        **/

        proxy.pluginManager.registerListener(this, this)
    }

    override fun onDisable() {
    }

    @EventHandler
    fun onChat(e: ChatEvent) {
        val p = e.sender
        if (p !is ProxiedPlayer) return
        var message = removeColorCode(e.message)
        if (lunachat) {
            val jmsg = Japanizer.japanize(message, JapanizeType.GOOGLE_IME, dic)
            if (jmsg != "") message += "($jmsg)"
        }
        val chatMessage = "${e.sender}@${p.server.info.name}>${message}"
        for (player in ProxyServer.getInstance().players) {
            if (player.server.info.name != p.server.info.name) {
                sendMessage(player.uniqueId, chatMessage)
            }
        }
        if (!e.isCommand || !e.isProxyCommand) {
            discord.chat(chatMessage)
        }
    }

    fun sendMessage(uuid: UUID ,text:String){
        ProxyServer.getInstance().getPlayer(uuid).sendMessage(TextComponent(text))
    }

    private fun removeColorCode(msg: String?): String? {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg))
    }


}