/**
 *
 *  Dequeue. A double-ended queue or deque (pronounced "deck") is a
 * generalization of a stack and a queue that supports adding and removing
 * items from either the front or the back of the data structure. Create a
 * generic data type Deque that implements the following API:
 *
 * Corner cases.
 *  Throw a java.lang.NullPointerException if the client
 *  attempts to add a null item; throw a java.util.NoSuchElementException
 *  if the client attempts to remove an item from an empty deque; throw a
 *  java.lang.UnsupportedOperationException if the client calls the remove()
 *  method in the iterator; throw a java.util.NoSuchElementException if the
 *  client calls the next() method in the iterator and there are no more
 *  items to return.
 *
 *
 * Performance requirements.
 *  Your deque implementation must support each deque operation in constant
 *  worst-case time. A deque containing N items must use at most 48N + 192
 *  bytes of memory. and use space proportional to the number of items currently
 *  in the deque. Additionally, your iterator implementation must support each
 *  operation (including construction) in constant worst-case time.
 *
 *
 */


import java.util.Iterator;


public class Deque<Item> implements Iterable<Item> {

    private Node first, last;
    private int size = 0;

    private class Node {
        private Item item;
        private Node next;
        private Node prev;
    }

    public Deque() {
        // construct an empty deque
    }

    public boolean isEmpty() {
        // is the deque empty?
        return size == 0;
    }

    public int size() {
        // return the number of items on the deque
        return size;
    }

    public void addFirst(Item item) {
        // add the item to the front

        if (item == null) {
            throw new java.lang.NullPointerException();
        }

        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        first.prev = null;

        if (isEmpty()) {
            last = first;
        } else {
            oldfirst.prev = first;
        }

        size++;
    }

    public void addLast(Item item) {
        // add the item to the end

        if (item == null) {
            throw new java.lang.NullPointerException();
        }

        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        last.prev = oldlast;
        if (isEmpty()) first = last;
        else oldlast.next = last;
        size++;
    }

    public Item removeFirst() {
        // remove and return the item from the front

        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        Node oldfirst = first;
        first = first.next;
        size--;
        return oldfirst.item;
    }

    public Item removeLast() {
        // remove and return the item from the end

        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        Node oldlast = last;
        last = last.prev;
        size--;
        return oldlast.item;
    }


    public Iterator<Item> iterator() {
        // return an iterator over items in order from front to end
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {

        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }

        public Item next() {

            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }

            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public static void main(String[] args) {
        // unit testing
        Deque<String> deque = new Deque<String>();
        assert deque.isEmpty();
        deque.addLast("hi");
        assert !deque.isEmpty();
        assert deque.size() == 1;
        deque.addLast("there");
        assert deque.size() == 2;

        assert deque.removeFirst().equals("hi");
        assert deque.removeFirst().equals("there");

        deque.addFirst("b");
        deque.addFirst("a");
        deque.addLast("c");
        deque.addLast("d");
        assert deque.size() == 4 : deque.size();
        assert !deque.isEmpty() : deque.isEmpty();

        // test iterator
        String[] strings = {"a", "b", "c", "d"};
        int i = 0;
        for (String s : deque) {
            assert s == strings[i];
            i++;
        }

        assert deque.removeFirst().equals("a");
        assert deque.removeFirst().equals("b");
        assert deque.removeFirst().equals("c");
        assert deque.removeFirst().equals("d");
        assert deque.size() == 0 : deque.size();
        assert deque.isEmpty() : deque.isEmpty();

        deque.addFirst("b");
        deque.addFirst("a");
        deque.addLast("c");
        deque.addLast("d");

        assert deque.removeLast().equals("d") : deque.removeLast();
        assert deque.removeLast().equals("c") : deque.removeLast();
        assert deque.removeLast().equals("b") : deque.removeLast();
        assert deque.removeLast().equals("a") : deque.removeLast();
        assert deque.size() == 0 : deque.size();
        assert deque.isEmpty() : deque.isEmpty();

        deque.addLast("b");
        deque.addFirst("a");
        deque.addLast("c");
        deque.addFirst("d");
        assert deque.size() == 4 : deque.size();
        assert !deque.isEmpty() : deque.isEmpty();

        assert deque.removeLast().equals("c") : deque.removeLast();
        assert deque.removeFirst().equals("d") : deque.removeLast();
        assert deque.removeLast().equals("b") : deque.removeLast();
        assert deque.removeFirst().equals("a") : deque.removeLast();
        assert deque.size() == 0 : deque.size();
        assert deque.isEmpty() : deque.isEmpty();


    }
}
