import edu.princeton.cs.algs4.StdIn;

public class Subset {

    public static void main(String[] args) {
        RandomizedQueue<String> rqueue = new RandomizedQueue<String>();

        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            rqueue.enqueue(item);
        }

        System.out.println(rqueue.dequeue());
        System.out.println(rqueue.dequeue());
        System.out.println(rqueue.dequeue());

    }
}
