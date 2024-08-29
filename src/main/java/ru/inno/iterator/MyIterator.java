package ru.inno.iterator;

import java.util.Collection;

public class MyIterator {

    private Collection<Integer> data;
    private int index;

    public MyIterator(Collection<Integer> data) {
        this.data = data;
        this.index = -1;
    }

    public Integer next() {
        index++;
        return (Integer)data.toArray()[index];
    }

    public boolean hasNext() {
        try {
            Object e = data.toArray()[index + 1];
            return true;
        } catch (IndexOutOfBoundsException ex){
            return false;
        }
    }
}
