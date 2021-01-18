package JavaC0.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IndexMap<K, V> extends HashMap<K, V>
{
    ArrayList<K> indexedKeys;

    public IndexMap(Map<? extends K, ? extends V> m)
    {
        super(m);
        var keys = m.keySet().toArray();
        indexedKeys = new ArrayList<>();
        for (var k : keys)
        {
            indexedKeys.add((K) k);
        }
    }

    @Override
    public V put(K key, V value)
    {
        if (!indexedKeys.contains(key))
        {
            indexedKeys.add(key);
        }
        return super.put(key, value);
    }

    public int getIndex(Object o)
    {
        return indexedKeys.indexOf(o);
    }
}
