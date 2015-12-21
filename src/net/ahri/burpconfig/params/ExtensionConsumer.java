package net.ahri.burpconfig.params;

import sun.misc.BASE64Encoder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExtensionConsumer implements Params.Consumer
{
    private final List<String> files = new ArrayList<String>();
    private final Params.Platform platform;
    private final ExtensionWriter extensionWriter;

    public ExtensionConsumer(Params.Platform platform, ExtensionWriter extensionWriter)
    {
        this.platform = platform;
        this.extensionWriter = extensionWriter;
    }

    @Override
    public int consume(int i, String[] args)
    {
        if (Params.isOutOfBounds(i, args))
        {
            throw new Params.ParamException("expected extension file");
        }

        if (!platform.exists(args[i]))
        {
            throw new Params.ParamException(args[i] + " does not exist");
        }

        files.add(args[i]);

        return 1;
    }

    @Override
    public String getDescription()
    {
        return "configures Burp to load the given extension, can be called multiple times";
    }

    public void writeExtensions()
    {
        for (int i = 0; i < files.size(); i++)
        {
            try
            {
                extensionWriter.write("suite.extension" + i, new ExtensionConfig(new File(files.get(0)).getCanonicalPath()).encode());
            }
            catch (IOException e)
            {
            }
        }
    }

    public interface ExtensionWriter
    {
        void write(String key, String value);
    }

    public static class ExtensionConfig
    {
        public static final boolean EXTENSION_LOADED = true;
        public static final boolean NOT_A_BAPP = false;
    
        public static final byte TYPE_LEGACY_JAVA = 0;
        public static final byte TYPE_JAVA = 1;
        public static final byte TYPE_PYTHON = 2;
        public static final byte TYPE_RUBY = 3;
    
        public static final byte OUTPUT_CONSOLE = 0;
        public static final byte OUTPUT_FILE = 1;
        public static final byte OUTPUT_UI = 2;
    
        private final String filename;
    
        public ExtensionConfig(String filename)
        {
            this.filename = filename;
        }
    
        public String encode() throws IOException
        {
            final List<Byte> buffer = new ArrayList<Byte>();
    
            new Tag("ec",
                    new Tag("t", new IntData(getType(filename))),
                    new Tag("f", new StringData(filename)),
                    new Tag("n", new StringData(getJarName(this.filename))),
                    new Tag("oo", new IntData(OUTPUT_CONSOLE)),
                    new Tag("eo", new IntData(OUTPUT_CONSOLE)),
                    new Tag("l", new BoolData(EXTENSION_LOADED)),
                    new Tag("b", new BoolData(NOT_A_BAPP))
            ).write(new DataOutputStream(new OutputStream()
            {
                @Override
                public void write(int b) throws IOException
                {
                    buffer.add((byte) b);
                }
            }));
    
            final byte[] bytes = new byte[buffer.size()];
            for (int i = 0; i < buffer.size(); i++)
            {
                bytes[i] = buffer.get(i);
            }
    
            return new BASE64Encoder()
            {
                @Override
                protected int bytesPerLine()
                {
                    return 10000;
                }
            }.encode(bytes);
        }
    
        private static byte getType(String filename)
        {
            try
            {
                final String extension = filename.substring(filename.lastIndexOf('.') + 1);
    
                if ("jar".equalsIgnoreCase(extension))
                {
                    return TYPE_JAVA;
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                throw new UnrecognizedFiletype(filename);
            }
    
            throw new UnrecognizedFiletype(filename);
        }
    
        private static String getJarName(String filename)
        {
            return filename.substring(filename.lastIndexOf('/') + 1);
        }
    
        private interface Writer
        {
            void write(DataOutputStream os) throws IOException;
    
        }
    
        private static class Tag implements Writer
        {
            private final String name;
    
            private final Writer[] writers;
    
            public Tag(String name, Writer... writers)
            {
                this.name = name;
                this.writers = writers;
            }
    
            @Override
            public void write(DataOutputStream os) throws IOException
            {
                os.writeBytes("<");
                os.writeBytes(name);
                os.writeBytes(">");
    
                for (Writer writer : writers)
                {
                    writer.write(os);
                }
    
                os.writeBytes("</");
                os.writeBytes(name);
                os.writeBytes(">");
            }
    
        }
    
        private static class IntData implements Writer
        {
    
            private final int data;
    
            public IntData(int data)
            {
                this.data = data;
            }
    
            @Override
            public void write(DataOutputStream os) throws IOException
            {
                os.writeByte(0);
                os.writeInt(data);
            }
    
        }
    
        private static class StringData implements Writer
        {
    
            private final String data;
    
            public StringData(String data)
            {
                this.data = data;
            }
    
            @Override
            public void write(DataOutputStream os) throws IOException
            {
                os.writeByte(3);
                final byte[] bytes = data.getBytes("UTF-8");
                os.writeInt(bytes.length);
                os.write(bytes);
            }
        }
    
        private static class BoolData implements Writer
        {
    
            private final boolean data;
    
            public BoolData(boolean data)
            {
                this.data = data;
            }
    
            @Override
            public void write(DataOutputStream os) throws IOException
            {
                os.writeByte(2);
                os.writeBoolean(data);
            }
        }
    
        private static class UnrecognizedFiletype extends RuntimeException
        {
            public UnrecognizedFiletype(String extension)
            {
                super("Unrecognized filetype, should be one of jar, rb, py: " + extension);
            }
        }
    }
}
