package sidly.wynnadhoc.utils

object Regexs {
    val TIMER_REGEX = Regex("§7(?<timeLeft>\\d+)(?<unit>[dhms])")
    val PROF_LVL_REQ_REGEX = Regex("""^(?<type>Mining|Farming|Woodcutting|Fishing) Lv Min: §f(?<lvl>\d++)$""")
    val USERNAME_REGEX = Regex("""(?<username>[A-Za-z0-9_]{1,16})""")
    val SERVER_REGEX = Regex("""\[(?<region>NA|EU|AS)(?<world>\d++)]""")
    val COLORED_USERNAME_REGEX = Regex("""§(?<color>.)(?<username>[A-Za-z0-9_]{1,16})""")

    // tab list
    val TAB_GLOBAL_HEADER_REGEX = Regex("""§f {2}§lGlobal ${SERVER_REGEX.pattern}""")
    val TAB_LINE_PLAYER_REGEX =
        Regex("""(?:§(?<serverColor>[23e])${SERVER_REGEX.pattern} §(?<nameColor>[abce7]))?${USERNAME_REGEX.pattern}""")

    // scoreboard
    val SCOREBOARD_PARTY_HEADER = Regex("""§e§lParty:§6 \[Lv. (?<partyLvl>\d++)]""")
    val SCOREBOARD_PARTY_MEMBER = Regex("""- \[\|\|(?<health>\d++)\|\|] ${USERNAME_REGEX.pattern} \[(?<lvl>\d++)]""")
}
