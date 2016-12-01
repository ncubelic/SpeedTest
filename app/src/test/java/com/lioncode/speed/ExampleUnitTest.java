package com.lioncode.speed;

import com.lioncode.speed.com.lioncode.speed.service.PingService;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void arpTest() throws Exception {
        PingService service = new PingService();
        String result = service.arp();
        assertNotEquals("0",result);
    }
}