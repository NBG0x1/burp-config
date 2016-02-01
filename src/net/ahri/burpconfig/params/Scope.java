package net.ahri.burpconfig.params;

class Scope
{
    private final int type;
    private final String host;
    private final String port;
    private final String path;

    public Scope(String scope)
    {
        final String[] parts = scope.split(",");

        if (parts.length != 4)
        {
            throw new Params.ParamException("Invalid scope specification; should be format https|http|any,host,port,path where only one of http, https or any can be specified. Supplied format: " + scope);
        }

        if ("http".equals(parts[0]))
        {
            type = 1;
        }
        else if ("https".equals(parts[0]))
        {
            type = 2;
        }
        else if ("any".equals(parts[0]))
        {
            type = 0;
        }
        else
        {
            throw new Params.ParamException("Invalid scope specification; should be format https|http|any,host,port,path where only one of http, https or any can be specified. Supplied format: " + scope);
        }

        host = parts[1];
        port = parts[2];
        path = parts[3];
    }

    public String toString()
    {
        return String.format("%d.%d.%d.%s%d.%s%s", Integer.toString(type).length(), type, host.length(), host, port.length(), port, path);
    }
}
