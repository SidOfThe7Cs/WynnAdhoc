package sidly.wynnadhoc.utils

object Regexs {
    val TIMER_REGEX = Regex("§7(?<timeLeft>\\d+)(?<unit>[dhms])")
    val PROF_LVL_REQ_REGEX = Regex("""^(?<type>Mining|Farming|Woodcutting|Fishing) Lv Min: §f(?<lvl>\d++)$""")
}
