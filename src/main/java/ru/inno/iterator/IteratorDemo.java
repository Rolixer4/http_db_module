package ru.inno.iterator;

import org.instancio.Instancio;

import java.util.Collection;
import java.util.List;

public class IteratorDemo {

    public static void main(String[] args) {
        // мы не знаем, сколько строк придет из БД. 0, 1000000000000
        Collection<Integer> list = getDataFromDb();

        MyIterator iterator = new MyIterator(list);

        System.out.println(list.size());
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }

    }

    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // []
    // 16







    private static Collection<Integer> getDataFromDb() {
        return Instancio.createList(Integer.class);
    }
}
