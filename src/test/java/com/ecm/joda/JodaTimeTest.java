package com.ecm.joda;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Bourne Qian
 * Date: 16-5-18
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
public class JodaTimeTest {

    @Test
    public void newDateTest(){
        Date firstDayOf2013 = new Date(113, 0, 1);

        DateTime firstDayOf20131 = new DateTime().withDate(2013, 1, 1).withTime(0, 0 , 0 , 0);

        assertEquals(firstDayOf2013, firstDayOf20131.toDate());
    }
}
