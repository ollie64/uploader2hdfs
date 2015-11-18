package qwerty;

import java.util.Collections;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;

public class SecuredContext {

    public static HashLoginService getHashLoginService() {
        // Since this example is for our test webapp, we need to setup a
        // LoginService so this shows how to create a very simple hashmap based
        // one. The name of the LoginService needs to correspond to what is
        // configured a webapp's web.xml and since it has a lifecycle of its own
        // we register it as a bean with the Jetty server object so it can be
        // started and stopped according to the lifecycle of the server itself.
        // In this example the name can be whatever you like since we are not
        // dealing with webapp realms.
        HashLoginService loginService = new HashLoginService("MyRealm");
        // FIXME hardcoded user
        loginService.putUser("oleg", Credential.getCredential("MD5:e10adc3949ba59abbe56e057f20f883e"), new String[]{"user"});

        return loginService;
    }

    public static ConstraintSecurityHandler getConstraintSecurityHandler(HashLoginService loginService) {
        // A security handler is a jetty handler that secures content behind a
        // particular portion of a url space. The ConstraintSecurityHandler is a
        // more specialized handler that allows matching of urls to different
        // constraints. The server sets this as the first handler in the chain,
        // effectively applying these constraints to all subsequent handlers in
        // the chain.
        ConstraintSecurityHandler security = new ConstraintSecurityHandler();

        // This constraint requires authentication and in addition that an
        // authenticated user be a member of a given set of roles for
        // authorization purposes.
        Constraint constraint = new Constraint();
        constraint.setName("auth");
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { "user", "admin" });

        // Binds a url pattern with the previously created constraint. The roles
        // for this constraing mapping are mined from the Constraint itself
        // although methods exist to declare and bind roles separately as well.
        ConstraintMapping mapping = new ConstraintMapping();
        mapping.setPathSpec("/workflows/*");
        mapping.setConstraint(constraint);

        // First you see the constraint mapping being applied to the handler as
        // a singleton list, however you can passing in as many security
        // constraint mappings as you like so long as they follow the mapping
        // requirements of the servlet api. Next we set a BasicAuthenticator
        // instance which is the object that actually checks the credentials
        // followed by the LoginService which is the store of known users, etc.
        security.setConstraintMappings(Collections.singletonList(mapping));
        security.setAuthenticator(new BasicAuthenticator());
        security.setLoginService(loginService);
        return security;
    }

}