package sidly.wynnadhoc.features.outervoid

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wynntils.models.gear.type.GearTier
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.ItemEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.catagories.OuterVoidConfig
import sidly.wynnadhoc.event.ClientTickEvent
import sidly.wynnadhoc.event.InitEvent
import sidly.wynnadhoc.event.WorldRenderEvent
import sidly.wynnadhoc.utils.LocationUtils
import sidly.wynnadhoc.utils.datatypes.getCorners
import sidly.wynnadhoc.utils.datatypes.getLast
import sidly.wynnadhoc.utils.datatypes.getTopCorners
import sidly.wynnadhoc.utils.datatypes.toBox
import sidly.wynnadhoc.utils.datatypes.toVec3d
import sidly.wynnadhoc.utils.render.drawBox
import sidly.wynnadhoc.utils.render.drawLine
import sidly.wynnadhoc.utils.render.drawLineToEye
import java.awt.Color
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate
import kotlin.math.sqrt

object OuterVoidItemPathfinder {
    private val config: OuterVoidConfig get() = ConfigManager.INSTANCE.config.outerVoid

    private var ALL_ISLANDS_NODES: MutableMap<Int, IslandNode> = HashMap<Int, IslandNode>()
    private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    private val FILE_PATH: Path = Paths.get("config", "sidly/islandNodes.json")

    val seenItems: MutableList<ItemEntity> = ArrayList<ItemEntity>()
    private val unknownItems: MutableSet<ItemEntity?> = HashSet<ItemEntity?>()

    const val PARABOLA_RADIUS: Float = 45f // 45
    const val PARABOLA_HEIGHT_ABOVE: Float = 11f // 11
    const val PARABOLA_HEIGHT_BELOW: Float = 40f // 40
    const val PARABOLA_RADIUS_DJ: Float = 75f
    const val PARABOLA_HEIGHT_ABOVE_DJ: Float = 22f
    const val PARABOLA_HEIGHT_BELOW_DJ: Float = 40f

    private var currentItem: ItemEntity? =
        null // used to ban the item from the first path from being found in the second path (not ideal solution)
    private var itemsRemoved = false // checks if any items were removed this frame

    private var path1: PathToItem? = null
    private var path2: PathToItem? = null

    private var recalcAllPaths = false
    private var wasInOuterVoid = false

    fun draw(event: WorldRenderEvent) {
        val client = MinecraftClient.getInstance() ?: return
        val player = client.player ?: return

        // check settings
        if (!config.mainToggle) return
        if (!LocationUtils.isInOuterVoid(player.x, player.z)) return

        updatePath()

        // update the paths so you "follow" them
        val playerPos = player.entityPos
        this.path1?.let {
            if (path2 != null) {
                drawPath(event, it.item.entityPos, path2, Color.BLUE)
            }

            //path1
            var closestIndex = -1
            var closestDistance = Double.MAX_VALUE

            for (i in it.nodes.indices) {
                val block = it.nodes[i]
                val distance = Vec3d.of(block).distanceTo(playerPos)

                if (distance < closestDistance) {
                    closestDistance = distance
                    closestIndex = i
                }
            }

            // checks if the player is closer to current+1 than current is
            if (closestIndex >= 0 && closestIndex < it.nodes.size - 1) {
                val currentPlusOne = Vec3d.of(it.nodes[closestIndex + 1])
                val dist1 = currentPlusOne.distanceTo(playerPos) // distance from player to next node
                val dist2 =
                    currentPlusOne.distanceTo(Vec3d.of(it.nodes[closestIndex])) // distance from closest node to next node
                if (dist1 < dist2) {
                    closestIndex += 1
                }
            } else if (it.nodes.size == 1) { // check if the player is closer to the item than the last node is
                val currentPlusOne = it.item.entityPos
                val dist1 = currentPlusOne.distanceTo(playerPos) // distance from player to item
                val dist2 =
                    currentPlusOne.distanceTo(Vec3d.of(it.nodes[closestIndex])) // distance from closest node to item
                if (dist1 < dist2) {
                    closestIndex += 1
                }
            }

            // Remove all nodes *before* the closest one
            if (closestIndex > 0) {
                it.nodes.subList(0, closestIndex).clear()
            }

            // draw first path last so its on top
            drawPath(event, playerPos, it, Color.white)
        }

        // boxes and lines for non-pathfinding settings
        for (item in seenItems) {
            //draw item boxes
            val rarity = OuterVoidItemDatabase.getRarity(item)
            val color = OuterVoidItemDatabase.getColor(rarity)
            if (rarity.ordinal >= config.showBoxesAtRarity.ordinal) {
                event.drawBox(item.boundingBox, color, xray = true)
            }

            //draw item lines
            if (rarity.ordinal >= config.showLinesAtRarity.ordinal) {
                event.drawLineToEye(item.entityPos, color, true)
            }
        }
    }

    fun updatePath() {
        val client = MinecraftClient.getInstance() ?: return
        val player = client.player ?: return

        if (recalcAllPaths) {
            currentItem = null
            path1 = getNextPath(player.entityPos)
            path1?.let { path2 = getNextPath(it.item.entityPos) }
            recalcAllPaths = false
            return
        }

        val p1Copy = path1
        val p2Copy = path2
        if (p1Copy == null) { // no paths generate then both
            path1 = getNextPath(player.entityPos)
            path1?.let { path2 = getNextPath(it.item.entityPos) }
        } else if (p2Copy == null || !seenItems.contains(p2Copy.item) || p1Copy.item == p2Copy.item) { // path1 but not path2 try to find path2 again
            path2 = getNextPath(p1Copy.item.entityPos)
        } else if (!seenItems.contains(p1Copy.item)) { // path1 and path2 but path1 is completed
            val path3 = getNextPath(p2Copy.item.entityPos)
            path1 = path2
            path2 = path3
        }
        // if path1 item has been picked up but there is no path2
    }

    fun onClientTick(event: ClientTickEvent) {
        // check settings
        if (!config.mainToggle) return

        // check nulls
        val client = event.client ?: return
        val player = client.player ?: return
        val world = client.world ?: return

        //check location
        if (!LocationUtils.isInOuterVoid(player.x, player.z)) {
            wasInOuterVoid = false
            return
        } else if (!wasInOuterVoid) { // we just entered the outer void
            if (ALL_ISLANDS_NODES.isEmpty()) cacheIslandData() // TODO make sure far corner is in range if we just entered
            recalcAllPaths = true
            wasInOuterVoid = true
        }

        // fell into void get new paths
        if (player.y < 90) recalcAllPaths = true

        // grab items from server
        val range = 64.0
        val box = player.boundingBox.expand(range)
        val nearbyItems = world.getEntitiesByClass(
            ItemEntity::class.java,
            box,
            Predicate { _: ItemEntity? -> true })

        for (itemEntity in nearbyItems) {
            if (itemEntity == null) continue
            // add new items
            if (!seenItems.contains(itemEntity)) {
                seenItems.add(itemEntity)

                if (itemEntity.stack.name.string == "Snow") {
                    println("Mythic thingy finally found at please add it to the database: " + itemEntity.entityPos)
                }

                // check for unknowns
                val rarity = OuterVoidItemDatabase.getRarity(itemEntity)
                if (rarity == GearTier.MYTHIC) {
                    println("Mythic thingy finally found at and database is correct: " + itemEntity.entityPos)
                }
                if (rarity == GearTier.CRAFTED && !unknownItems.contains(itemEntity)) {
                    unknownItems.add(itemEntity)
                    println("Name: " + itemEntity.stack.name.string)
                    println(
                        "Model: " + itemEntity.stack.get(DataComponentTypes.CUSTOM_MODEL_DATA)?.floats()[0]
                    )
                    println("Rarity: $rarity")
                    println("Durability: " + itemEntity.stack.damage)
                }
            }
        }

        // Remove items that are gone
        val closeRangeBox = player.boundingBox.expand(32.0)
        itemsRemoved = seenItems.removeIf { item: ItemEntity ->
            item.entityPos.getY() < 117 ||
                    (closeRangeBox.contains(item.entityPos) && !nearbyItems.contains(item))
        }
    }

    fun saveIslandNodes() {
        // Serialize the ALL_ISLANDS_NODES map to JSON
        try {
            FileWriter(FILE_PATH.toFile()).use { writer ->
                GSON.toJson(ALL_ISLANDS_NODES, writer)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // from file
    fun loadIslandNodes(event: InitEvent) {
        // Check if the file exists and read it if it does
        if (Files.exists(FILE_PATH)) {
            try {
                FileReader(FILE_PATH.toFile()).use { reader ->
                    // Deserialize the JSON into the ALL_ISLANDS_NODES map
                    val type = object : TypeToken<MutableMap<Int, IslandNode>>() {}.type
                    ALL_ISLANDS_NODES.clear()
                    val loadedNodes = GSON.fromJson<MutableMap<Int, IslandNode>>(reader, type)
                    if (loadedNodes != null) {
                        ALL_ISLANDS_NODES.putAll(loadedNodes)
                    }
                }
            } catch (e: IOException) {
                println("failed to load from $FILE_PATH")
                e.printStackTrace()
            }
        }
    }

    // loads the island data from nearby chunks and saves it to file
    fun cacheIslandData() {
        // check nulls
        val client = MinecraftClient.getInstance() ?: return
        val player = client.player ?: return
        val world = client.world ?: return

        if (!LocationUtils.isInOuterVoid(player.x, player.z)) return
        val nodes: MutableMap<Int, IslandNode> = ConcurrentHashMap<Int, IslandNode>()

        println("Island calculation2 started...")
        val visited: MutableSet<Vec3i?> = HashSet<Vec3i?>()
        val minX = 13577
        val maxX = 14046
        val minY = 116
        val maxY = 240
        val minZ = -3588
        val maxZ = -3187

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                for (z in minZ..maxZ) {
                    val pos = Vec3i(x, y, z)
                    if (!isSolidBlock(world, pos)) continue

                    if (!visited.add(pos)) continue

                    val island = getIslandBlocks(world, pos)
                    visited.addAll(island)
                    val surface = getSurfaceBlocks(world, island)
                    if (surface.isEmpty()) continue

                    val node = IslandNode(getBoundBox(surface), surface)
                    val id = nodes.size
                    nodes[id] = node
                }
            }
        }
        ALL_ISLANDS_NODES = nodes
        println("block calculations finnished found " + ALL_ISLANDS_NODES.size + " island, starting neighbor checks")

        // Connect neighbors based on movement constraints
        for (entryA in nodes.entries) {
            val a = entryA.value
            for (entryB in nodes.entries) {
                if (canTravelFromIslandAtoB(entryA.key, entryB.key, false)) {
                    a.neighbors.add(entryB.key)
                } else if (canTravelFromIslandAtoB(entryA.key, entryB.key, true)) {
                    a.neighborsReqDoubleJump.add(entryB.key)
                }
            }
        }
        ALL_ISLANDS_NODES = nodes
        saveIslandNodes()
        println("Island calculation2 complete and saved.")
    }

    // TODO config executor
    fun clearSeenItems() {
        seenItems.clear()
    }


    private fun canTravelFromIslandAtoB(a: Int, b: Int, doubleJump: Boolean): Boolean {
        val boxA = ALL_ISLANDS_NODES[a]?.bounds ?: return false
        val boxB = ALL_ISLANDS_NODES[b]?.bounds ?: return false

        val aBlocks: MutableList<Vec3i> = ArrayList<Vec3i>()
        for (corner in boxA.getCorners()) {
            getClosestBlockOnIsland(a, Vec3d.of(corner))?.let { aBlocks.add(it) }
        }

        val bBlocks: MutableList<Vec3i> = ArrayList<Vec3i>()
        for (corner in boxB.getTopCorners()) {
            bBlocks.add(getClosestBlockOnIsland(b, Vec3d.of(corner)) ?: continue)
        }

        var result = false
        for (blockA in aBlocks) {
            for (blockB in bBlocks) {
                if (isLocationInRange(blockB, blockA, false)) {
                    result = true
                }
            }
        }

        if (doubleJump) {
            // island is already a neighbor
            if (result) {
                return false
            }

            for (blockA in aBlocks) {
                for (blockB in bBlocks) {
                    if (isLocationInRange(blockB, blockA, true)) {
                        result = true
                    }
                }
            }
        }

        return result
    }

    private fun canTravelToIsland(a: Box, b: Vec3i, doubleJump: Boolean): Boolean {
        for (corner in a.getCorners()) {
            if (isLocationInRange(corner, b, doubleJump)) {
                return true
            }
        }
        return false
    }

    private fun getBoundBox(island: MutableSet<Vec3i>): Box {
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        var minZ = Int.MAX_VALUE
        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE
        var maxZ = Int.MIN_VALUE

        for (pos in island) {
            if (pos.x < minX) minX = pos.x
            if (pos.y < minY) minY = pos.y
            if (pos.z < minZ) minZ = pos.z
            if (pos.x > maxX) maxX = pos.x
            if (pos.y > maxY) maxY = pos.y
            if (pos.z > maxZ) maxZ = pos.z
        }

        // Add 1 to max values because Box is inclusive-exclusive
        return Box(
            minX.toDouble(),
            minY.toDouble(),
            minZ.toDouble(),
            (maxX + 1).toDouble(),
            (maxY + 1).toDouble(),
            (maxZ + 1).toDouble()
        )
    }

    private fun drawPath(event: WorldRenderEvent, startPos: Vec3d, path: PathToItem?, color: Color) {
        if (path == null) {
            //System.out.println("tryed to draw null path");
            return
        }

        val client = MinecraftClient.getInstance() ?: return
        val player = client.player ?: return

        if (startPos == player.entityPos) {
            if (path.nodes.isEmpty()) {
                // draws item and line to it if path is empty
                event.drawBox(
                    path.item.boundingBox,
                    OuterVoidItemDatabase.getColor(OuterVoidItemDatabase.getRarity(path.item)),
                    xray = true
                )
                event.drawLineToEye(path.item.entityPos, color, true)
                return
            }

            // draws line from screen to first node
            event.drawLineToEye(
                Vec3d.of(path.nodes[0]).add(Vec3d(0.5, 0.5, 0.5)),
                color,
                false
            )
        } else {
            if (path.nodes.isEmpty()) {
                // draws item and line to it
                event.drawBox(
                    path.item.boundingBox,
                    OuterVoidItemDatabase.getColor(OuterVoidItemDatabase.getRarity(path.item)),
                    xray = true,
                    thicknessMultiplier = 2.5,
                )
                event.drawLine(
                    startPos,
                    path.item.entityPos,
                    color,
                    true,
                )
                return
            }

            // draw line from last item to first node
            event.drawLine(
                startPos,
                Vec3d.of(path.nodes[0]).add(Vec3d(0.5, 0.5, 0.5)),
                color,
                true,
            )
        }


        // Loop through the path and draw a line between each consecutive block
        for (i in 0..<path.nodes.size - 1) {
            val startPosition = path.nodes[i]
            val endPosition = path.nodes[i + 1]

            // draw block
            event.drawBox(startPosition.toBox(), color, xray = true)
            // Draw a line between the two blocks
            event.drawLine(
                Vec3d.of(startPosition).add(Vec3d(0.5, 0.5, 0.5)),
                Vec3d.of(endPosition).add(Vec3d(0.5, 0.5, 0.5)),
                color,
                true,
            )
        }
        // draw the last block because the loop above doesnt
        event.drawBox(path.nodes.getLast()?.toBox(), color, xray = true)
        // draw the item that we tryin to get to
        event.drawBox(
            path.item.boundingBox,
            OuterVoidItemDatabase.getColor(OuterVoidItemDatabase.getRarity(path.item)),
            xray = true
        )
        // draw the line from last block to the item
        event.drawLine(
            path.nodes.getLast()?.toVec3d()?.add(0.5, 0.5, 0.5),
            path.item.entityPos,
            color,
            true
        )
    }

    private fun getClosestBlockOnIsland(islandId: Int, pos: Vec3d): Vec3i? {
        val island = ALL_ISLANDS_NODES[islandId]
        if (island == null || island.blocks.isEmpty()) return null

        var closest: Vec3i? = null
        var closestDistanceSq = Double.MAX_VALUE

        for (block in island.blocks) {
            val dx = block.x + 0.5 - pos.x
            val dy = block.y + 0.5 - pos.y
            val dz = block.z + 0.5 - pos.z
            val distSq = dx * dx + dy * dy + dz * dz

            if (distSq < closestDistanceSq) {
                closestDistanceSq = distSq
                closest = block
            }
        }

        return closest
    }

    fun getNextPath(start: Vec3d): PathToItem? {
        val paths: MutableMap<ItemEntity, MutableList<Int>> = HashMap<ItemEntity, MutableList<Int>>()
        for (item in seenItems) {
            paths[item] = findShortestPath(start, item.entityPos, config.voidquartzPropulsor)
        }
        if (currentItem != null) paths.remove(currentItem)

        // remap the path of island id to a path of block positions
        val paths2: MutableList<PathToItem> = ArrayList<PathToItem>()
        // this is for each item path
        for (entry in paths.entries) {
            var lastId: Int? = null
            val path2: MutableList<Vec3i> = ArrayList<Vec3i>()

            for (islandId in entry.value) {
                if (lastId != null) {
                    val island: IslandNode = ALL_ISLANDS_NODES[islandId] ?: run {
                        println("Error: Island $islandId not found")
                        return null
                    }
                    val closestBlock: Vec3i = checkNotNull(
                        (getClosestBlockOnIsland(
                            lastId, Vec3d.of(
                                island.center
                            )
                        ))
                    )
                    val closestBlockOnSelf = (getClosestBlockOnIsland(islandId, Vec3d.of((closestBlock))))
                    closestBlockOnSelf?.let {
                        path2.add(closestBlock)
                        path2.add(it)
                    }
                }
                lastId = islandId
            }
            paths2.add(PathToItem(entry.key, path2))
        }


        var shortestPath: PathToItem? = null
        var shortestDistance = Double.MAX_VALUE

        // Loop through each path and calculate the total distance
        for (path in paths2) {
            var totalDistance = 0.0

            //if (path.nodes.size() < 2) continue;

            // Calculate the total distance for the current path by summing the island-to-island distances
            for (i in 0..<path.nodes.size - 1) {
                val currentBlock = path.nodes[i]
                val nextBlock = path.nodes[i + 1]

                val distance = sqrt(currentBlock.getSquaredDistance(nextBlock))

                totalDistance += distance
            }
            if (!path.nodes.isEmpty()) {
                totalDistance += Vec3d.of(path.nodes.getLast()).distanceTo(path.item.entityPos)
            } else {
                // if there is no path just grab the item
                if (isLocationInRange(path.item.entityPos, start, config.voidquartzPropulsor)) {
                    totalDistance += (start.distanceTo(path.item.entityPos))
                } else {
                    // we cant get there
                    totalDistance = Double.MAX_VALUE
                }
            }

            // prioritize rare items
            if (OuterVoidItemDatabase.getRarity(path.item) == GearTier.RARE) {
                if (totalDistance != Double.MAX_VALUE) {
                    totalDistance *= config.rareItemDistanceMultiplier.toDouble()
                }
            }

            // If the total distance of the current path is shorter than the previous shortest, update the shortest path
            if (totalDistance < shortestDistance) {
                shortestDistance = totalDistance
                shortestPath = path
            }
        }

        if (shortestPath != null) {
            currentItem = shortestPath.item
            return shortestPath
        } else {
            //System.out.println("No path found.");
            currentItem = null
            return null
        }
    }


    fun findShortestPath(start: Vec3d, end: Vec3d, doubleJump: Boolean): MutableList<Int> {
        // First, find the islands each point is on.

        val startIsland = findIslandForPoint(start)
        val endIsland = findIslandForPoint(end)

        //System.out.println(("attempting to find path: " + startIsland + " " + endIsland));
        if (startIsland == null || endIsland == null) {
            return mutableListOf()
        }

        //System.out.println(bfs(startIsland, endIsland).size());
        return bfs(startIsland, endIsland, doubleJump)
    }

    // bfs returns the path with the least hops not shortest by distance
    private fun bfs(startIsland: Int, endIsland: Int, doubleJump: Boolean): MutableList<Int> {
        // BFS to find the shortest path between startIsland and endIsland.
        val queue: Queue<Int> = LinkedList()
        val previous: MutableMap<Int, Int> = HashMap<Int, Int>()
        val visited: MutableSet<Int?> = HashSet<Int?>()

        queue.add(startIsland)
        visited.add(startIsland)

        while (!queue.isEmpty()) {
            val current = queue.poll()

            if (current == endIsland) {
                return if (startIsland != endIsland) {
                    //System.out.println("reconstruction path: " + startIsland + " " + endIsland);
                    reconstructPath(previous, startIsland, endIsland)
                } else {
                    //System.out.println("called bfs with start and end equal");
                    mutableListOf()
                }
            }

            val currentNode: IslandNode = ALL_ISLANDS_NODES[current] ?: run {
                println("Error: Island $current not found")
                return mutableListOf()
            }

            for (neighbor in currentNode.neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                    previous[neighbor] = current
                }
            }

            if (doubleJump) {
                for (neighbor in currentNode.neighborsReqDoubleJump) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor)
                        queue.add(neighbor)
                        previous[neighbor] = current
                    }
                }
            }
        }
        return mutableListOf() // No path found.
    }

    private fun reconstructPath(
        previous: MutableMap<Int, Int>,
        startIsland: Int,
        endIsland: Int
    ): MutableList<Int> {
        val path: MutableList<Int> = ArrayList<Int>()
        var current = endIsland

        while (current != startIsland) {
            path.add(current)
            current = previous[current] ?: continue
        }

        // Add the start island to the path
        path.add(startIsland)
        path.reverse() // Reverse the path to go from startIsland to endIsland
        return path
    }

    // closest island to Vec3d
    private fun findIslandForPoint(pos: Vec3d): Int? {
        var closest: Int? = null
        var smallestDistance = Double.MAX_VALUE

        // Loop through all islands to find which one the point belongs to.
        val pos2 = Vec3i(pos.x.toInt(), pos.y.toInt(), pos.z.toInt())
        for (entry in ALL_ISLANDS_NODES.entries) {
            val island = entry.value
            //System.out.println("ergia: " + island.bounds + " " + pos2);
            if (canTravelToIsland(island.bounds, pos2, false)) {
                val distance = pos.squaredDistanceTo(Vec3d.of(island.center))
                if (distance < smallestDistance) {
                    smallestDistance = distance
                    closest = entry.key
                }
            }
        }
        return closest
    }

    fun isLocationInRange(location: Vec3i, origin: Vec3i, doubleJump: Boolean): Boolean {
        var above = PARABOLA_HEIGHT_ABOVE
        var below = PARABOLA_HEIGHT_BELOW
        var radius = PARABOLA_RADIUS
        if (doubleJump) {
            above = PARABOLA_HEIGHT_ABOVE_DJ
            below = PARABOLA_HEIGHT_BELOW_DJ
            radius = PARABOLA_RADIUS_DJ
        }

        // Calculate the parabola's steepness factor
        val a = (above + below) / (radius * radius)

        // Set the vertex point above the player's head
        val vertex = (origin.add(0, above.toInt(), 0))

        // Check if the item is inside the parabola directly by calling the function
        return isLocationInsideParabola(location, vertex, a, radius)
    }

    fun isLocationInRange(location: Vec3d, origin: Vec3d, doubleJump: Boolean): Boolean {
        val loc = Vec3i(location.x.toInt(), location.y.toInt(), location.z.toInt())
        val ori = Vec3i(origin.x.toInt(), origin.y.toInt(), origin.z.toInt())

        return isLocationInRange(loc, ori, doubleJump)
    }

    private fun isLocationInsideParabola(location: Vec3i, vertex: Vec3i, a: Float, radius: Float): Boolean {
        // Calculate the horizontal distance squared from the vertex (in the XZ plane)
        val dx = (location.x - vertex.x).toFloat()
        val dz = (location.z - vertex.z).toFloat()
        val distSq = dx * dx + dz * dz

        // Check if the item is within the horizontal radius of the parabola
        if (distSq > radius * radius) {
            return false // Item is outside the horizontal bounds of the parabola
        }

        // Calculate the expected y-coordinate at the (x, z) position
        val dy = a * distSq

        // Check if the item’s y-coordinate is below the calculated parabola height
        return location.y <= vertex.y - dy // If item is below the parabola's surface, it's inside
    }

    /*
    private fun visualizeParabola(origin: Vec3d, doubleJump: Boolean) {
        val client = MinecraftClient.getInstance()
        if (client == null || client.world == null) return

        var above = PARABOLA_HEIGHT_ABOVE
        var below = PARABOLA_HEIGHT_BELOW
        var radius = PARABOLA_RADIUS
        if (doubleJump) {
            above = PARABOLA_HEIGHT_ABOVE_DJ
            below = PARABOLA_HEIGHT_BELOW_DJ
            radius = PARABOLA_RADIUS_DJ
        }

        // Calculate the parabola's steepness factor
        val a = (above + below) / (radius * radius)

        // Set the vertex point above the player's head
        val vertex = origin.add(0.0, above.toDouble(), 0.0)

        // Loop through points to create the parabola shape and visualize it with particles
        var x = -radius
        while (x <= radius) {
            // Adjust resolution as needed
            var z = -radius
            while (z <= radius) {
                // Adjust resolution as needed
                // Only consider points within the radius
                val distSq = x * x + z * z
                if (distSq > radius * radius) {
                    z += 1.0f
                    continue
                }

                // Calculate the expected y-coordinate of the parabola at (x, z)
                val dy = a * distSq

                // Position on the parabola
                val pos =
                    vertex.add(x.toDouble(), -dy.toDouble(), z.toDouble()) // The negative dy moves the point downward

                // Visualize the point with particles
                client.world.addParticle(
                    ParticleTypes.END_ROD,
                    pos.x,
                    pos.y,
                    pos.z,
                    0,
                    0.01,
                    0
                ) // Adjust particle type and effect as needed
                z += 1.0f
            }
            x += 1.0f
        }
    }

     */

    private fun isSolidBlock(world: World, pos: Vec3i): Boolean {
        val block = BlockPos(pos.x, pos.y, pos.z)
        val state = world.getBlockState(block)

        // Skip barrier blocks
        if (state.block == Blocks.BARRIER) return false

        return state.isSolidBlock(world, block)
    }


    // Direction vectors for moving in 26 directions
    private val DIRECTIONS: Array<IntArray>

    init {
        val directions: MutableList<IntArray> = ArrayList<IntArray>()
        for (dx in -1..1) {
            for (dy in -1..1) {
                for (dz in -1..1) {
                    if (dx == 0 && dy == 0 && dz == 0) continue  // Skip the center

                    directions.add(intArrayOf(dx, dy, dz))
                }
            }
        }
        DIRECTIONS = directions.toTypedArray<IntArray>()
    }

    // connected blocks
    fun getIslandBlocks(world: World, start: Vec3i?): MutableSet<Vec3i> {
        val visited: MutableSet<Vec3i?> = HashSet<Vec3i?>()
        val islandBlocks: MutableSet<Vec3i> = HashSet<Vec3i>()

        val stack: Deque<Vec3i> = ArrayDeque<Vec3i>()
        stack.push(start)
        visited.add(start)

        while (!stack.isEmpty()) {
            val current = stack.pop()

            if (!isSolidBlock(world, current)) continue
            islandBlocks.add(current)

            for (dir in DIRECTIONS) {
                val neighbor = current.add(dir[0], dir[1], dir[2])
                if (visited.add(neighbor)) {
                    stack.push(neighbor)
                }
            }
        }

        return islandBlocks
    }

    // give it an island it returns the surface
    fun getSurfaceBlocks(world: World, blocks: MutableSet<Vec3i>): MutableSet<Vec3i> {
        val surfaceBlocks: MutableSet<Vec3i> = HashSet<Vec3i>()

        for (current in blocks) {
            // Check if the block above is not solid (this ensures it's a surface block)
            val blockAbove = current.up()
            val block2Above = blockAbove.up()
            if (!isSolidBlock(world, blockAbove) && !isSolidBlock(world, block2Above)) {
                surfaceBlocks.add(current) // Add to surface blocks
            }
        }

        return surfaceBlocks
    }

    class PathToItem(var item: ItemEntity, var nodes: MutableList<Vec3i>)

    data class IslandNode(val bounds: Box, var blocks: MutableSet<Vec3i> = mutableSetOf()) {
        var neighbors: MutableList<Int> = ArrayList<Int>()
        var neighborsReqDoubleJump: MutableList<Int> = ArrayList<Int>()

        val center: Vec3i
            get() {
                // Calculate the center of the box by averaging the min and max coordinates
                val centerX = ((bounds.minX + bounds.maxX) / 2).toInt()
                val centerZ = ((bounds.minZ + bounds.maxZ) / 2).toInt()

                // Return the center as a Vec3i
                return Vec3i(centerX, bounds.maxY.toInt(), centerZ)
            }
    }
}
