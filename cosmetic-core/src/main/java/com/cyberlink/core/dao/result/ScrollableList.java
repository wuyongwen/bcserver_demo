package com.cyberlink.core.dao.result;

import java.util.AbstractList;
import java.util.Iterator;

import org.hibernate.ScrollableResults;

public class ScrollableList<E> extends AbstractList<E> {
    private ScrollableResults cursor;

    public ScrollableList(ScrollableResults results) {
        this.cursor = results;
    }

    @Override
    public E get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            public boolean hasNext() {
                return cursor.next();
            }

            @SuppressWarnings("unchecked")
            public E next() {
                return (E) cursor.get(0);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        return cursor.getRowNumber();
    }

}
