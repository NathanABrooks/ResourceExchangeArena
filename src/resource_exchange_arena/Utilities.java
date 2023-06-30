package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 * @author refracc
 * @since 2023-JUNE-30
 * @version 1.0.0
 *
 * This class holds some general utility methods for general use in the program.
 */
public final class Utilities {

    public static void write(@NotNull Writer writer, String @NotNull ... data) throws IOException {
        for (String s : data)
            writer.append(s);
    }
}