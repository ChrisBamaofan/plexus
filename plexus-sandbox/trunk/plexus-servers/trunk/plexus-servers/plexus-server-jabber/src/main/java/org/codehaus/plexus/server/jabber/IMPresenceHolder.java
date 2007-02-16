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
package org.codehaus.plexus.server.jabber;

import java.util.Collection;

import org.codehaus.plexus.server.jabber.data.jabber.IMPresence;


/**
 * @author AlAg
 * @author PV
 * @version 1.0
 */
public interface IMPresenceHolder
{
    public void setPresence( String jidAndRessource,
                             IMPresence presence );

    public Collection getPresence( String jid );

    public IMPresence removePresence( String jidAndRessource );

    public void registerListener( IMPresenceListener listener );

    public void unregisterListener( IMPresenceListener listener );
}

