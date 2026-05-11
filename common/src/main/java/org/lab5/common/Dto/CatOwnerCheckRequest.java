package org.lab5.common.Dto;

public class CatOwnerCheckRequest {
    private Long catId;
    private Long ownerId;

    public CatOwnerCheckRequest() {}

    public CatOwnerCheckRequest(Long catId, Long ownerId) {
        this.catId = catId;
        this.ownerId = ownerId;
    }

    public Long getCatId() {
        return catId;
    }


    public Long getOwnerId() {
        return ownerId;
    }
}
