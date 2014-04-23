package org.elasticsearch.plugin.swift;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.repositories.swift.SwiftService;

/**
 * Swift module. Binds the service.
 */
public class SwiftModule extends AbstractModule {
	/**
	 * Constructor. Much boredom.
	 */
    public SwiftModule() {
        super();
    }

    /**
     * Bind the service
     */
    @Override
    protected void configure() {
        bind(SwiftService.class).asEagerSingleton();
    }
}
