package sidly.wynnadhoc.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public class CommandRegistrationEvent extends Event<CommandRegistrationEvent> {
    public CommandDispatcher<FabricClientCommandSource> dispatcher;
    public CommandRegistryAccess registryAccess;

    public void register(final LiteralArgumentBuilder<FabricClientCommandSource> command) {
        dispatcher.register(ClientCommandManager.literal("WynnAdhoc").then(command));
        dispatcher.register(ClientCommandManager.literal("wynnadhoc").then(command));
    }

    public CommandRegistrationEvent(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        this.dispatcher = dispatcher;
        this.registryAccess = registryAccess;
        this.fire();
    }
}
