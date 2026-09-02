package sidly.wynnadhoc.utils

import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.mixin.client.accessors.EntityAccessor
import sidly.wynnadhoc.models.CameraModel
import sidly.wynnadhoc.utils.datatypes.hasLOS

fun Entity.getVehicleHitboxFallback(): Box {
    return this.vehicle?.getBoundingBoxFallback() ?: this.getBoundingBoxFallback()
}

fun Entity.getBoundingBoxFallback(): Box {
    var box = this.boundingBox
    if (box.isNaN || box.getAverageSideLength() == 0.0) {
        box = Box(this.entityPos.add(-0.3, 0.0, 0.3), this.entityPos.add(0.3, -2.0, -0.3))
    }
    return box
}

fun Entity.playerCanSee(): Boolean {
    return this.inCameraFrustum() && this.getBoundingBoxFallback().hasLOS()
}

fun Entity.inCameraFrustum(): Boolean {
    val frustum = CameraModel.lastFrustum ?: return false
    return frustum.isVisible(this.getBoundingBoxFallback())
}

fun Entity.debugRiders() {
    val log = WynnAdhocClient.LOGGER

    log.info(Debug.Type.MANUAL, "=== RIDERS DEBUG for ${entityInfo(this)} ===")

    // Print vehicle chain (upward)
    var current: Entity? = this
    var depth = 0
    while (current?.vehicle != null) {
        val vehicle = current.vehicle!!
        log.info(Debug.Type.MANUAL, "  ".repeat(depth) + "↑ vehicle: ${entityInfo(vehicle)}")
        current = vehicle
        depth++
    }

    // Print passengers (downward)
    printPassengers(this, depth = 0, maxDepth = 5)

    log.info(Debug.Type.MANUAL, "=== END RIDERS DEBUG ===")
}

private fun printPassengers(entity: Entity, depth: Int, maxDepth: Int) {
    if (depth > maxDepth) {
        WynnAdhocClient.LOGGER.info(Debug.Type.MANUAL, "  ".repeat(depth) + "... (too deep)")
        return
    }
    val passengers = entity.passengerList
    if (passengers.isEmpty()) return
    for (passenger in passengers) {
        val info = entityInfo(passenger)
        WynnAdhocClient.LOGGER.info(Debug.Type.MANUAL, "  ".repeat(depth) + "↓ passenger: $info")
        // Recurse for nested passengers
        printPassengers(passenger, depth + 1, maxDepth)
    }
}

private fun entityInfo(entity: Entity): String {
    val name = entity.name?.string ?: "<unnamed>"
    val type = entity.type?.name ?: entity::class.simpleName
    val pos = "(${entity.x}, ${entity.y}, ${entity.z})"
    return "$name ($type) @ $pos"
}

fun Entity.setGlowing() {
    val tracker = this.dataTracker
    val current = tracker.get(EntityAccessor.getFlags()) // Byte
    val newFlags = (current.toInt() or (1 shl 6)).toByte() // set bit 6 (value 64)
    tracker.set(EntityAccessor.getFlags(), newFlags)
}