package shared.customshadows;

import android.content.Context;
import android.util.AttributeSet;

import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.customControls.FontVariant;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;

/**
 * Created by onadev on 15/06/2017.
 */
@Implements(CustomFontTextView.class)
public class FontTextViewShadow extends ShadowTextView{

    public void CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {

    }

    public void setFontVariant(final FontVariant variant) {

    }
}
