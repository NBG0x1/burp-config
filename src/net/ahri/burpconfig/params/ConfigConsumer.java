package net.ahri.burpconfig.params;

import java.util.ArrayList;
import java.util.List;

public class ConfigConsumer implements Params.Consumer
{
    private final Params.Platform platform;
    private final ConfigReadWriter configReadWriter;

    private List<String> configFilenames = new ArrayList<String>();

    public ConfigConsumer(Params.Platform platform, ConfigReadWriter configReadWriter)
    {
        this.platform = platform;
        this.configReadWriter = configReadWriter;
    }

    @Override
    public int consume(int i, String[] args)
    {
        if (Params.isOutOfBounds(i, args))
        {
            throw new Params.ParamException("expected config file");
        }

        if (!platform.exists(args[i]))
        {
            throw new Params.ParamException(args[i] + " does not exist");
        }

        configFilenames.add(args[i]);

        return 1;
    }

    @Override
    public String getDescription()
    {
        return "writes config stored in a file to Burp prefs, can be called multiple times";
    }

    public void write()
    {
        if (configFilenames == null)
        {
            return;
        }

        for (String configFilename : configFilenames)
        {
            final ConfigReadWriter.LineReader lineReader = configReadWriter.read(configFilename);

            while (true)
            {
                final String line = lineReader.readLine();
                if (line == null)
                {
                    break;
                }

                configReadWriter.write(parseLine(line));
            }
        }
    }

    private static ConfigReadWriter.Pair parseLine(String line) throws ParseException
    {
        final int colon = line.indexOf(':');
        try
        {
            return new ConfigReadWriter.Pair(line.substring(0, colon), line.substring(colon + 1).trim());
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new ParseException("invalid line format: " + line);
        }
    }


    public interface ConfigReadWriter
    {
        LineReader read(String filename);

        void write(Pair pair);

        class Pair
        {
            public final String key;
            public final String value;

            public Pair(String key, String value)
            {
                this.key = key;
                this.value = value;
            }
        }

        interface LineReader
        {
            String readLine();
        }
    }

    public static class ParseException extends RuntimeException
    {
        public ParseException(String message)
        {
            super(message);
        }
    }
}
