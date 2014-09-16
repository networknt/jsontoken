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
package net.oauth.jsontoken.discovery;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.oauth.jsontoken.crypto.MagicRsaPublicKey;

import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the {@link ServerInfo} interface that assumes the
 * server info document is in JSON format. It can parse such a JSON-formatted
 * server info document and exposes its contents through the requisite
 * methods of the {@link ServerInfo} interface.
 */
public class JsonServerInfo implements ServerInfo {
  static ObjectMapper mapper = new ObjectMapper();

  @JsonProperty("verification_keys")
  private final Map<String, String> verificationKeys = new LinkedHashMap<String, String>();

  /**
   * Parses a JSON-formatted server info document and returns it as a
   * {@link JsonServerInfo} object.
   * @param json the contents of the JSON-formatted server info document.
   */
  public static JsonServerInfo getDocument(String json) {
      JsonServerInfo jsi = null;
      try {
          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          jsi = mapper.readValue(json, JsonServerInfo.class);
      } catch (Exception e) {
          e.printStackTrace();
      }
      return jsi;
  }

  /*
   * (non-Javadoc)
   * @see net.oauth.jsontoken.discovery.ServerInfo#getVerificationKey(java.lang.String)
   */
  @Override
  public PublicKey getVerificationKey(String keyId) {
    String magicKey = verificationKeys.get(keyId);
    if (magicKey == null) {
      return null;
    } else {
      return new MagicRsaPublicKey(magicKey).getKey();
    }
  }
}
