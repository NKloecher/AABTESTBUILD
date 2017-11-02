package gui.table;

@FunctionalInterface
public interface ValidationHandler {
	void onValidate(String error, boolean isValid);
}