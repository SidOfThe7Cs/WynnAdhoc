package sidly.wynnadhoc.features.mounts

enum class SecondaryColor : MountColor {
    UNKNOWN,
    ANY,

    // adasaurs
    Blood,
    Rose,
    Tawny,
    Moss,
    Royal,
    Misty,
    Maroon,
    Raven,
    Sage,

    // wyverns
    Bleach,
    Cinder,
    Horn,
    Kander,
    Onyx,
    Quartz,
    Sapphire, //Rose,
    Shell,
    Ivory,
    Tusk,

    // horses
    Sable,
    Rich,
    Reddish,
    Dawn,
    Dusk,
    Fawn,
    Night,
    Ash,
    Argent,
    Pale;

    companion object {
        fun fromString(s: String): SecondaryColor {
            entries.forEach { if (it.name == s) return it }
            return UNKNOWN
        }
    }
}