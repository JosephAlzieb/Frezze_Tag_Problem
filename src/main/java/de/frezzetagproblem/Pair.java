package de.frezzetagproblem;

public class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {

  private final T1 first;
  private final T2 second;

  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public T1 getFirst() {
    return first;
  }

  public T2 getSecond() {
    return second;
  }

  @Override
  public int compareTo(Pair<T1, T2> o) {
    int firstCompare = this.first.compareTo(o.first);
    if (firstCompare != 0) {
      return firstCompare;
    }
    return this.second.compareTo(o.second);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Pair<?, ?> pair = (Pair<?, ?>) o;

    if (!first.equals(pair.first))
      return false;
    return second.equals(pair.second);
  }

  @Override
  public int hashCode() {
    int result = first.hashCode();
    result = 31 * result + second.hashCode();
    return result;
  }
}
