package Collections;

public interface CustomList<A>{
  boolean add(A element);
  A get(int index);
  A remove(int index);
  int size();
  boolean isEmpty();
  CustomIterator<A> iterator();
}
