package org.ei.opensrp.core;

import android.content.UriMatcher;
import android.net.Uri;

import org.ei.opensrp.core.db.repository.RegisterContentProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Maimoona on 1/14/2017.
 */
//@RunWith(RobolectricTestRunner.class)
public class RegisterContentProviderTest {
    private static final int ALL = 1;
    private static final int ID = 2;
    private static final int COUNT = 3;
    private static final int JOIN = 4;

    public static final String AUTHORITY = "org.ei.opensrp.provider.registers";
    public static final Uri CONTENT_JOIN_URI(String table, String id, String referenceTable, String referenceColumn, String groupBy){
        return Uri.parse("content://" + AUTHORITY + "/join/"+table+"/"+id+"/"+referenceTable+"/"+referenceColumn/*+"?group="+groupBy*/);
    }
   /// @Test
    public void testJoinUri(){
        Uri uri = CONTENT_JOIN_URI("pkhousehold", "id", "pkindividual", "relationalid", "pkhousehold.id");
        assertEquals("/join/pkhousehold/id/pkindividual/relationalid", uri.getPath());

        UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(RegisterContentProvider.AUTHORITY, "/join/*/*/*/*", 4);
        //assertEquals("group=pkhousehold.id", uri.getQuery());
        assertEquals(4, sURIMatcher.match(uri));

    }
}
