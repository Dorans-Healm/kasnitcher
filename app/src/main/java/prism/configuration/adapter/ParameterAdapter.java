package prism.configuration.adapter;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration settings for the parameter input operations.
 */
@Getter
@Setter
public class ParameterAdapter {

    /**
     * The file path to load parameters from.
     */
    private String file;
}