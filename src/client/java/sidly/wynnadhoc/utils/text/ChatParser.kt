package sidly.wynnadhoc.utils.text

import net.minecraft.text.Text

class ChatParser : TextParser {
    constructor(text: Text) : super(text)

    val usernameRegex = Regex("""^(?<username>[A-Za-z0-9_]{1,16}):?$""")

    fun getPM(): PMData? {
        if (this.get(0)?.matches { part ->
                (part.string == "\uDAFF\uDFFC\uE007\uDAFF\uDFFF\uE002\uDAFF\uDFFE" || part.string == "\uDAFF\uDFFC\uE001\uDB00\uDC06") &&
                        part.getColorRgb() == 0xDDCC99 &&
                        part.getFontPath()?.equals("chat/prefix") == true
            } != true) return null

        var senderName: String? = null
        val senderIndex = this.findIndex({ part ->
            val matchResult = usernameRegex.matchEntire(part.string)
            senderName = matchResult?.groups["username"]?.value
            matchResult != null
        }, 2)

        if (this.get(senderIndex + 2)?.matches { part ->
                part.string == "\uE003" &&
                        part.getColorRgb() == 0xDDCC99 &&
                        part.getFontPath()?.equals("chat/prefix") == true
            } != true) return null


        var receiverName: String? = null
        val receiverIndex = this.findIndex({ part ->
            val matchResult = usernameRegex.matchEntire(part.string)
            receiverName = matchResult?.groups["username"]?.value
            matchResult != null
        }, senderIndex + 4)

        return PMData.from(senderName, receiverName, this.subText(receiverIndex + 2))
    }

    class PMData(val sender: String, val receiver: String, val message: MutableList<TextPart>) {
        companion object {
            fun from(sender: String?, receiver: String?, message: MutableList<TextPart>?): PMData? {
                sender ?: return null
                receiver ?: return null
                message ?: return null
                return PMData(sender, receiver, message)
            }
        }

        fun getMessageString(): String {
            return message.getString()
        }

        fun getMessageStringNoSpaces(): String {
            return message.getStringNoSpaces()
        }

        override fun toString(): String {
            return "PMData(sender='$sender', receiver='$receiver', message=$message)"
        }
    }
}