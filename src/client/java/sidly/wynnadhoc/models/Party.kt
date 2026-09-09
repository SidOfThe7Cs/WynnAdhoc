package sidly.wynnadhoc.models

import sidly.wynnadhoc.WynnAdhocClient

object Party {
    fun getPartyMembers(): List<String> {
        var leaderFromTab: String? = null
        val membersFromTab = mutableListOf<String>()
        TabList.EntryType.PARTY.getData()?.forEach {
            if (it.partyLeader == true) {
                if (leaderFromTab != null) WynnAdhocClient.LOGGER.warn("found leader ${it.name} but leader is already $leaderFromTab")
                else leaderFromTab = it.name
            } else {
                membersFromTab.add(it.name)
            }
        }

        val fromTab = mutableListOf<String>()

        if (leaderFromTab == null) {
            if (membersFromTab.isNotEmpty()) WynnAdhocClient.LOGGER.warn("no leader found in tab list party")
        } else {
            fromTab.add(leaderFromTab)
            fromTab.addAll(membersFromTab)
        }

        val fromScoreboard = Scoreboard.getPartyMembers().map { it.name }.toList()

        return if (fromTab.isEmpty()) fromScoreboard else fromTab
    }
}