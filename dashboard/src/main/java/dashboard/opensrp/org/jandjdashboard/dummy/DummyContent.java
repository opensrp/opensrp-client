package dashboard.opensrp.org.jandjdashboard.dummy;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dashboard.opensrp.org.jandjdashboard.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static final List<Drawable> menudrawable = new ArrayList<Drawable>();
    public static String [] menuTitles = {
            "UPCOMING SCHEDULE STATUS",
            "FAMILY PLANNING STATUS",
            "ANC/PNC/ENCC REMINDER VISIT STATUS",
            "REPRODUCTIVE HEALTH SERVICE",
            "DELIVERY STATUS",
            "CHILD & NEONATAL CARE",
            "DEATH",
            "NUTRITION",
            "CONTRACEPTIVE SUPPLY STATUS"
    };

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 9;

    static {
        // Add some sample items.
        for (int i = 0; i < COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }


    public static void addDrawables(Context context) {
        menudrawable.add(context.getResources().getDrawable(R.drawable.upcoming));
        menudrawable.add(context.getResources().getDrawable(R.drawable.familyplanning));
        menudrawable.add(context.getResources().getDrawable(R.drawable.ancpncreminder));
        menudrawable.add(context.getResources().getDrawable(R.drawable.reproductive));
        menudrawable.add(context.getResources().getDrawable(R.drawable.delivery));
        menudrawable.add(context.getResources().getDrawable(R.drawable.child));
        menudrawable.add(context.getResources().getDrawable(R.drawable.death));
        menudrawable.add(context.getResources().getDrawable(R.drawable.nutrition));
        menudrawable.add(context.getResources().getDrawable(R.drawable.contraceptive));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(menuTitles[position], menuTitles[position], makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
