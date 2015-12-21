package net.ahri.burpconfig;

import net.ahri.burpconfig.params.ExtensionConsumer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExtensionConfigTest
{
    @Test
    public void encodeJar() throws Exception
    {
        assertEquals("PGVjPjx0PgAAAAABPC90PjxmPgMAAAAIL2Zvby5qYXI8L2Y+PG4+AwAAAAdmb28uamFyPC9uPjxvbz4AAAAAADwvb28+PGVvPgAAAAAAPC9lbz48bD4CATwvbD48Yj4CADwvYj48L2VjPg==", new ExtensionConsumer.ExtensionConfig("/foo.jar").encode());
    }
}