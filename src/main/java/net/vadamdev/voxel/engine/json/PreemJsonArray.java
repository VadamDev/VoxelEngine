package net.vadamdev.voxel.engine.json;

import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author VadamDev
 * @since 10/06/2025
 */
public class PreemJsonArray extends ArrayList<Object> implements JSONAware, JSONStreamAware {
    public static PreemJsonArray of(Object source) {
        if(!(source instanceof JSONArray jsonArray))
            throw new IllegalArgumentException("Source must be an instance of JSONArray !");

        return new PreemJsonArray(jsonArray);
    }

    private PreemJsonArray(JSONArray jsonArray) {
        Collections.addAll(this, jsonArray.toArray());
    }

    /*
       Classic Gets
     */

    public byte getByte(int index) {
        return getNumber(index).byteValue();
    }

    public short getShort(int index) {
        return getNumber(index).shortValue();
    }

    public int getInt(int index) {
        return getNumber(index).intValue();
    }

    public long getLong(int index) {
        return getNumber(index).longValue();
    }

    public float getFloat(int index) {
        return getNumber(index).floatValue();
    }

    public double getDouble(int index) {
        return getNumber(index).doubleValue();
    }

    private Number getNumber(int index) {
        return map(index, Number.class);
    }

    public boolean getBoolean(int index) {
        return Boolean.valueOf(getString(index));
    }

    public char getChar(int index) {
        return map(index, Character.class).charValue();
    }

    @Nullable
    public String getString(int index) {
        return mapOrNull(index, String.class);
    }

    public PreemJsonObj getJsonObject(int index) {
        return PreemJsonObj.of(map(index, JSONObject.class));
    }

    /*
       Arrays
     */

    public int[] toIntArray() throws ClassCastException {
        final int[] arr = new int[size()];
        for(int i = 0; i < size(); i++)
            arr[i] = map(i, Number.class).intValue();

        return arr;
    }

    public float[] toFloatArray() throws ClassCastException {
        final float[] arr = new float[size()];
        for(int i = 0; i < size(); i++)
            arr[i] = map(i, Number.class).floatValue();

        return arr;
    }

    /*
       Map with Generics
     */

    @Nullable
    public <T> T mapOrNull(int index, Class<T> type) {
        try {
            return map(index, type);
        }catch(Exception ignored) {}

        return null;
    }

    public <T> T map(int index, Class<T> type) {
        final Object stored = get(index);
        if(stored == null || !type.isAssignableFrom(stored.getClass()))
            throw new ClassCastException("Stored object (" + (stored != null ? stored.getClass().getSimpleName() : null) + ") is not assignable from " + type.getSimpleName());

        return type.cast(stored);
    }

    public <T> T map(int index, Function<Object, T> mappingFunction) {
        return mappingFunction.apply(get(index));
    }

    public <T> T acquire(Function<PreemJsonArray, T> acquireFunc) {
        return acquireFunc.apply(this);
    }

    public <T extends JSONAware> List<T> asListOf(Class<T> clazz) throws ClassCastException {
        final List<T> result = new ArrayList<>(size());

        if(PreemJsonObj.class.isAssignableFrom(clazz)) {
            for(int i = 0; i < size(); i++) {
                result.add((T) PreemJsonObj.of(map(i, JSONObject.class)));
            }
        }else if(PreemJsonArray.class.isAssignableFrom(clazz)) {
            for(int i = 0; i < size(); i++) {
                result.add((T) PreemJsonArray.of(map(i, JSONArray.class)));
            }
        }else {
            for(Object obj : this)
                result.add(clazz.cast(obj));
        }

        return result;
    }

    /*
       Faking JsonArray behavior...
     */

    @Override
    public void writeJSONString(Writer out) throws IOException {
        JSONArray.writeJSONString(this, out);
    }

    @Override
    public String toJSONString(){
        return JSONArray.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
