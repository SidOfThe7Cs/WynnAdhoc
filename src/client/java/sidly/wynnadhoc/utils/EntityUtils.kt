package sidly.wynnadhoc.utils

import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.util.math.Box

fun Entity.getVehicleHitbox(): Box {
    return vehicle?.boundingBox ?: Box(entityPos.add(-0.25, 0.0, 0.25), entityPos.add(0.25, -2.0, -0.25))
}

fun DisplayEntity.TextDisplayEntity.isRareMob(): Boolean {
    return text?.string?.contains("\uE02A") ?: false
}