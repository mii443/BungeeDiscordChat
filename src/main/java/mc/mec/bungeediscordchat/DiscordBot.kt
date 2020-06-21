package mc.mec.bungeediscordchat

import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.md_5.bungee.api.ProxyServer
import javax.security.auth.login.LoginException


class DiscordBot : ListenerAdapter() {
    var plugin: BungeeDiscordChat? = null

    lateinit var jda: JDA
    var token:String? = null

    var guild: Guild? = null

    var guildID:Long = 0
    var chatChannelID:Long = 0
    var systemChannelID:Long = 0
    var commandlogChannelID:Long = 0

    var chatChannel: TextChannel? = null
    var systemChannel:TextChannel? = null
    var commandlogChannel: TextChannel? = null


    //      チャットチャンネル出力
    fun chat(text:String){

        if (text.indexOf("/") == 0)return

        chatChannel?.sendMessage(text)?.queue()
    }
    //      システム出力
    fun system(text:String){
        systemChannel?.sendMessage(text)?.queue()
    }
    //      コマンドログ出力
    fun cmdlog(text: String){
        commandlogChannel?.sendMessage(text)?.queue()
    }

    fun shutdown(){
        jda.shutdown()
       plugin?.logger?.info("discord shutdown")
    }

    fun setup(){
        plugin?.logger?.info("discord setup")

        if(token == null){
            plugin?.logger?.info("Discord token is not initialized.")
            return
        }
        try {

            jda = JDABuilder(AccountType.BOT).setToken(token).addEventListeners(this).build()
            jda.awaitReady()

            guild = jda.getGuildById(this.guildID)
            chatChannel = guild?.getTextChannelById(this.chatChannelID)
            systemChannel = guild?.getTextChannelById(this.systemChannelID)
            commandlogChannel = guild?.getTextChannelById(this.commandlogChannelID)

        } catch (e: LoginException) {
            e.printStackTrace()
            plugin?.logger?.info(e.localizedMessage)
            return
        }
        plugin?.logger?.info("discord setup done!")
    }

    fun checkChannel(channel:TextChannel?){
        if(channel == null){
            plugin?.logger?.info("channel null")
            return
        }
        plugin?.logger?.info("cantalk:${channel.canTalk()}")

    }

    override fun onReady(event: ReadyEvent) {
        plugin?.logger?.info("Discord bot ready")
    }

    override fun onMessageReceived(e: MessageReceivedEvent) {
        if(e.author.id == jda.selfUser.id){
            return
        }
        if(e.channelType != ChannelType.TEXT){
            return
        }
        if(e.textChannel.idLong != chatChannel?.idLong) {
            return
        }
        ProxyServer.getInstance().broadcast("${plugin?.prefix} ${e.author.name}  ${e.message.contentRaw}")
    }
}