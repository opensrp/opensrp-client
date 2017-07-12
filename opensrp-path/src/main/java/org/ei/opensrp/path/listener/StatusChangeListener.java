package org.ei.opensrp.path.listener;

/**
 * Created by raihan on 4/16/17.
 */
public interface StatusChangeListener {
    public void updateStatus();
    public void updateClientAttribute(String attributeName, Object attributeValue);
}
