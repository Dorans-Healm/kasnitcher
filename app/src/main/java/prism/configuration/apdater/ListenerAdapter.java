package prism.configuration.apdater;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListenerAdapter {

    // TODO - Affirm the correct socket dir
    private String directory = "/var/";
}