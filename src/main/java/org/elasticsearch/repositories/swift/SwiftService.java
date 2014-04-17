package org.elasticsearch.repositories.swift;

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

    private Account swiftUser;

    @Inject
    public SwiftService(Settings settings) {
        super(settings);
    }

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
