/*
 * Copyright 2014 the original author or authors.
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
 */
package com.google.appengine.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Copied and modified from org.gradle.api.internal.artifacts.ivyservice.ivyresolve.strategy.ExactVersionMatcher,
 * because it's an internal utility and we can't have any expectation that the API remains the same.
 */
public class VersionComparator {

    private static final Map<String, Integer> SPECIAL_MEANINGS;
    static {
        SPECIAL_MEANINGS = new HashMap<String, Integer>();
        SPECIAL_MEANINGS.put("dev", -1);
        SPECIAL_MEANINGS.put("rc", 1);
        SPECIAL_MEANINGS.put("final", 2);
    }

    public static int compare(String selector, String candidate) {
        if (selector.equals(candidate)) {
            return 0;
        }

        selector = selector.replaceAll("([a-zA-Z])(\\d)", "$1.$2");
        selector = selector.replaceAll("(\\d)([a-zA-Z])", "$1.$2");
        candidate = candidate.replaceAll("([a-zA-Z])(\\d)", "$1.$2");
        candidate = candidate.replaceAll("(\\d)([a-zA-Z])", "$1.$2");

        String[] parts1 = selector.split("[\\._\\-\\+]");
        String[] parts2 = candidate.split("[\\._\\-\\+]");

        int i = 0;
        for (; i < parts1.length && i < parts2.length; i++) {
            if (parts1[i].equals(parts2[i])) {
                continue;
            }
            boolean is1Number = isNumber(parts1[i]);
            boolean is2Number = isNumber(parts2[i]);
            if (is1Number && !is2Number) {
                return 1;
            }
            if (is2Number && !is1Number) {
                return -1;
            }
            if (is1Number && is2Number) {
                return Long.valueOf(parts1[i]).compareTo(Long.valueOf(parts2[i]));
            }
            // both are strings, we compare them taking into account special meaning
            Integer sm1 = SPECIAL_MEANINGS.get(parts1[i].toLowerCase(Locale.US));
            Integer sm2 = SPECIAL_MEANINGS.get(parts2[i].toLowerCase(Locale.US));
            if (sm1 != null) {
                sm2 = sm2 == null ? 0 : sm2;
                return sm1 - sm2;
            }
            if (sm2 != null) {
                return -sm2;
            }
            return parts1[i].compareTo(parts2[i]);
        }
        if (i < parts1.length) {
            return isNumber(parts1[i]) ? 1 : -1;
        }
        if (i < parts2.length) {
            return isNumber(parts2[i]) ? -1 : 1;
        }

        return 0;
    }

    private static boolean isNumber(String str) {
        return str.matches("\\d+");
    }
}
