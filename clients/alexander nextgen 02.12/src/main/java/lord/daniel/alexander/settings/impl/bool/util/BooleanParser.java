package lord.daniel.alexander.settings.impl.bool.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public final class BooleanParser {

    private static final Map<String, Boolean> PARSE;

    static {
        PARSE = new HashMap<String, Boolean>() {
            {
                this.put("enable", true);
                this.put("true", true);
                this.put("on", true);
                this.put("1", true);
                this.put("disable", false);
                this.put("false", false);
                this.put("off", false);
                this.put("0", false);
            }
        };
    }

    public static Optional<Boolean> parse(final String input) {
        return Optional.ofNullable(BooleanParser.PARSE.get(input.toLowerCase()));
    }

}