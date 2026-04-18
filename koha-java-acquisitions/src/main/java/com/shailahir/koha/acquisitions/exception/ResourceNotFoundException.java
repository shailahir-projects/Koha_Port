package com.shailahir.koha.acquisitions.exception;

/**
 * Thrown when a requested resource (order, basket, vendor, invoice, etc.) is not found.
 */
public class ResourceNotFoundException extends AcquisitionException {

    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(resourceType + " not found: " + resourceId);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public String getResourceType() { return resourceType; }
    public Object getResourceId()   { return resourceId; }
}

