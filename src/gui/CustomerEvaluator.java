package gui;

import java.util.regex.Pattern;

public class CustomerEvaluator {

	/**
	 * Test om information er gyldige
	 * 
	 * @param name
	 *            testes kun på om strengen ikke er tom
	 * @param phone
	 *            testes på om den kun indeholder tal
	 * @param email
	 *            testes på gyldig email adresse (se tidligere lektion) <br>
	 *            adresse er bare gyldig
	 * @return
	 */
	public boolean isValid(String name, String phone, String email) {
		return nameIsValid(name) && phoneIsValid(phone) && emailIsValid(email);
	}

	public boolean nameIsValid(String s) {
		return s != null && !s.isEmpty();
	}

	public boolean phoneIsValid(String s) {
		return Pattern.matches("\\d*", s);
	}

	public boolean emailIsValid(String s) {
		if (s.isEmpty()) {
			return true;
		}
		return Pattern.matches("^\\w+(\\.\\w+)?@\\w+(\\.\\w+)?$", s);
	}

}
