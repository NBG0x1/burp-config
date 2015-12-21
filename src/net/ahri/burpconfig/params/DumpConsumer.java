package net.ahri.burpconfig.params;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class DumpConsumer implements Params.Consumer
{
    private final Preferences prefs;
    private boolean requested;

    public DumpConsumer(Preferences prefs)
    {
        this.prefs = prefs;
    }

    @Override
    public int consume(int i, String[] args)
    {
        requested = true;
        return 0;
    }

    @Override
    public String getDescription()
    {
        return "dumps all Burp config and then quits";
    }

    public void dumpAndQuitIfRequested() throws BackingStoreException
    {
        if (!requested)
        {
            return;
        }

        for (String key : prefs.keys())
        {
            System.out.printf("%s: %s\n", key, prefs.get(key, ""));
        }

        System.exit(0);
    }
}
