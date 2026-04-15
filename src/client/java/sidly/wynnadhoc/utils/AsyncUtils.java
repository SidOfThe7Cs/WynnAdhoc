package sidly.wynnadhoc.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncUtils {
    public static <T> CompletableFuture<T> runAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task);
    }

    public static CompletableFuture<Void> runAsync(Runnable task) {
        return CompletableFuture.runAsync(task);
    }
}

