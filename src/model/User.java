package model;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

public class User extends Person implements Serializable {
	private String username;
	private byte[] passwordHash;
	private final byte[] salt = new byte[16];
	private Permission permission;
	private boolean isDeleted = false;

	public User(String name, String username, String password, Permission permission) {
		super(name);

		assert username != null;
		assert permission != null;

		this.username = username;
		this.permission = permission;

		setPassword(password);
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted() {
		isDeleted = true;
	}

	/**
	 * Generates the password hash
	 */
	public byte[] getHash(String password) {
		assert password != null;

		try {
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
			SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return f.generateSecret(spec).getEncoded();

		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Error getting hashing algorithm");
		} catch (InvalidKeySpecException ex) {
			throw new RuntimeException("Error generating hash");
		}

	}

	/**
	 * Check if the input matches the stored hash
	 */
	public boolean checkPassword(String password) {
		assert password != null;

		return Arrays.equals(passwordHash, this.getHash(password));
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		assert permission != null;

		this.permission = permission;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		assert username != null;

		this.username = username;
	}

	/**
	 * Hashes a new password
	 */
	public void setPassword(String password) {
		assert password != null;

		new Random().nextBytes(salt);
		passwordHash = this.getHash(password);
	}

	@Override
	public String toString() {
		return name + " - " + username;
	}
}
