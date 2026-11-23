package sidly.wynnadhoc.features.mounts

enum class PrimaryColor : MountColor {
    UNKNOWN,
    ANY,

    // adasaurs
    Crimson,
    Dust,
    Amber,
    Emerald,
    Cobalt,
    Dusk,
    Plum,
    Sable,
    Ash,
    Albino,

    // wyverns
    Azure,
    Cerulean,
    Bronze,
    Ebony,
    Fledge,
    Golden,
    Hollow,
    Infernal,
    Jade,
    Mystic,

    // horses
    Cherry,
    Bay,
    Chestnut,
    Gold,
    Tan,
    Beige,
    Black,
    Gray,
    Silver,
    White;

    companion object {
        fun fromString(s: String): PrimaryColor {
            entries.forEach { if (it.name == s) return it }
            return UNKNOWN
        }
    }
}