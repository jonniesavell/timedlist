package com.indigententerprises.components;

import com.indigententerprises.services.ReportingService;
import com.indigententerprises.services.TimedListAdminService;
import com.indigententerprises.domain.Node;

import java.util.List;
import java.util.stream.Collectors;

public class TimeoutReportingComponent<T> implements ReportingService {

    private final TimedListAdminService<T> timedListAdminService;

    public TimeoutReportingComponent(final TimedListAdminService<T> timedListAdminService) {
        this.timedListAdminService = timedListAdminService;
    }

    @Override
    public List<Node<String>> retrieveCopy() {
        return timedListAdminService
                .retrieveCopy()
                .stream()
                .map((node) -> {
                    Node<String> newNode = new Node<>(node.getT().toString());
                    newNode.setTimeoutInMilliseconds(node.getTimeoutInMilliseconds());
                    return newNode;
                })
                .collect(Collectors.toList());
    }
}
