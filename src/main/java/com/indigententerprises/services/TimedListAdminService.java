package com.indigententerprises.services;

import com.indigententerprises.domain.Node;

import java.util.List;

public interface TimedListAdminService<T> {
    public List<Node<T>> retrieveCopy();
}
