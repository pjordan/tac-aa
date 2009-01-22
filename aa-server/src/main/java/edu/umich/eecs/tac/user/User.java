package edu.umich.eecs.tac.user;

import edu.umich.eecs.tac.props.Product;

/**
 * A user is defined by its query state and product preference.
 * 
 * @author Patrick Jordan
 */
public class User {
	private QueryState state;
	private Product product;

	public User() {
	}

	public User(QueryState state, Product product) {
		this.state = state;
		this.product = product;
	}

	public QueryState getState() {
		return state;
	}

	public void setState(QueryState state) {
		this.state = state;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isSearching() {
		return state.isSearching();
	}

	public boolean isTransacting() {
		return state.isTransacting();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		User user = (User) o;

		if (product != null ? !product.equals(user.product)
				: user.product != null)
			return false;
		if (state != user.state)
			return false;

		return true;
	}

	public int hashCode() {
		int result;
		result = (state != null ? state.hashCode() : 0);
		result = 31 * result + (product != null ? product.hashCode() : 0);
		return result;
	}
}
