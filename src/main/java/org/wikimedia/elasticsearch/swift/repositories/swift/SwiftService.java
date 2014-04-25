package org.wikimedia.elasticsearch.swift.repositories.swift;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.exception.CommandException;
import org.javaswift.joss.model.Account;

public class SwiftService extends AbstractLifecycleComponent<SwiftService> {
	// The account we'll be connecting to Swift with
    private Account swiftUser;

    /**
     * Constructor
     * @param settings Settings for our repository. Injected.
     */
    @Inject
    public SwiftService(Settings settings) {
        super(settings);
    }

    /**
     * Create a Swift account object and connect it to Swift
     * @param url The auth url (eg: localhost:8080/auth/v1.0/)
     * @param username The username
     * @param password The password
     */
    public synchronized Account swift(String url, String username, String password) {
        if (swiftUser != null) {
            return swiftUser;
        }

        try {
            AccountConfig conf = new AccountConfig();
            conf.setUsername(username);
            conf.setPassword(password);
            conf.setAuthUrl(url);
            conf.setAuthenticationMethod(AuthenticationMethod.BASIC);
            swiftUser = new AccountFactory(conf).createAccount();
        } catch (CommandException ce) {
            throw new ElasticsearchIllegalArgumentException("Unable to authenticate to Swift", ce);
        }
        return swiftUser;
    }

    /**
     * Start the service. No-op here.
     */
    @Override
    protected void doStart() throws ElasticsearchException {
    }

    /**
     * Stop the service. No-op here.
     */
    @Override
    protected void doStop() throws ElasticsearchException {
    }

    /**
     * Close the service. No-op here.
     */
    @Override
    protected void doClose() throws ElasticsearchException {
    }
}
