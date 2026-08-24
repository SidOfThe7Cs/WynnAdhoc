package sidly.wynnadhoc.utils

import net.minecraft.nbt.NbtString
import net.minecraft.text.ClickEvent
import net.minecraft.util.Identifier
import sidly.wynnadhoc.features.BombShare
import java.util.*
import java.util.function.Supplier

enum class ClickEventFunction {
    BOMB_SHARE_PROF,
    BOMB_SHARE_XP,
    BOMB_SHARE_LOOT,
    BOMB_SHARE_CHEST;

    private var runnableSupplier: Supplier<Runnable> =
        Supplier { Runnable { ChatMessageUtils.sendChatMessage("Looks like this clickEvent was not properly initialized") } }
    private var cachedRunnable: Runnable? = null

    fun onClick() {
        if (cachedRunnable == null) {
            cachedRunnable = runnableSupplier.get()
        }
        cachedRunnable!!.run()
    }

    val clickEvent: ClickEvent
        get() = ClickEvent.Custom(
            RUN_FUNCTION_ID,
            Optional.ofNullable(NbtString.of(name))
        )

    companion object {
        val RUN_FUNCTION_ID: Identifier = Identifier.ofVanilla("chat_click_run_function")

        init {
            BOMB_SHARE_PROF.runnableSupplier = Supplier { BombShare.Bomb.PROF.getChatCommand() }
            BOMB_SHARE_XP.runnableSupplier = Supplier { BombShare.Bomb.XP.getChatCommand() }
            BOMB_SHARE_LOOT.runnableSupplier = Supplier { BombShare.Bomb.LOOT.getChatCommand() }
            BOMB_SHARE_CHEST.runnableSupplier = Supplier { BombShare.Bomb.CHEST_LOOT.getChatCommand() }
        }
    }
}
