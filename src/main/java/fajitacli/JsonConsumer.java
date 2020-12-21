package fajitacli;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.io.IOException;

public class JsonConsumer {

    public static class TokenRecord {
        protected String value;
        protected JsonToken token;

        public TokenRecord(JsonToken token, String value) {
            this.token = token;
            this.value = value;
        }
    }

    public static Queue<TokenRecord> queueAllTokens(JsonParser parser) throws IOException {
        Queue<TokenRecord> tokens = new ArrayDeque<TokenRecord>();
        while (parser.currentToken() != null) {
            tokens.add(new TokenRecord(parser.currentToken(), parser.getValueAsString()));
            parser.nextToken();
        }
        return tokens;
    }

    public static void pullTokens(Queue<TokenRecord> building, Queue<TokenRecord> tokens) {
        TokenRecord current = tokens.peek();
        if (current.token == JsonToken.START_OBJECT) {
            while (current.token != JsonToken.END_OBJECT) {
                if (current.token == JsonToken.START_OBJECT || current.token == JsonToken.START_ARRAY) {
                    pullTokens(building, tokens);
                } else {
                    building.add(tokens.remove());
                }
                current = tokens.peek();
            }
        } else if (current.token == JsonToken.START_ARRAY) {
            while (current.token != JsonToken.END_ARRAY) {
                if (current.token == JsonToken.START_OBJECT || current.token == JsonToken.START_ARRAY) {
                    pullTokens(building, tokens);
                } else {
                    building.add(tokens.remove());
                }
            }
        }
        // adds the end array, end object, or just plum thing you asked for if
        // it wasn't one of those
        building.add(tokens.remove());
    }

    public static Map<String, Queue<TokenRecord>> consumeObject(Queue<TokenRecord> tokens) {
        TokenRecord current = tokens.remove(); // Remove START_OBJECT, presumably
        if (current.token == JsonToken.VALUE_NULL) {
            return null;
        }
        Map<String, Queue<TokenRecord>> result = new HashMap<String, Queue<TokenRecord>>();
        String currentName;
        while (current.token != JsonToken.END_OBJECT) {
            Queue<TokenRecord> b = new ArrayDeque<TokenRecord>();
            currentName = tokens.remove().value;
            pullTokens(b, tokens);
            result.put(currentName, b);
        }
        return result;
    }

    public static List<Queue<TokenRecord>> consumeArray(Queue<TokenRecord> tokens) {

        TokenRecord current = tokens.remove(); // Remove START_ARRAY
        if (current.token == JsonToken.VALUE_NULL) {
            return null;
        }
        List<Queue<TokenRecord>> result = new ArrayList<Queue<TokenRecord>>();
        while (current.token != JsonToken.END_ARRAY) {
            Queue<TokenRecord> b = new ArrayDeque<TokenRecord>();
            pullTokens(b, tokens);
            result.add(b);
        }
        return result;
    }

    public static BigInteger consumeInteger(Queue<TokenRecord> tokens) {
        TokenRecord there = tokens.remove();
        if (there.token == JsonToken.VALUE_NULL) {
            return null;
        } else {
            return new BigInteger(there.value);
        }
    }

    public static BigDecimal consumeDecimal(Queue<TokenRecord> tokens) {
        TokenRecord there = tokens.remove();
        if (there.token == JsonToken.VALUE_NULL) {
            return null;
        } else {
            return new BigDecimal(there.value);
        }
    }

    public static String consumeString(Queue<TokenRecord> tokens) {
        TokenRecord there = tokens.remove();
        if (there.token == JsonToken.VALUE_NULL) {
            return null;
        } else {
            return there.value;
        }
    }

    public static Boolean consumeBoolean(Queue<TokenRecord> tokens) {
        TokenRecord there = tokens.remove();
        if (there.token == JsonToken.VALUE_NULL) {
            return null;
        } else {
            return Boolean.valueOf(there.token == JsonToken.VALUE_TRUE);
        }
    }

}
