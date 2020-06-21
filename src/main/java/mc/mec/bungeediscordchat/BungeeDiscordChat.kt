package mc.mec.bungeediscordchat

import mc.mec.bungeediscordchat.japanize.JapanizeType
import mc.mec.bungeediscordchat.japanize.Japanizer
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
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
        loadConfig()

        proxy.pluginManager.registerListener(this, this)
    }

    fun loadConfig(){
        val config = ConfigFile(this).getConfig()
        try {
            this.lunachat = config?.getBoolean("lunachat")!!

            discord.token = config.getString("Discord.Token")
            discord.guildID = config.getLong("Discord.Guild")
            discord.chatChannelID = config.getLong("Discord.ChatChannel")
            discord.commandlogChannelID = config.getLong("Discord.CommandlogChannel")
            discord.systemChannelID = config.getLong("Discord.SystemChannel")

        } catch (e: NullPointerException) {
            e.printStackTrace()
            discord.system("[Error]${e.localizedMessage}")
        }
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
        if (e.isCommand || e.isProxyCommand) {
            discord.cmdlog("[CMD-LOG] <${e.sender}> $message")
        }else {
            discord.chat(chatMessage)
        }
    }

    @EventHandler
    fun onLogin(e: PostLoginEvent){
        val player = e.player
        val name = player.name
        ProxyServer.getInstance().broadcast("§e§l[ログイン] §f§l${name}§r§fさんがサーバーに参加しました。")
        discord.chat(":bangbang: **${player}さんがログインしました**")
    }

    @EventHandler
    fun onLogout(e: PlayerDisconnectEvent) {
        val player = e.player
        val name = player.name
        ProxyServer.getInstance().broadcast("§e§l[ログアウト] §f§l${name}§r§fさんがサーバーから退出しました。")
        discord.chat(":x: **${name}さんがログアウトしました**")
    }

    @EventHandler
    fun onMove(e: ServerConnectedEvent){
        val player = e.player
        val name = player.name
        val server = e.server.info.name
        ProxyServer.getInstance().broadcast("§e§l[ムーブメント] §f§l${name}§r§fさんが${server}サーバーに移動しました。")
        discord.chat(":door: **${name}さんが${server}サーバーに移動しました。**")
    }

    fun sendMessage(uuid: UUID ,text:String){
        ProxyServer.getInstance().getPlayer(uuid).sendMessage(TextComponent(text))
    }

    private fun removeColorCode(msg: String?): String? {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', msg))
    }
}