package resource_exchange_arena;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 * @author refracc
 * @version 1.0.0
 * <p>
 * This class holds some general utility methods for general use in the program.
 * @since 2023-JUNE-30
 */
public final class Utilities {

    /**
     * Append data to some sort of {@link Writer}.
     * @param writer A generic implementation of a {@link Writer}
     * @param data The data to be appended to the {@link Writer} (in the form of a {@link String}).
     * @throws IOException If there is some kind of issue with the {@link Writer}.
     */
    public static void write(@NotNull Writer writer, String @NotNull ... data) throws IOException {
        for (String s : data)
            writer.append(s);
    }
}