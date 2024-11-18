package com.kds.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MockTests {

//    @Mock
//    private ServiceProperties serviceProperties;
//
//    @InjectMocks
//    private MyService myService;

//    @Test
//    public void testMockMessae() {
//        when(serviceProperties.getMessage()).thenReturn("Mocked Hello! World");
//
//        String result = myService.message();
//        Assertions.assertEquals("Mocked Hello! World", result);
//    }

    @Test
    public void testAllAssertions() {
        Assertions.assertAll("Testing multiple assertions",
                () -> Assertions.assertEquals("Hello", "Hello"),
                () -> Assertions.assertTrue(5 > 3),
                () -> Assertions.assertNull(null)
        );
    }
}
