package io.sahil.server.core.model;

/**
 * @author Sahil Saini
 */
public class InfluxDTO {

    private String tagValue;
    private float fieldValue;
    private long timestamp;

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public float getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(float fieldValue) {
        this.fieldValue = fieldValue;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
