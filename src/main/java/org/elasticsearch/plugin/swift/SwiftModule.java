package org.elasticsearch.plugin.swift;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.repositories.swift.SwiftService;

/**
 * Swift repository module
 */
public class SwiftModule extends AbstractModule {

    public SwiftModule() {
        super();
    }

    @Override
    protected void configure() {
        bind(SwiftService.class).asEagerSingleton();
    }
}
