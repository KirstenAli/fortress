package com.fortress.dto;

import com.fortress.entity.EntityInterface;

@Deprecated
public interface DTOInterface {
    String getId();
    void setId(String id);
    EntityInterface getEntity();
}
