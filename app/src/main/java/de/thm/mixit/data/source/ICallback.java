package de.thm.mixit.data.source;

/**
 * @author Justin Wolek
 */
public interface ICallback<T> {
    void onDataLoaded(T data);
}
