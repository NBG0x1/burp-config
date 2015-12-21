package net.ahri.burpconfig.params;

import java.util.Map;

public class Params
{
    private final Map<String, Consumer> consumers;
    private final String[] args;

    public Params(Map<String, Consumer> consumers, String... args)
    {

        this.consumers = consumers;
        this.args = args;
    }

    public void parse()
    {
        for (int i = 0; i < args.length; i++)
        {
            final Consumer consumer = consumers.get(args[i]);

            if (consumer == null)
            {
                throw new ParamException("unexpected param " + args[i]);
            }

            i += consumer.consume(i + 1, args);
        }
    }

    static boolean isOutOfBounds(int i, String[] args)
    {
        return i + 1 > args.length;
    }

    public static class ParamException extends RuntimeException
    {
        public ParamException(String message)
        {
            super(message);
        }
    }

    public interface Consumer
    {
        int consume(int i, String[] args);

        String getDescription();
    }

    public interface Platform
    {
        boolean exists(String filename);
    }
}
