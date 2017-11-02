package gui.table;

@FunctionalInterface
public interface Setter<A, B> {
	void set(A item, B value);
}