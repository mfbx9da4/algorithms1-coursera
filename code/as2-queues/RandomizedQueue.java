/**
 *
 * Randomized queue. A randomized queue is similar
 * to a stack or queue, except that the item removed
 * is chosen uniformly at random from items in the data
 * structure. Create a generic data type RandomizedQueue
 * that implements the following API:
 *
 * Corner cases.
 *   The order of two or more iterators to the same randomized queue
 * must be mutually independent; each iterator must maintain its own
 * random order. Throw a java.lang.NullPointerException if the client
 * attempts to add a null item; throw a java.util.NoSuchElementException
 * if the client attempts to sample or dequeue an item from an empty
 * randomized queue; throw a java.lang.UnsupportedOperationException
 * if the client calls the remove() method in the iterator; throw
 * a java.util.NoSuchElementException if the client calls the
 * next() method in the iterator and there are no more items to return.
 *
 * Performance requirements.
 *  Your randomized queue implementation must support each randomized queue operation (besides creating an iterator) in constant amortized time. That is, any sequence of M randomized queue operations (starting from an empty queue) should take at most cM steps in the worst case, for some constant c. A randomized queue containing N items must use at most 48N + 192 bytes of memory. Additionally, your iterator implementation must support operations next() and hasNext() in constant worst-case time; and construction in linear time; you may (and will need to) use a linear amount of extra memory per iterator.
 *
 *
 */


import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;


public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] queue;
    private int size;

    public RandomizedQueue() {
        // construct an empty randomized queue
        queue = newGenericArray(1);
    }

    private Item[] newGenericArray(int n) {
        return (Item[]) new Object[n];
    }

    public boolean isEmpty() {
        // is the queue empty?
        return size == 0;
    }

    public int size() {
        // return the number of items on the queue
        return size;
    }

    private int internalSize() {
        return queue.length;
    }

    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.NullPointerException();
        }

        // grow if necessary
        if (size == queue.length) {
            grow();
        }

        // add the item to the end
        queue[size] = item;
        size++;
    }

    private void grow() {
        Item[] new_queue = newGenericArray(queue.length * 2);
        for (int i = 0; i < queue.length; i++) {
            new_queue[i] = queue[i];
        }
        queue = new_queue;
    }

    private void shrink() {
        if (queue.length <= 2) {
            return;
        }

        Item[] new_queue = newGenericArray(queue.length / 2);
        for (int i = 0; i < queue.length; i++) {
            if (queue[i] != null) {
                new_queue[i] = queue[i];
            }
        }
        queue = new_queue;
    }

    public Item dequeue() {
        // remove and return a random item

        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        // put a random item at the end
        int random_i = StdRandom.uniform(size);
        swap(random_i, size - 1);

        // remove random item
        Item item = queue[size - 1];
        queue[size - 1] = null;
        size--;

        // shrink if necessary
        if (size <= queue.length / 4) {
            shrink();
        }

        return item;
    }

    private void swap(int i, int j) {
        Item temp = queue[i];
        queue[i] = queue[j];
        queue[j] = temp;
    }

    public Item sample() {
        // return (but do not remove) a random item

        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        return queue[StdRandom.uniform(size)];
    }

    public Iterator<Item> iterator() {
        // return an independent iterator over items in random order
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private int i = 0;
        private Item item;
        private Item[] new_queue;

        public RandomizedQueueIterator() {
            new_queue = newGenericArray(size);
            int j;
            for (int i = 0; i < size; i++) {
                j = StdRandom.uniform(i + 1);
                if (j == i) {
                    new_queue[i] = queue[i];
                } else {
                    new_queue[i] = new_queue[j];
                    new_queue[j] = queue[i];
                }
            }

        }

        public boolean hasNext() {
            return i < size;
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }

        public Item next() {

            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }

            item = new_queue[i];
            i++;
            return item;
        }
    }

    public static void main(String[] args) {
        // unit testing
        RandomizedQueue<String> rqueue = new RandomizedQueue<String>();
        assert rqueue.isEmpty();

        rqueue.enqueue("a");
        rqueue.enqueue("b");
        assert !rqueue.isEmpty();
        assert rqueue.size() == 2;
        assert rqueue.internalSize() == 2 : rqueue.internalSize();

        rqueue.enqueue("c");
        assert !rqueue.isEmpty();
        assert rqueue.size() == 3;
        assert rqueue.internalSize() == 4 : rqueue.internalSize();

        System.out.println("These positions");

        String[] strings = {"a", "b", "c"};
        int i = 0;
        for (String s : rqueue) {
            System.out.println("Position " + i + " : " + s);
            i++;
        }

        System.out.println("Should be different to these positions");

        i = 0;
        for (String s : rqueue) {
            System.out.println("Position " + i + " : " + s);
            i++;
        }

        String sam = rqueue.sample();
        System.out.println("Random sample: " + sam);
        assert sam.equals("a") || sam.equals("b") || sam.equals("c");

        sam = rqueue.dequeue();
        System.out.println("Random dequeue 1: " + sam);
        assert sam.equals("a") || sam.equals("b") || sam.equals("c");
        assert !rqueue.isEmpty();
        assert rqueue.size() == 2;
        assert rqueue.internalSize() == 4 : rqueue.internalSize();

        sam = rqueue.dequeue();
        System.out.println("Random dequeue 2: " + sam);
        assert sam.equals("a") || sam.equals("b") || sam.equals("c");
        assert !rqueue.isEmpty();
        assert rqueue.size() == 1;
        assert rqueue.internalSize() == 2 : rqueue.internalSize();

        sam = rqueue.dequeue();
        System.out.println("Random dequeue 3: " + sam);
        assert sam.equals("a") || sam.equals("b") || sam.equals("c");
        assert rqueue.isEmpty();
        assert rqueue.size() == 0;
        assert rqueue.internalSize() == 2 : rqueue.internalSize();

        System.out.println("Tests passed! :-)");

    }
}


