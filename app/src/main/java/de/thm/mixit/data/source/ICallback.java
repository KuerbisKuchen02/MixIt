package de.thm.mixit.data.source;

/**
 * @author Justin Wolek
 * @version 1.0.0
 */
public interface ICallback<T> {
    void onDataLoaded(T data);
}
