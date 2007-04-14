/*
 * 
 * 
 * Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.  
 * Contributors retain copyright to elements licensed under a Contributor Agreement.
 * Licensed to the User under the LGPL license.
 * 
 * $
 */
package org.sipfoundry.sipxconfig.site.common;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.sipfoundry.sipxconfig.components.TapestryUtils;

/*
 * Date and time picker
 */
@ComponentClass(allowBody = false, allowInformalParameters = false)
public abstract class DateTimeEditor extends BaseComponent {
    private static final int[] TIME_FIELDS = {
        Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND
    };

    @Parameter(required = true)
    public abstract Date getDatetime();

    public abstract void setDatetime(Date datetime);

    @Parameter(required = true)
    public abstract String getLabel();

    public abstract Date getTime();

    public abstract void setTime(Date time);

    public abstract Date getDate();

    public abstract void setDate(Date date);

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {
        Date datetime = getDatetime();
        setDate(new Date(datetime.getTime()));
        setTime(new Date(datetime.getTime()));

        super.renderComponent(writer, cycle);
        if (TapestryUtils.isRewinding(cycle, this) && TapestryUtils.isValid(this)) {
            datetime = toDateTime(getDate(), getTime(), getPage().getLocale());
            setDatetime(datetime);
        }
    }

    public static Date toDateTime(Date date, Date time, Locale locale) {
        Calendar calDate = Calendar.getInstance(locale);
        calDate.setTime(date);

        Calendar calTime = Calendar.getInstance(locale);
        calTime.setTime(time);

        for (int field : TIME_FIELDS) {
            calDate.set(field, calTime.get(field));
        }

        Date datetime2 = calDate.getTime();
        return datetime2;
    }
}
