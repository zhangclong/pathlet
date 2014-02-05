/*
 * Copyright 2010-2011 the original author or authors.
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
package com.google.code.pathlet.web.ognl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import ognl.TypeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.google.code.pathlet.web.ognl.ConvertException;


/**
 * A simple list that guarantees that inserting and retrieving objects will always work regardless
 * of the current size of the list.  Upon insertion, type conversion is also performed if necessary.
 * Empty beans will be created to fill the gap between the current list size and the requested index
 * using ObjectFactory's {@link ObjectFactory#buildBean(Class,java.util.Map) buildBean} method.
 *
 * @author Charlie Zhang
 */
public class IncreasableList extends ArrayList { 
    
	private static final Log log = LogFactory.getLog(IncreasableList.class);

    private Class clazz;
    
    private TypeConverter conv;
    
    private Map context;

    public IncreasableList(Map context, TypeConverter conv, Class clazz) {
    	this.context = context;
        this.conv = conv;
        this.clazz = clazz;
    }

    public IncreasableList(Map context, TypeConverter conv, Class clazz, int initialCapacity) {
        super(initialCapacity);
        this.context = context;
        this.clazz = clazz;
        this.conv = conv;
    }

    /**
     * Inserts the specified element at the specified position in this list. Shifts the element
     * currently at that position (if any) and any subsequent elements to the right (adds one to
     * their indices).
     * <p/>
     * This method is guaranteed to work since it will create empty beans to fill the gap between
     * the current list size and the requested index to enable the element to be set.  This method
     * also performs any necessary type conversion.
     *
     * @param index   index at which the specified element is to be inserted.
     * @param element element to be inserted.
     */
    @Override
    public void add(int index, Object element) {
        if (index >= this.size()) {
            get(index);
        }

        element = convert(element);

        super.add(index, element);
    }

    /**
     * Appends the specified element to the end of this list.
     * <p/>
     * This method performs any necessary type conversion.
     *
     * @param element element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of Collection.add).
     */
    @Override
    public boolean add(Object element) {
        element = convert(element);

        return super.add(element);
    }

    /**
     * Appends all of the elements in the specified Collection to the end of this list, in the order
     * that they are returned by the specified Collection's Iterator.  The behavior of this
     * operation is undefined if the specified Collection is modified while the operation is in
     * progress.  (This implies that the behavior of this call is undefined if the specified
     * Collection is this list, and this list is nonempty.)
     * <p/>
     * This method performs any necessary type conversion.
     *
     * @param c the elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     * @throws NullPointerException if the specified collection is null.
     */
    @Override
    public boolean addAll(Collection c) {
        if (c == null) {
            throw new NullPointerException("Collection to add is null");
        }

        for (Object aC : c) {
            add(aC);
        }

        return true;
    }

    /**
     * Inserts all of the elements in the specified Collection into this list, starting at the
     * specified position.  Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (increases their indices).  The new elements will appear in
     * the list in the order that they are returned by the specified Collection's iterator.
     * <p/>
     * This method is guaranteed to work since it will create empty beans to fill the gap between
     * the current list size and the requested index to enable the element to be set.  This method
     * also performs any necessary type conversion.
     *
     * @param index index at which to insert first element from the specified collection.
     * @param c     elements to be inserted into this list.
     * @return <tt>true</tt> if this list changed as a result of the call.
     */
    @Override
    public boolean addAll(int index, Collection c) {
        if (c == null) {
            throw new NullPointerException("Collection to add is null");
        }

        boolean trim = false;

        if (index >= this.size()) {
            trim = true;
        }

        for (Iterator it = c.iterator(); it.hasNext(); index++) {
            add(index, it.next());
        }

        if (trim) {
            remove(this.size() - 1);
        }

        return true;
    }

    /**
     * Returns the element at the specified position in this list.
     * <p/>
     * An object is guaranteed to be returned since it will create empty beans to fill the gap
     * between the current list size and the requested index.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     */
    @Override
    public synchronized Object get(int index) {
        while (index >= this.size()) {
            try {
                this.add(clazz.newInstance()); 
            } catch (Exception e) {
                throw new ConvertException(e);
            }
        }

        return super.get(index);
    }

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * <p/>
     * This method is guaranteed to work since it will create empty beans to fill the gap between
     * the current list size and the requested index to enable the element to be set.  This method
     * also performs any necessary type conversion.
     *
     * @param index   index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     */
    @Override
    public Object set(int index, Object element) {
        if (index >= this.size()) {
            get(index);
        }

        element = convert(element);

        return super.set(index, element);
    }

    private Object convert(Object element) {
        if ((element != null) && !clazz.isAssignableFrom(element.getClass())) {
            // convert to correct type
            if (log.isDebugEnabled()) {
                log.debug("Converting from " + element.getClass().getName() + " to " + clazz.getName());
            }

            element = conv.convertValue(context, null, null, null, element, clazz);
        }

        return element;
    }

    @Override
    public boolean contains(Object element) {
        element = convert(element);

        return super.contains(element);
    }
}
