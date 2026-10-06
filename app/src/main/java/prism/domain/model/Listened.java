package prism.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import prism.infrastructure.filesystem.ActiveImageType;
import prism.utils.ArrayUtils;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Listened {

    private String sender;
    private String command;

    public @NonNull String extractFirstImageFileExt() {
        for (String token : this.getCommandTokens()) {
            String ext = this.getExtension(token);

            if (ActiveImageType.has(ext)) {
                return ext;
            }
        }

        return "";
    }

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

    private @NonNull String getExtension(@NonNull String token) {
        int dotIndex = token.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == token.length() - 1) {
            return "";
        }

        return token.substring(dotIndex + 1);
    }
}