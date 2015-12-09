package Ui;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Observer;
import java.util.Properties;

import domain.WebshopFacade;
import domain.product.Product;
import domain.shoppingcart.ShoppingCart;
import domain.shoppingcartproduct.ShoppingCartProduct;

/**
 * 
 * @author Milan Sanders
 *
 */
public class Controller {
	private WebshopFacade webshop;
	CashierUI cashierUi;
	CustomerUI costumerUi;

	public Controller() {
		WebshopFacade webshop = new WebshopFacade(this.getProperties());
		this.webshop = webshop;
	}

	private Properties getProperties() {
		Properties properties = new Properties();
		try {
			InputStream is = new FileInputStream("config.env");
			properties.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	private ShoppingCart getCart(int cartId) {
		return this.webshop.getCart(cartId);
	}

	public int createCart(String userId) {
		return webshop.createCart(userId).getId();
	}

	public Product getProduct(int productId) {
		return webshop.getProduct(productId);
	}

	public void addProductToCart(int currentCartId, int productId, int qty) {
		Product p = getProduct(productId);
		if (p == null) {
			throw new IllegalArgumentException("Product not found");
		}
		webshop.addProductToCart(currentCartId, p, qty);
	}

	public void addCartObserver(int currentCartId, Observer cartUi) {
		getCart(currentCartId).addObserver(cartUi);
	}

	public Double getTotalAmountFromCart(int currentCartId) {
		return getCart(currentCartId).getTotalPrice();
	}

	public List<ShoppingCartProduct> getCartProducts(int cartId) {
		return getCart(cartId).getProducts();
	}

	public void initUI() {
		int cartId = createCart(null);

		this.cashierUi = new CashierUI(this, cartId);
		this.costumerUi = new CustomerUI(this, cartId);

		this.cashierUi.launch();
		this.costumerUi.launch();

	}


	public void shutDown() {
		webshop.deleteCart(cashierUi.cartId);
		costumerUi.dispose();
		cashierUi.dispose();

	}

	public void alterQuantity(int cartId, int productId, int newQuantity) {
		ShoppingCart cart = getCart(cartId);
		cart.alterProduct(productId, newQuantity);
	}

	public void updateCart(int cartId) {
		ShoppingCart cart = getCart(cartId);
		cart.reportChanges();
	}

	public void addDiscount(int cartId, String code) {
		webshop.addDiscountToCart(cartId, code);
	}

	public String getAppliedDiscountCode(int cartId) {
		return webshop.getDiscountCode(cartId);
	}

}
