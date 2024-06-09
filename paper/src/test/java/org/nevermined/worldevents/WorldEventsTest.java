package org.nevermined.worldevents;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.inject.Guice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nevermined.worldevents.config.modules.ConfigModule;
import org.nevermined.worldevents.expansions.modules.ExpansionModule;

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

        @Test
        void ExpansionModuleTest() {
            assertDoesNotThrow(() -> Guice.createInjector(new ExpansionModule()));
        }

    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }

}
