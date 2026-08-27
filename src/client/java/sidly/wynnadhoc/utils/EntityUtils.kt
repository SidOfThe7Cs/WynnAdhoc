package sidly.wynnadhoc.utils

import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
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