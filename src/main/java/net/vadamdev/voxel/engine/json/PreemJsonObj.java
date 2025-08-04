package net.vadamdev.voxel.engine.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.function.Function;

/**
 * @author VadamDev
 * @since 10/06/2025
 */
public class PreemJsonObj extends HashMap<String, Object> implements JSONAware, JSONStreamAware {
    public static PreemJsonObj of(Object source) {
        if(!(source instanceof JSONObject jsonObject))
            throw new IllegalArgumentException("Source must be an instance of JSONObject");

        final PreemJsonObj result = new PreemJsonObj();
        if(jsonObject.isEmpty())
            return result;

        result.putAll(jsonObject);
        return result;
    }

    private PreemJsonObj() {}

    /*
       Classic Gets
     */

    public byte getByte(String key) {
        return getNumber(key).byteValue();
    }

    public short getShort(String key) {
        return getNumber(key).shortValue();
    }

    public int getInt(String key) {
        return getNumber(key).intValue();
    }

    public long getLong(String key) {
        return getNumber(key).longValue();
    }

    public float getFloat(String key) {
        return getNumber(key).floatValue();
    }

    public double getDouble(String key) {
        return getNumber(key).doubleValue();
    }

    private Number getNumber(String key) {
        return map(key, Number.class);
    }

    public boolean getBoolean(String key) {
        return mapOrNull(key, Boolean.class) != null; //Programming war crime
    }

    public char getChar(String key) {
        return map(key, Character.class);
    }

    @Nullable
    public String getString(String key) {
        return mapOrNull(key, String.class);
    }

    @Nullable
    public PreemJsonObj getJsonObject(String key) {
        final JSONObject obj = mapOrNull(key, JSONObject.class);
        return obj != null ? PreemJsonObj.of(obj) : null;
    }

    @Nullable
    public PreemJsonArray getJsonArray(String key) {
        final JSONArray array = mapOrNull(key, JSONArray.class);
        return array != null ? PreemJsonArray.of(array) : null;
    }

    /*
       Map with Generics
     */

    @Nullable
    public <T> T mapOrNull(String key, Class<T> type) {
        try {
            return map(key, type);
        }catch(Exception ignored) {}

        return null;
    }

    @NotNull
    public <T> T map(String key, Class<T> type) {
        final Object stored = get(key);
        if(stored == null || !type.isAssignableFrom(stored.getClass()))
            throw new ClassCastException("Stored object (" + (stored != null ? stored.getClass().getSimpleName() : null) + ") is not assignable from " + type.getSimpleName());

        return type.cast(stored);
    }

    @Nullable
    public <T> T map(String key, Function<Object, T> mappingFunction) {
        final Object stored = get(key);
        if(stored == null)
            return null;

        return mappingFunction.apply(stored);
    }

    @Nullable
    public <T> T acquire(Function<PreemJsonObj, T> acquireFunc) {
        return acquireFunc.apply(this);
    }

    /*
       Faking JSONObject behavior
     */

    @Override
    public void writeJSONString(Writer out) throws IOException {
        JSONObject.writeJSONString(this, out);
    }

    @Override
    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}

