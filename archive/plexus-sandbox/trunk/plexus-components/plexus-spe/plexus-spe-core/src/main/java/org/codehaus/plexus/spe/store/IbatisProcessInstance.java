package org.codehaus.plexus.spe.store;

import org.codehaus.plexus.spe.model.ProcessInstance;
import org.codehaus.plexus.spe.ProcessException;
import org.codehaus.plexus.util.IOUtil;

import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class IbatisProcessInstance
    extends ProcessInstance
{
    /**
     * Set to 1 if the context bytes are set.
     */
    private boolean contextExists;

    private byte[] contextBytes;

    public IbatisProcessInstance()
    {
    }

    public IbatisProcessInstance( String processId )
    {
        super( null, processId );
    }

    public IbatisProcessInstance( ProcessInstance processInstance )
    {
        super( processInstance.getId(),  processInstance.getProcessId() );
        super.setCreatedTime( processInstance.getCreatedTime() );
        super.setEndTime( processInstance.getEndTime() );
        super.setErrorMessage( processInstance.getErrorMessage() );
        super.setSteps( processInstance.getSteps() );
        setContext( processInstance.getContext() );
    }

    public boolean isContextSet()
    {
        return contextExists;
    }

    public void setContextExists( boolean contextExists )
    {
        this.contextExists = contextExists;
    }

    public byte[] getContextBytes()
    {
        return contextBytes;
    }

    public void setContextBytes( byte[] contextBytes )
    {
        this.contextBytes = contextBytes;
    }

    public void setContext( Map<String, Serializable> map )
    {
        super.setContext( map );

        contextExists = true;
    }

    public Map<String, Serializable> getContext()
    {
        if ( !contextExists )
        {
            throw new RuntimeException( "This method can only be called on a full object." );
        }

        return super.getContext();
    }

    public void serialize()
        throws ProcessException
    {
        if ( !contextExists )
        {
            throw new ProcessException( "Internal error: no context to deserialize." );
        }

        ByteArrayOutputStream os = null;
        ObjectOutputStream oos = null;

        try
        {
            os = new ByteArrayOutputStream( 1024 * 1024 );
            oos = new ObjectOutputStream( os );
            oos.writeObject( super.getContext() );
            oos.close();

            contextBytes = os.toByteArray();
        }
        catch ( IOException e )
        {
            throw new ProcessException( "Error while serializing process context. Make sure all objects in the context implement Serializable!", e );
        }
        finally
        {
            IOUtil.close( os );
            IOUtil.close( oos );
        }
    }

    public void deserialize()
        throws ProcessException
    {
        if ( !contextExists )
        {
            throw new ProcessException( "Internal error: no context to deserialize." );
        }

        ObjectInputStream is = null;

        try
        {
            is = new ObjectInputStream( new ByteArrayInputStream( contextBytes ) );

            setContext( (Map<String, Serializable>) is.readObject() );
        }
        catch ( IOException e )
        {
            throw new ProcessException( "Error while deserializing process context.", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new ProcessException( "Error while deserializing process context.", e );
        }
        finally
        {
            IOUtil.close( is );
        }
    }
}
