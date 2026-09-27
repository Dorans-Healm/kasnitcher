package prism.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import prism.infrastructure.filesystem.ActiveImageType;

@NoArgsConstructor
@AllArgsConstructor
public class Listened {

    private String sender;
    private String command;

    public @NonNull String extractFirstImageFileExt() {
        int dotIndex;

        while ((dotIndex = this.command.indexOf(".")) != -1) {
            int crrIndex = dotIndex + 1;

            StringBuilder ext = new StringBuilder();
            while (crrIndex < this.command.length()
                    && this.command.charAt(crrIndex) != ' ') {
                ext.append(this.command.charAt(crrIndex));
            }

            String extType = ext.toString();

            if (ActiveImageType.has(extType)) {
                return extType;
            }
        }

        return "";
    }

    public @NonNull String extractCmdFile(@NonNull String ext) {
        assert this.command != null;

        if (this.command.isBlank() || ext.isBlank()) {
            return "";
        }

        int start = 0;
        int end = ext.length();

        for (int i = this.command.indexOf(ext) - 2; i >= 0; i--) {
            start = i;

            if (this.command.charAt(i) == ' ') {
                break;
            }
        }

        return this.command.substring(start, end);
    }
}