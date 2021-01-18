package javac0.compiler;

import java.util.*;

public class IndexSet<T> extends AbstractSet<T> implements Set<T>
{
    ArrayList<T> set;
    private static final Object PRESENT = new Object();

    public IndexSet(T s)
    {
        this.set = new ArrayList<>();
        set.add(s);
    }

    @Override
    public Iterator<T> iterator()
    {
        return set.iterator();
    }

    @Override
    public int size()
    {
        return set.size();
    }

    public int getIndex(Object o)
    {
        return set.indexOf(o);
    }

    @Override
    public boolean isEmpty()
    {
        return set.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return set.contains(o);
    }

    @Override
    public boolean add(T o)
    {
        if (set.contains(o))
            return false;
        set.add(o);
        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        return set.remove(o);
    }

    @Override
    public void clear()
    {
        set.clear();
    }

//
//    @Override
//    public boolean addAll(Collection c)
//    {
//        for (var o : c)
//        {
//            if (set.contains(o))
//                return false;
//        }
//        set.addAll(c);
//        return true;
//    }
//
//    @Override
//    public boolean removeAll(Collection c)
//    {
//        return false;
//    }
//
//    @Override
//    public boolean retainAll(Collection c)
//    {
//        return false;
//    }
//
//    @Override
//    public boolean containsAll(Collection c)
//    {
//        return false;
//    }
//
//    @Override
//    public Object[] toArray(Object[] a)
//    {
//        return new Object[0];
//    }
}
