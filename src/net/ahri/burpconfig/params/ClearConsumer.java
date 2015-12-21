package net.ahri.burpconfig.params;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class ClearConsumer implements Params.Consumer
{
    private final Preferences prefs;
    private boolean requested;

    public ClearConsumer(Preferences prefs)
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
        return "clears all Burp config and then quits";
    }

    public void clearAndQuitIfRequested() throws BackingStoreException
    {
        if (!requested)
        {
            return;
        }

        for (String key : prefs.keys())
        {
            prefs.remove(key);
        }

        System.out.println("Burp config cleared");

        System.exit(0);
    }
}
