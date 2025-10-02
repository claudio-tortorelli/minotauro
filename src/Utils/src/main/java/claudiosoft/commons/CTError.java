package claudiosoft.commons;

/**
 *
 * @author claudio.tortorelli
 */
public enum CTError {

    GENERIC_ERROR("0001"),
    INVALID_ARGUMENT("0002"),
    IO_GENERIC_ERROR("0003"),
    NETWORK_GENERIC_ERROR("0004"),
    FILESYSTEM_GENERIC_ERROR("0005"),
    UNDEFINED_VALUE("0006"),
    INDEXER_GENERIC("0100"),
    PLUGIN_GENERIC("0200"),
    PLUGIN_THREAD("0201"),
    OLLAMA_GENERIC("0300"),
    TRANSIENT_GENERIC("0400"),;

    private String code;

    private CTError(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }

}
