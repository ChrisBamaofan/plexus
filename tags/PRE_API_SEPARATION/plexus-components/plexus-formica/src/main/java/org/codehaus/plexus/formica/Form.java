package org.codehaus.plexus.formica;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class Form
    extends Identifiable
{
    private List elements;

    private boolean skipNullValues;

    private boolean validateNullValues;

    private List elementGroups;

    private String targetClass;

    private String keyExpression;

    // ----------------------------------------------------------------------
    // Mode descriptors
    // ----------------------------------------------------------------------

    private Add add;

    private Update update;

    private View view;

    private Delete delete;

    private Summary summary;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private String sourceRole;

    private String lookupExpression;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void addElement( Element element )
    {
        elements.add( element );
    }

    public List getElements()
    {
        return elements;
    }

    public List getElementGroups()
    {
        return elementGroups;
    }


    public void setSkipNullValues( boolean skipNullValues )
    {
        this.skipNullValues = skipNullValues;
    }

    public boolean skipNullValues()
    {
        return skipNullValues;
    }

    public void setValidateNullValues( boolean validateNullValues )
    {
        this.validateNullValues = validateNullValues;
    }

    public boolean validateNullValues()
    {
        return validateNullValues;
    }

    public String getTargetClass()
    {
        return targetClass;
    }

    public void setTargetClass( String targetClass )
    {
        this.targetClass = targetClass;
    }

    //!! The form should just be initialized to create a Map

    public Element getElement( String id )
    {
        Element retValue = null;

        for ( Iterator iterator = elements.iterator(); iterator.hasNext(); )
        {
            Element element = (Element) iterator.next();

            if ( element.getId().equals( id ) )
            {
                retValue = element;

                break;
            }
        }
        return retValue;
    }

    public ElementGroup getElementGroup( String id )
    {
        ElementGroup retValue = null;

        for ( Iterator iterator = elementGroups.iterator(); iterator.hasNext(); )
        {
            ElementGroup group = (ElementGroup) iterator.next();

            if ( group.getId().equals( id ) )
            {
                retValue = group;

                break;
            }
        }

        return retValue;
    }

    public String getKeyExpression()
    {
        return keyExpression;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getSourceRole()
    {
        return sourceRole;
    }

    public String getLookupExpression()
    {
        return lookupExpression;
    }

    // ----------------------------------------------------------------------
    // Mode descriptors
    // ----------------------------------------------------------------------

    public Add getAdd()
    {
        return add;
    }

    public Update getUpdate()
    {
        return update;
    }

    public View getView()
    {
        return view;
    }

    public Delete getDelete()
    {
        return delete;
    }

    public Summary getSummary()
    {
        return summary;
    }
}