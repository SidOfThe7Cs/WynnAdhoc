package sidly.wynnadhoc.event;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class CommandRegistrationEvent extends Event<CommandRegistrationEvent> {
    public CommandDispatcher<FabricClientCommandSource> dispatcher;
    public CommandRegistryAccess registryAccess;

    public CommandRegistrationEvent(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        this.dispatcher = dispatcher;
        this.registryAccess = registryAccess;
        this.fire();
    }
}
