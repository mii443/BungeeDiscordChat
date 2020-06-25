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
import java.text.SimpleDateFormat
import java.util.*


class BungeeDiscordChat : Plugin(), Listener{

    val prefix = "§b§l[BDiscord]§r"
    var dic = HashMap<String?, String?> ()
    var lunachat:Boolean = false
    var discord = DiscordBot()

    override fun onEnable() {
        loadConfig()
        logger.info("Config Loaded")
        proxy.pluginManager.registerListener(this, this)
        discord.system(":ballot_box_with_check: Bot起動")
        discord.chat(":ballot_box_with_check: **サーバーが起動しました**")
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

            discord.plugin = this
            discord.setup()
        } catch (e: NullPointerException) {
            e.printStackTrace()
            logger.info("[Error]${e.localizedMessage}")
        }
    }

    override fun onDisable() {
        discord.chat(":no_entry:  **サーバーが停止しました**")
        discord.system(":no_entry:  Bot停止")
        discord.shutdown()
    }

    @EventHandler
    fun onChat(e: ChatEvent) {
        val p = e.sender
        val now = Date()
        val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        if (p !is ProxiedPlayer) return
        var message = removeColorCode(e.message)
        if (lunachat) {
            val jmsg = Japanizer.japanize(message, JapanizeType.GOOGLE_IME, dic)
            if (jmsg != "") message += "($jmsg)"
        }
        val chatMessage = "§d<${e.sender}@${p.server.info.name}> $message"
        for (player in ProxyServer.getInstance().players) {
            if (player.server.info.name != p.server.info.name) {
                sendMessage(player.uniqueId, chatMessage)
            }
        }
        if (e.isCommand || e.isProxyCommand) {
            discord.cmdlog("[CMD-LOG] <${e.sender}@${p.server.info.name}> ${e.message} (${format.format(now)})")
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