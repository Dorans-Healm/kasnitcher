package prism.configuration.apdater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CacheAdapter {

    private Integer amount = 3;
}