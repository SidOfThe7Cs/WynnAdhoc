package sidly.wynnadhoc.features.mounts

import sidly.wynnadhoc.utils.text.ItemTextParser

class MountCore() {
    var primaryColor: PrimaryColor = PrimaryColor.UNKNOWN
    var secondaryColor: SecondaryColor = SecondaryColor.UNKNOWN

    constructor(primaryColor: PrimaryColor, secondaryColor: SecondaryColor) : this() {
        this.primaryColor = primaryColor
        this.secondaryColor = secondaryColor
    }

    fun colorMatch(other: MountCore): Boolean {
        if (primaryColor == PrimaryColor.UNKNOWN || secondaryColor == SecondaryColor.UNKNOWN) return false

        var primaryMatch = false
        var secondaryMatch = false

        if (this.primaryColor == other.primaryColor || this.primaryColor == PrimaryColor.ANY || other.primaryColor == PrimaryColor.ANY) {
            primaryMatch = true
        }

        if (this.secondaryColor == other.secondaryColor || this.secondaryColor == SecondaryColor.ANY || other.secondaryColor == SecondaryColor.ANY) {
            secondaryMatch = true
        }

        return primaryMatch && secondaryMatch
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        if (other !is MountCore) return false

        if (primaryColor != other.primaryColor) return false
        if (secondaryColor != other.secondaryColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = primaryColor.hashCode()
        result = 31 * result + secondaryColor.hashCode()
        return result
    }


    companion object {
        fun fromParser(parser: ItemTextParser): MountCore? {
            val findIndex = parser.findIndex({ part -> part.isMount() })
            if (findIndex == -1) return null
            val mount = MountCore()
            val colorLine = parser.find({ part ->
                mount.primaryColor = PrimaryColor.UNKNOWN
                mount.secondaryColor = SecondaryColor.UNKNOWN
                val split = part.string.trim().split("-")
                if (split.size != 2) return@find false
                mount.primaryColor = PrimaryColor.fromString(split[0])
                if (mount.primaryColor == PrimaryColor.UNKNOWN) return@find false
                mount.secondaryColor = SecondaryColor.fromString(split[1])
                return@find mount.secondaryColor != SecondaryColor.UNKNOWN
            }, findIndex)
            return if (colorLine == null) null else mount
        }
    }
}