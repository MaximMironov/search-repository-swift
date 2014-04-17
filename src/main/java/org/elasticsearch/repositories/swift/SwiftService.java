package org.elasticsearch.repositories.swift;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.exception.CommandException;
import org.javaswift.joss.model.Account;

public class SwiftService extends AbstractLifecycleComponent<SwiftService> {

    private Account swiftUser;

    @Inject
    public SwiftService(Settings settings) {
        super(settings);
    }

    public synchronized Account swift(String url, String tenantId, String username, String password) {
        if (swiftUser != null) {
            return swiftUser;
        }

        try {
	        swiftUser = new AccountFactory()
				.setUsername(username)
				.setPassword(password)
				.setAuthUrl(url)
				.setTenantId(tenantId)
				.createAccount();
        } catch (CommandException ce) {
        	throw new ElasticsearchIllegalArgumentException("Unknown host", ce);
        }

		return swiftUser;
    }

    @Override
    protected void doStart() throws ElasticsearchException {

    }

    @Override
    protected void doStop() throws ElasticsearchException {

    }

    @Override
    protected void doClose() throws ElasticsearchException {
    }
}
