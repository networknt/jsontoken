/**
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package net.oauth.jsontoken;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.util.Map;


/**
 * Some utility functions for {@link JsonToken}s.
 */
public class JsonTokenUtil {
  static ObjectMapper mapper = new ObjectMapper();
  static public final String DELIMITER = ".";

  public static String toBase64(Map<String, Object> json) {
    return convertToBase64(toJson(json));
  }

  public static String toJson(Map<String, Object> json) {
      String v = null;
      try {
          v = mapper.writeValueAsString(json);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return v;
  }

  public static String convertToBase64(String source) {
    return Base64.encodeBase64URLSafeString(StringUtils.getBytesUtf8(source));
  }
  
  public static String decodeFromBase64String(String encoded) {
    return new String(Base64.decodeBase64(encoded));
  }
  
  public static String fromBase64ToJsonString(String source) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(source));
  }
  
/*
  public static String toDotFormat(String... parts) {
    return Joiner.on('.').useForNull("").join(parts);
  }
*/

    public static String toDotFormat(String... parts) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (String n : parts) {
            sb.append(prefix);
            prefix = ".";
            if(n == null) {
                n = "";
            }
            sb.append(n);
        }
       return sb.toString();
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }
    /**
     * Ensures that an object reference passed as a parameter to the calling
     * method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will
     *     be converted to a string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
    /**
     * Determines whether two possibly-null objects are equal. Returns:
     *
     * <ul>
     * <li>{@code true} if {@code a} and {@code b} are both null.
     * <li>{@code true} if {@code a} and {@code b} are both non-null and they are
     *     equal according to {@link Object#equals(Object)}.
     * <li>{@code false} in all other situations.
     * </ul>
     *
     * <p>This assumes that any non-null objects passed to this function conform
     * to the {@code equals()} contract.
     */
    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    private JsonTokenUtil() { }
}
