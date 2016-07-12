package it.org.catrobat.estimationplugin;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class MyComponentTrdTest
{
    @Ignore
    @Test
    public void testSomeFailure()
    {
        System.out.println("I RAN But failed...");
        assertEquals("something failed","blah","boo");
    }
}
