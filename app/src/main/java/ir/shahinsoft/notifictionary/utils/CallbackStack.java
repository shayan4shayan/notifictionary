package ir.shahinsoft.notifictionary.utils;

import java.util.Stack;

public class CallbackStack<T> extends Stack<T> {

    private Callback<T> callback;

    public CallbackStack(Callback<T> callback) {

        this.callback = callback;
    }

    @Override
    public T push(T item) {
        T t = super.push(item);
        callback.onItemInserted(item);
        return t;
    }

    @Override
    public synchronized boolean add(T t) {
        boolean result = super.add(t);
        callback.onItemInserted(t);
        return result;
    }

    @Override
    public synchronized T pop() {
        T item = super.pop();
        callback.onItemRemoved(item);
        if (isEmpty()) {
            callback.onStackEmpty();
        }
        return item;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if (isEmpty()) {
            callback.onStackEmpty();
        }
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        callback.onStackEmpty();
    }

    public interface Callback<T> {
        void onItemInserted(T item);

        void onStackEmpty();

        void onItemRemoved(T item);
    }
}
