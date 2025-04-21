package io.github.rjaros87;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

@DisabledInNativeImage
class ApplicationTest {

    private static TestAppender testAppender;

    @BeforeAll
    static void beforeAll() {
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();

        testAppender = new TestAppender("TestListAppender");
        testAppender.start();

        LoggerConfig loggerConfig = config.getLoggerConfig("io.github.rjaros87");
        loggerConfig.addAppender(testAppender, null, null);
        context.updateLoggers();
    }

    @AfterAll
    static void tearDown() {
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();

        LoggerConfig loggerConfig = config.getLoggerConfig("io.github.rjaros87");
        loggerConfig.removeAppender("TestListAppender");
        testAppender.stop();
        context.updateLoggers();
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

            List<String> logsList = testAppender.getLogEvents().stream()
                    .map(event -> event.getMessage().getFormattedMessage())
                    .toList();

            boolean containsVersionLog = logsList.stream()
                    .anyMatch(message -> message.contains("Micronaut version:"));
            boolean containsUnknownVersionLog = logsList.stream()
                    .anyMatch(message -> message.contains("Micronaut version: Unknown"));

            Assertions.assertTrue(containsVersionLog, "Expected log to contain Micronaut version");
            Assertions.assertFalse(containsUnknownVersionLog, "Log should not contain 'Unknown' version");

            micronautMockStatic.verify(() -> Micronaut.build(Mockito.any(String[].class)));
            Mockito.verify(micronautMock).mainClass(Application.class);
            Mockito.verify(micronautMock).banner(false);
            Mockito.verify(micronautMock).start();
        }
    }

    private static class TestAppender extends AbstractAppender {

        private final List<LogEvent> logEvents = new ArrayList<>();

        protected TestAppender(String name) {
            super(name, null, PatternLayout.createDefaultLayout(), false, null);
        }

        @Override
        public void append(LogEvent event) {
            logEvents.add(event.toImmutable());
        }

        public List<LogEvent> getLogEvents() {
            return logEvents;
        }
    }
}
