/*
 * 
 * 
 * Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.  
 * Contributors retain copyright to elements licensed under a Contributor Agreement.
 * Licensed to the User under the LGPL license.
 * 
 * $
 */
package org.sipfoundry.sipxconfig.site.user;

import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.html.BasePage;
import org.sipfoundry.sipxconfig.common.CoreContext;
import org.sipfoundry.sipxconfig.components.TapestryUtils;
import org.sipfoundry.sipxconfig.site.LoginPage;

public abstract class FirstUser extends BasePage implements PageBeginRenderListener {
    public static final String PAGE = "FirstUser";

    public abstract CoreContext getCoreContext();
    
    public abstract String getPin();
    public abstract void setPin(String pin);

    public void pageBeginRender(PageEvent event) {
        // This page runs only when there are no users, and the first user
        // needs to be created.  If a user exists, then bail out to the login page.
        // After we create the user, we'll land here and go to login.
        if (getCoreContext().getUsersCount() != 0) {
            LoginPage loginPage = (LoginPage) event.getRequestCycle().getPage(LoginPage.PAGE);
            throw new PageRedirectException(loginPage);
        }
    }

    public void commit() {
        if (TapestryUtils.isValid(this)) {            
            // Create the superadmin user
            getCoreContext().createAdminGroupAndInitialUser(getPin());
        }
    }
}
