package org.ei.opensrp.indonesia;

import android.content.res.AssetManager;

import org.apache.commons.io.IOUtils;
import org.ei.opensrp.DristhiConfiguration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by koros on 2/4/16.
 */
public class BidanConfiguration extends DristhiConfiguration {

    public BidanConfiguration(AssetManager assetManager) {
        super(assetManager);
        try {
            InputStream dummyNameFile = assetManager.open("dummy_name.json");
            dummyData = IOUtils.toString(dummyNameFile);
            IOUtils.closeQuietly(dummyNameFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
