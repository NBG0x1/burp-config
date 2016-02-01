package net.ahri.burpconfig;

import net.ahri.burpconfig.params.ClearConsumer;
import net.ahri.burpconfig.params.ConfigConsumer;
import net.ahri.burpconfig.params.DumpConsumer;
import net.ahri.burpconfig.params.ExtensionConsumer;
import net.ahri.burpconfig.params.Params;
import net.ahri.burpconfig.params.ScopeExcludeConsumer;
import net.ahri.burpconfig.params.ScopeIncludeConsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main
{
    public static void main(String[] args) throws BackingStoreException, IOException, ParseException
    {
        final Preferences prefs = Preferences.userRoot().node("burp");

        final Map<String, Params.Consumer> map = new TreeMap<String, Params.Consumer>();
        final Params.Platform platform = new Params.Platform()
        {
            @Override
            public boolean exists(String filename)
            {
                return new File(filename).exists();
            }
        };

        final ConfigConsumer configConsumer = new ConfigConsumer(
                platform,
                new ConfigReadFileWritePrefs(prefs)
        );

        final ExtensionConsumer extensionConsumer = new ExtensionConsumer(
                platform,
                new PrefsWriter(prefs)
        );

        final ScopeIncludeConsumer scopeIncludeConsumer = new ScopeIncludeConsumer(
                new PrefsWriter(prefs)
        );

        final ScopeExcludeConsumer scopeExcludeConsumer = new ScopeExcludeConsumer(
                new PrefsWriter(prefs)
        );

        final DumpConsumer dumpConsumer = new DumpConsumer(prefs);
        final ClearConsumer clearConsumer = new ClearConsumer(prefs);

        map.put("-c", configConsumer);
        map.put("-e", extensionConsumer);
        map.put("-i", scopeIncludeConsumer);
        map.put("-x", scopeExcludeConsumer);
        map.put("-dump", dumpConsumer);
        map.put("-clear", clearConsumer);

        if (args.length == 0)
        {
            System.err.println("Usage:");
            for (Map.Entry<String, Params.Consumer> entry : map.entrySet())
            {
                System.err.printf("    %s: %s\n", entry.getKey(), entry.getValue().getDescription());

            }
            System.exit(1);
        }

        try
        {
            new Params(map, args).parse();
        }
        catch (Params.ParamException e)
        {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        dumpConsumer.dumpAndQuitIfRequested();
        clearConsumer.clearAndQuitIfRequested();

        configConsumer.write();
        extensionConsumer.writeExtensions();
        scopeIncludeConsumer.writeScopes();
        scopeExcludeConsumer.writeScopes();
    }

    private static class ConfigReadFileWritePrefs implements ConfigConsumer.ConfigReadWriter
    {
        private final PrefsWriter writer;

        public ConfigReadFileWritePrefs(Preferences prefs)
        {
            this.writer = new PrefsWriter(prefs);
        }

        @Override
        public LineReader read(String filename)
        {
            try
            {
                final BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

                return new LineReader()
                {
                    @Override
                    public String readLine()
                    {
                        try
                        {
                            return stream.readLine();
                        }
                        catch (IOException ignored)
                        {
                            return null;
                        }
                    }
                };
            }
            catch (FileNotFoundException ignored)
            {
                return null;
            }
        }

        @Override
        public void write(Pair pair)
        {
            writer.write(pair.key, pair.value);
        }
    }

    private static class PrefsWriter implements KvpWriter
    {
        private final Preferences prefs;

        public PrefsWriter(Preferences prefs)
        {
            this.prefs = prefs;
        }

        @Override
        public void write(String key, String value)
        {
            prefs.put(key, value);
        }
    }
}
