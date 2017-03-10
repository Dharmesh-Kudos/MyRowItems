package smart.framework;

import android.content.SearchRecentSuggestionsProvider;

public class SmartSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.dailypass.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SmartSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}