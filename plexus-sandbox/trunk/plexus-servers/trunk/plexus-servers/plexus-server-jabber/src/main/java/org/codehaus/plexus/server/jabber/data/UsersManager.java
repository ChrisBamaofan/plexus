/*
 * BSD License http://open-im.org/bsd-license.html
 * Copyright (c) 2003, OpenIM Project http://open-im.org
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the OpenIM project. For more
 * information on the OpenIM project, please see
 * http://open-im.org/
 */
package org.codehaus.plexus.server.jabber.data;


import org.codehaus.plexus.server.jabber.data.jabber.User;

/**
 * @author AlAg
 * @version 1.0
 */
public interface UsersManager
{
    public User getNewUser()
        throws Exception;
}

