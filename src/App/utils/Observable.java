package App.utils;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> {

    private T value;
    private List<Observer<T>> observers = new ArrayList<>();

    public Observable(T value) {
        this.value = value;
    }

    public Observable() {
        this(null);
    }

    public T getValue() {
        return value;
    }

    public void subscribe(Observer<T> observer) {
        observers.add(observer);
        observer.update(value);
    }

    public void unsubscribe(Observer<T> observer) {
        observers.remove(observer);
    }

    public void update(T value) {
        this.value = value;
        for (var observer : observers) {
            observer.update(value);
        }
    }

}
