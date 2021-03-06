package org.codehaus.plexus.spe.action;

import org.codehaus.plexus.action.AbstractAction;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.action.Action" role-hint="set-context"
 */
public class SetContextAction
    extends AbstractAction
{
    private String contextKey;

    private String contextValue;

    public void execute( Map context )
        throws Exception
    {
        context.put( contextKey, contextValue );
    }
}
