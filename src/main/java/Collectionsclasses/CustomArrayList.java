package Collections;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class CustomArrayList<A> implements CustomList<A> {

  private Object[] massiv;
  private int size;
  private static final int DEFAULT_CAPASITY = 10;

  public CustomArrayList() {
    massiv = new Object[DEFAULT_CAPASITY];
    size = 0;
  }

  @Override
  public boolean add(A element) {
    if (size == massiv.length) {
      ensureCapacity();
    }
    massiv[size++] = element;
    return true;
  }

  @Override
  public A get(int index) {

    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return (A) massiv[index];
  }

  @Override
  public A remove(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    A old = (A) massiv[index];
    int mov = size - index - 1;
    if (mov > 0) {
      System.arraycopy(massiv, index + 1, massiv, index, mov);
    }
    massiv[--size] = null;
    return old;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  private void ensureCapacity() {
    int newCapacity = massiv.length + (massiv.length >> 1); // Увеличение в 1.5 раза
    massiv = Arrays.copyOf(massiv, newCapacity);
  }

  public CustomIterator<A> iterator() {
    return new CustomArrayListIterator();
  }

  private class CustomArrayListIterator implements CustomIterator<A> {

    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
      return currentIndex < size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public A next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements");
      }
      return (A) massiv[currentIndex++];
    }
  }
}

