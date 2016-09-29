package com.ainq.caliphr.website.format;

import static org.powermock.api.easymock.PowerMock.mockStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ainq.caliphr.common.util.format.FileSizeFormat;

/**
 * Created by mmelusky on 5/6/2015.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FileSizeFormat.class)
public class FileSizeFormatTest {

    /*
        TODO have a better test case here.
     */
    @Test
    public void testFileSizeFormatStaticMethods() {
        mockStatic(FileSizeFormat.class);
    }
}
