package sidly.wynnadhoc.utils.datatypes

data class Server(val world: Int, val region: Region) {
    enum class Region {
        EU,
        AS,
        NA;

        companion object {
            fun from(string: String): Region? {
                return entries.find { it.name.equals(string, ignoreCase = true) }
            }
        }
    }

    companion object {
        fun fromMatch(match: MatchResult): Server? {
            val regionStr = match.groups["region"]?.value ?: return null
            val worldStr = match.groups["world"]?.value ?: return null
            val region = Region.from(regionStr) ?: return null
            val world = worldStr.toIntOrNull() ?: return null
            return Server(world, region)
        }
    }
}