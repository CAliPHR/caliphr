package com.ainq.caliphr.website.format;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ainq.caliphr.common.util.format.DateTimeFormat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DateTimeFormat.class)
public class DateTimeFormatTest {

    @Test
    public void testHumanElapsedTime() {
        mockStatic(DateTimeFormat.class);
        String expectedResult = null;
        Long elapsedTime = System.currentTimeMillis();
        expect(DateTimeFormat.humanElapsedTime(elapsedTime)).andReturn(expectedResult);
        replay(DateTimeFormat.class);
        String actualResult = DateTimeFormat.humanElapsedTime(elapsedTime);
        verify(DateTimeFormat.class);
        assertEquals(expectedResult, actualResult);
    }

}
