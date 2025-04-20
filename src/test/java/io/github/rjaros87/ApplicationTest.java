package io.github.rjaros87;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.util.List;

class ApplicationTest {

    static Logger logger = (Logger) LoggerFactory.getLogger(Application.class);
    static ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

    @BeforeAll
    static void beforeAll() {
        logger.addAppender(listAppender);
        listAppender.start();
    }

    @AfterAll
    static void tearDown() {
        listAppender.list.clear();
    }

    @Test
    void testMicronautVersionIsLoaded() {
        Micronaut micronautMock = Mockito.mock(Micronaut.class);

        try (MockedStatic<Micronaut> micronautMockStatic = Mockito.mockStatic(Micronaut.class)) {
            ApplicationContext contextMock = Mockito.mock(ApplicationContext.class);

            micronautMockStatic.when(() -> Micronaut.build(Mockito.any(String[].class))).thenReturn(micronautMock);
            Mockito.when(micronautMock.mainClass(Application.class)).thenReturn(micronautMock);
            Mockito.when(micronautMock.banner(false)).thenReturn(micronautMock);
            Mockito.when(micronautMock.start()).thenReturn(contextMock);

            Application.main(new String[0]);

            List<ILoggingEvent> logsList = listAppender.list;
            boolean containsVersionLog = logsList.stream()
                    .anyMatch(event -> event.getFormattedMessage().contains("Micronaut version:"));
            boolean containsUnknownVersionLog = logsList.stream()
                    .anyMatch(event -> event.getFormattedMessage().contains("Micronaut version: Unknown"));

            Assertions.assertTrue(containsVersionLog, "Expected log to contain Micronaut version");
            Assertions.assertFalse(containsUnknownVersionLog, "Log should not contain 'Unknown' version");

            micronautMockStatic.verify(() -> Micronaut.build(Mockito.any(String[].class)));
            Mockito.verify(micronautMock).mainClass(Application.class);
            Mockito.verify(micronautMock).banner(false);
            Mockito.verify(micronautMock).start();
        }
    }
}
