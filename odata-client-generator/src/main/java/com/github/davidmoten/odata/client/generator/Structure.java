package com.github.davidmoten.odata.client.generator;

import java.util.LinkedList;
import java.util.List;

import org.oasisopen.odata.csdl.v4.TNavigationProperty;
import org.oasisopen.odata.csdl.v4.TProperty;

public abstract class Structure<T> {

    protected final T value;
    private final Class<T> cls;
    protected final Names names;

    public Structure(T value, Class<T> cls, Names names) {
        this.value = value;
        this.cls = cls;
        this.names = names;
    }

    abstract Structure<T> create(T t);

    abstract String getName();

    abstract String getBaseType();

    abstract List<TProperty> getProperties();

    abstract List<TNavigationProperty> getNavigationProperties();

    public final List<T> getHeirarchy() {
        List<T> a = new LinkedList<>();
        a.add(value);
        Structure<T> st = this;
        while (true) {
            if (st.getBaseType() == null) {
                return a;
            } else {
                String baseTypeSimpleName = names
                        .getSimpleTypeNameFromTypeWithNamespace(st.getBaseType());
                st = Util.types(names.getSchema(), cls) //
                        .map(this::create) //
                        .filter(x -> x.getName().equals(baseTypeSimpleName)) //
                        .findFirst() //
                        .get();
                a.add(0, st.value);
            }
        }
    }

}