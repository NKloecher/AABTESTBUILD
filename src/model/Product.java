package model;

import java.io.Serializable;

public class Product implements Serializable, Comparable<Product> {
	private String name;
	private Integer clips;
	private String category;
	private String image;

	public Product(String name, Integer clips, String category, String image) {
		assert name != null && !name.isEmpty();

		this.name = name;
		this.clips = clips;
		this.category = category;
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public Integer getClips() {
		return clips;
	}

	public String getCategory() {
		return category;
	}

	public void setName(String name) {
		assert name != null && !name.isEmpty();

		this.name = name;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setClips(Integer clips) {
		this.clips = clips;
	}

	public String getImage() {
		return image;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Product o) {
		int nameCompare = getName().compareTo(o.getName());

		if (nameCompare == 0)
			return getCategory().compareTo(o.getCategory());
		else
			return nameCompare;
	}

}