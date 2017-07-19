package shared.customshadows;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.Photo;
import org.opensrp.api.constants.Gender;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;

import util.ImageUtils;

/**
 * Created by onadev on 15/06/2017.
 */
@Implements(ImageUtils.class)
public class ImageUtilsShadow extends Shadow {

    @Implementation
    public static Photo profilePhotoByClient(CommonPersonObjectClient client) {
        return new Photo();
    }


    @Implementation
    public static int profileImageResourceByGender(String gender) {
        return R.drawable.child_boy_infant;
    }

    @Implementation
    public static int profileImageResourceByGender(Gender gender) {
        return R.drawable.child_transgender_inflant;
    }

}
