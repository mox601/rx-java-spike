package org.mox.spikes.rx;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class FunctionalTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalTestCase.class);
    private Database database;
    private Object data;
    private List<Object> someData;

    @BeforeMethod
    public void setUp() throws Exception {

        database = new DefaultDatabase();
        data = new Object();
        someData = asList(new Object(), new Object());

    }

    @Test
    public void testFunctionAsInstance() throws Exception {

        final DatabaseWriter databaseWriter = new DatabaseWriter(database);

        //one
        try {
            databaseWriter.apply(data);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        //more
        Iterables.transform(someData, databaseWriter);

        //more map
        Collections2.transform(someData, databaseWriter);

    }

    @Test
    public void testFunctionAsStatic() throws Exception {

        //one
        DatabaseSaver.save(database, data);

        //more
        for (Object aData : someData) {
            DatabaseSaver.save(database, aData);
        }

    }

    private static class DatabaseWriter implements Function<Object, Void> {

        private final Database database;

        public DatabaseWriter(final Database database) {

            this.database = database;

        }

        @Override
        public Void apply(final Object data) {

            this.database.save(data);
            return null;
        }
    }

    private static interface Database {

        public void save(Object data);

    }

    private static class DefaultDatabase implements Database {

        @Override
        public void save(Object data) {

        }
    }

    private static class DatabaseSaver {

        private DatabaseSaver() {

        }

        public static void save(Database database, Object data) {

            database.save(data);

        }
    }
}
