package cs157a.util;

/**
 * Makes text safe to print inside an HTML page.
 *
 * Anything a user typed must go through escape() before a JSP prints it.
 * Otherwise a display name such as <script>...</script> would run as code in
 * the browser of whoever views the page (cross-site scripting).
 */
public class Html {

    /** Replaces the five characters HTML treats as markup. null becomes "". */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }

        StringBuilder out = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            switch (c) {
                case '&'  -> out.append("&amp;");
                case '<'  -> out.append("&lt;");
                case '>'  -> out.append("&gt;");
                case '"'  -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default   -> out.append(c);
            }
        }
        return out.toString();
    }
}
