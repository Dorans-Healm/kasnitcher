package prism.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import prism.infrastructure.filesystem.ActiveImageType;
import prism.utils.ArrayUtils;

import java.util.Objects;

/**
 * Represents a parsed command that has been listened to or received by the system.
 * Contains information about the sender and the raw command string.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Listened {

    /**
     * The sender or origin of the command.
     */
    private String sender;

    /**
     * The raw command string received.
     */
    private String command;

    /**
     * Extracts the first file extension from the command tokens that matches an active image type.
     *
     * @return the matching file extension, or an empty string if none is found
     */
    public @NonNull String extractFirstImageFileExt() {
        for (String token : this.getCommandTokens()) {
            String ext = this.getExtension(token);

            if (ActiveImageType.has(ext)) {
                return ext;
            }
        }

        return "";
    }

    /**
     * Extracts the command token (file name) that matches the specified file extension.
     *
     * @param ext the file extension to search for
     * @return the matching command token, or an empty string if none is found or if ext is blank
     */
    public @NonNull String extractCmdFile(@NonNull String ext) {
        if (ext.isBlank()) {
            return "";
        }

        for (String token : this.getCommandTokens()) {
            if (this.getExtension(token).equals(ext)) {
                return token;
            }
        }

        return "";
    }

    /**
     * Splits the listened command by whitespace, stripping surrounding quotes of each token.
     *
     * @return the command tokens, or an empty array if there is no command
     */
    private @NonNull String[] getCommandTokens() {
        return ArrayUtils.splitCommand(this.command);
    }

    /**
     * Extracts the file extension from a given token.
     *
     * @param token the command token or file name
     * @return the file extension, or an empty string if none is found
     */
    private @NonNull String getExtension(@NonNull String token) {
        int dotIndex = token.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == token.length() - 1) {
            return "";
        }

        return token.substring(dotIndex + 1);
    }
}