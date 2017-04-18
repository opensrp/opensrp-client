package org.ei.opensrp.path.viewstates;

import android.os.Parcel;

import com.vijay.jsonwizard.mvp.ViewState;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

/**
 * Created by vijay on 5/14/15.
 */
public class PathJsonFormFragmentViewState extends JsonFormFragmentViewState implements android.os.Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public PathJsonFormFragmentViewState() {
    }

    private PathJsonFormFragmentViewState(Parcel in) {
        super(in);
    }

    public static final Creator<PathJsonFormFragmentViewState> CREATOR = new Creator<PathJsonFormFragmentViewState>() {
        public PathJsonFormFragmentViewState createFromParcel(
                Parcel source) {
            return new PathJsonFormFragmentViewState(source);
        }

        public PathJsonFormFragmentViewState[] newArray(
                int size) {
            return new PathJsonFormFragmentViewState[size];
        }
    };
}
