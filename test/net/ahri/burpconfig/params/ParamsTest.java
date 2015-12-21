package net.ahri.burpconfig.params;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ParamsTest
{
    @Test(expected = Params.ParamException.class)
    public void givenInvalidParam_whenParamsParsed_thenThrowsException() throws Exception
    {
        new Params(
                new HashMap<String, Params.Consumer>(),

                "-nope"
        ).parse();
    }

    static Map<String, Params.Consumer> singleConsumer(String param, Params.Consumer consumer)
    {
        final Map<String, Params.Consumer> consumers = new HashMap<String, Params.Consumer>();
        consumers.put(param, consumer);
        return consumers;
    }
}
