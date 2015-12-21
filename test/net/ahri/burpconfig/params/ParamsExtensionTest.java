package net.ahri.burpconfig.params;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ParamsExtensionTest
{
    private static final ExtensionConsumer.ExtensionWriter NULL_EXTENSION_WRITER = new ExtensionConsumer.ExtensionWriter()
    {
        @Override
        public void write(String key, String value)
        {
        }
    };

    @Test(expected = Params.ParamException.class)
    public void givenNonExistentExtension_whenParamsParsed_thenThrowException() throws Exception
    {
        new Params(
                ParamsTest.singleConsumer("-e", new ExtensionConsumer(new Params.Platform()
                {
                    @Override
                    public boolean exists(String filename)
                    {
                        return false;
                    }
                }, NULL_EXTENSION_WRITER)),

                "-e",
                "foo.jar"
        ).parse();
    }

    @Test(expected = Params.ParamException.class)
    public void givenNoParamAfterExtensionSwitch_whenParamsParsed_thenThrowException() throws Exception
    {
        new Params(
                ParamsTest.singleConsumer("-e", new ExtensionConsumer(new Params.Platform()
                {
                    @Override
                    public boolean exists(String filename)
                    {
                        return true;
                    }
                }, NULL_EXTENSION_WRITER)),

                "-e"
        ).parse();
    }

    @Test
    public void givenNoParamAfterExtensionSwitch_whenParamsParsed_thenExtensionWritten() throws Exception
    {
        final MapExtensionWriter extensionWriter = new MapExtensionWriter();
        final ExtensionConsumer extensionConsumer = new ExtensionConsumer(new Params.Platform()
        {
            @Override
            public boolean exists(String filename)
            {
                return true;
            }
        }, extensionWriter);

        new Params(
                ParamsTest.singleConsumer("-e", extensionConsumer),

                "-e",
                "/foo.jar"
        ).parse();

        extensionConsumer.writeExtensions();

        assertEquals("PGVjPjx0PgAAAAABPC90PjxmPgMAAAAIL2Zvby5qYXI8L2Y+PG4+AwAAAAdmb28uamFyPC9uPjxvbz4AAAAAADwvb28+PGVvPgAAAAAAPC9lbz48bD4CATwvbD48Yj4CADwvYj48L2VjPg==", extensionWriter.output.get("suite.extension0"));
    }

    private static class MapExtensionWriter implements ExtensionConsumer.ExtensionWriter
    {
        public final Map<String, String> output = new HashMap<String, String>();

        @Override
        public void write(String key, String value)
        {
            output.put(key, value);
        }
    }
}
