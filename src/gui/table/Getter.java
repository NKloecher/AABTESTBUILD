package gui.table;

@FunctionalInterface
public interface Getter<A, B> {
	B get(A item);
}
