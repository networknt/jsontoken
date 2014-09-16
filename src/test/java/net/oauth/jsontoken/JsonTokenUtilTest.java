package net.oauth.jsontoken;

/**
 * Created by steve on 13/09/14.
 */
public class JsonTokenUtilTest extends JsonTokenTestBase {
    public void testToDotFormat() {
        String result = "String1.String2..String3";
        assertEquals(result, JsonTokenUtil.toDotFormat("String1", "String2", null, "String3"));
    }
}
