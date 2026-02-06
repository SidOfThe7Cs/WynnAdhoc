package sidly.wynnadhoc.lootruns;

import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.config.LootrunData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootrunningSaveData {
    public Map<String, LootrunData> lootruns = new HashMap<>();
    public Map<BlockPos, Long> chests = new HashMap<>();
    public List<BlockPos> bannedChests = new ArrayList<>();

    public LootrunningSaveData() {}
}
