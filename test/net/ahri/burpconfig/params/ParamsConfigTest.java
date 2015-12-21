package net.ahri.burpconfig.params;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ParamsConfigTest
{
    private static final Params.Consumer NULL_CONSUMER = new Params.Consumer()
    {
        @Override
        public int consume(int i, String[] args)
        {
            return 0;
        }

        @Override
        public String getDescription()
        {
            return "";
        }
    };

    private static final ConfigConsumer.ConfigReadWriter NULL_CONFIG_READ_WRITER = new ConfigConsumer.ConfigReadWriter()
    {
        @Override
        public LineReader read(String filename)
        {
            return new LineReader()
            {
                @Override
                public String readLine()
                {
                    return null;
                }
            };
        }

        @Override
        public void write(Pair pair)
        {
        }
    };

    @Test(expected = Params.ParamException.class)
    public void givenDuplicateConfig_whenParamsParsed_thenThrowException() throws Exception
    {
        new Params(
                ParamsTest.singleConsumer("-c", new ConfigConsumer(new Params.Platform()
                {
                    @Override
                    public boolean exists(String filename)
                    {
                        return true;
                    }
                }, NULL_CONFIG_READ_WRITER)),

                "-c",
                "config",
                "-c"
        ).parse();
    }

    @Test(expected = Params.ParamException.class)
    public void givenNonExistentConfig_whenParamsParsed_thenThrowException() throws Exception
    {
        new Params(
                ParamsTest.singleConsumer("-c", new ConfigConsumer(new Params.Platform()
                {
                    @Override
                    public boolean exists(String filename)
                    {
                        return false;
                    }
                }, NULL_CONFIG_READ_WRITER)),

                "-c",
                "non-existent-config"
        ).parse();
    }

    @Test(expected = Params.ParamException.class)
    public void givenNoParamAfterConfigSwitch_whenParamsParsed_thenThrowException() throws Exception
    {
        new Params(
                ParamsTest.singleConsumer("-c", new ConfigConsumer(new Params.Platform()
                {
                    @Override
                    public boolean exists(String filename)
                    {
                        return false;
                    }
                }, NULL_CONFIG_READ_WRITER)),

                "-c"
        ).parse();
    }

    @Test
    public void givenValidConfigFile_whenParamsParsed_thenConfigConsumerHasConfigFile() throws Exception
    {
        final ConfigListReadWriterMapWriter configReadWriter = new ConfigListReadWriterMapWriter("foo: bar");

        final ConfigConsumer config = new ConfigConsumer(
                new Params.Platform()
                {
                    @Override
                    public boolean exists(String filename)
                    {
                        return true;
                    }
                },
                configReadWriter
        );

        new Params(
                ParamsTest.singleConsumer("-c", config),

                "-c",
                "config"
        ).parse();

        config.write();

        assertEquals("bar", configReadWriter.output.get("foo"));
    }

    private static class ConfigListReadWriterMapWriter implements ConfigConsumer.ConfigReadWriter
    {
        private final List<String> input;

        public Map<String, String> output = new HashMap<String, String>();

        public ConfigListReadWriterMapWriter(String... input)
        {
            this.input = new ArrayList<String>();
            Collections.addAll(this.input, input);
        }

        @Override
        public LineReader read(String filename)
        {
            return new LineReader()
            {
                @Override
                public String readLine()
                {
                    if (input.isEmpty())
                    {
                        return null;
                    }

                    return input.remove(0);
                }
            };
        }

        @Override
        public void write(Pair pair)
        {
            output.put(pair.key, pair.value);
        }
    }
}