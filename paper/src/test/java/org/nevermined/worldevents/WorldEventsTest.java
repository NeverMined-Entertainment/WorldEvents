package org.nevermined.worldevents;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.inject.Guice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nevermined.worldevents.config.modules.ConfigModule;

import static org.junit.jupiter.api.Assertions.*;

public class WorldEventsTest {

    @BeforeEach
    public void setUp()
    {
        MockBukkit.mock();
    }

    @Nested
    class GuiceInjectorTest {

        @Test
        void ConfigModuleTest() {
            assertDoesNotThrow(() -> Guice.createInjector(new ConfigModule()));
        }

    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }

}
