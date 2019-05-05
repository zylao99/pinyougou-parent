package com.pinyougou;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void test(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(date);
        System.out.println(format);


        Calendar rightNow  = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow .add(Calendar.MONTH,-3);
        Date time = rightNow.getTime();
        String format1 = dateFormat.format(time);
        System.out.println(format1);


        if (date.getTime()>time.getTime()){
            System.out.println(date.getTime() - time.getTime());
        }
    }
}
