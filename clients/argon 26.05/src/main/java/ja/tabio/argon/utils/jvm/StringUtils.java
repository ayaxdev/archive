package ja.tabio.argon.utils.jvm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String extractStringBetween(String input, String startDelimiter, String endDelimiter) {
        final String regexPattern = Pattern.quote(startDelimiter) + "(.*?)" + Pattern.quote(endDelimiter);
        final Pattern pattern = Pattern.compile(regexPattern);
        final Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Match not found");
        }
    }

}
