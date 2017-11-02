package gui.table;

@FunctionalInterface
public interface ActionHandler<A> {
	void exec(A item);
}