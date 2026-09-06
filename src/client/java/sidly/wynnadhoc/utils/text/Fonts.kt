package sidly.wynnadhoc.utils.text

object Fonts {
    val fonts = mutableMapOf<String, Font<*>>()

    init {
        fonts["nameplate/progress"] = create(
            "\ue000\ue001\ue002\ue003\ue004\ue005\ue006\ue007\ue008\ue009" +
                    "\ue010\ue011\ue012\ue013\ue014\ue015\ue016\ue017\u0000\u0000" +
                    "\ue020\ue021\ue022\ue023\ue024\ue025\ue026\ue027\ue028\ue029" +
                    "\ue030\ue031\ue032\ue033\ue034\ue035\ue036\ue037\u0000\u0000",
            arrayOf(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, // active 1-9
                -2, -2, -2, -2, -2, -1, -1, -1, -2, -2, // active symbols and such
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, // inactive 1-9
                -2, -2, -2, -2, -2, -1, -1, -1, -2, -2, // inactive symbols and such
            )
        )
    }

    /**
     * Type‑safe retrieval. Use as:
     *     val font: Font<Int>? = Fonts.get<Int>("<fontPath>")
     */
    inline fun <reified T> get(fontPath: String): Font<T>? {
        return fonts[fontPath] as? Font<T>
    }

    // Create from a CharArray and an Array of values
    fun <T> create(chars: CharArray, values: Array<T>): Font<T> {
        require(chars.size == values.size) {
            "Character count (${chars.size}) must match value count (${values.size})"
        }
        val map = chars.indices.associate { chars[it] to values[it] }
        return Font(map)
    }

    // Convenience overload: accepts a String (e.g., "\ue000\ue001...")
    fun <T> create(chars: String, values: Array<T>): Font<T> {
        return create(chars.toCharArray(), values)
    }

    class Font<T>(private val map: Map<Char, T>) {
        fun get(char: Char): T? = map[char]
    }
}