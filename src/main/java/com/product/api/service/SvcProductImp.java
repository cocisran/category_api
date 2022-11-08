package com.product.api.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
import com.product.api.entity.SimpleProduct;
import com.product.api.repository.RepoCategory;
import com.product.api.repository.RepoProduct;
import com.product.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	RepoProduct repo;

	@Autowired
	RepoCategory repoCategory;

	@Override
	public Product getProduct(String gtin) {
		Product product = repo.getProductByGTIN(gtin);
		if (product != null) {
			product.setCategory(repoCategory.findByCategoryId(product.getCategory_id()));
			return product;
		} else
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
	}

	@Override
	public List<SimpleProduct> getListProductsByCategory(Integer category_id) {

		Category product_category = repoCategory.findByCategoryId(category_id);
		if (product_category == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");

		List<Product> productos = repo.getProductsByCategoryID(category_id);

		return makeItSimple(productos);
	}

	@Override
	public ApiResponse createProduct(Product in) {
		Category db_category = repoCategory.findByCategoryId(in.getCategory_id());

		if (db_category == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");

		Product db_product_instance = repo.getAllProductByGTIN(in.getGtin());
		if (db_product_instance != null) {
			if (db_product_instance.getStatus() == 1)
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");

			in.setStatus(1);
			ApiResponse update_response = updateProduct(in, in.getProduct_id());
			return new ApiResponse("product activated", update_response.getStatus());
		}

		db_product_instance = repo.getProductByName(in.getProduct());
		if (db_product_instance != null)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");

		repo.createProduct(in.getGtin(),
				in.getProduct(),
				in.getDescription(),
				in.getPrice(),
				in.getStock(),
				in.getCategory_id());
		return new ApiResponse("product created", HttpStatus.OK);
	}

	@Override
	public ApiResponse updateProduct(Product in, Integer id) {
		Integer updated = 0;
		try {
			updated = repo.updateProduct(id, in.getGtin(), in.getProduct(), in.getDescription(), in.getPrice(),
					in.getStock(), in.getCategory_id());
		} catch (DataIntegrityViolationException e) {
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
			if (e.contains(SQLIntegrityConstraintViolationException.class))
				throw new ApiException(HttpStatus.BAD_REQUEST, "category not found");
		}
		if (updated == 0)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be updated");
		else
			return new ApiResponse("product updated", HttpStatus.OK);
	}

	@Override
	public ApiResponse deleteProduct(Integer id) {
		if (repo.deleteProduct(id) > 0)
			return new ApiResponse("product removed", HttpStatus.OK);
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be deleted");
	}

	@Override
	public ApiResponse updateProductStock(String gtin, Integer stock) {
		Product product = getProduct(gtin);
		if (stock > product.getStock())
			throw new ApiException(HttpStatus.BAD_REQUEST, "stock to update is invalid");

		repo.updateProductStock(gtin, product.getStock() - stock);
		return new ApiResponse("product stock updated", HttpStatus.OK);
	}

	@Override
	public ApiResponse updateProductCategory(String gtin, Integer category_id) {

		Category new_category = repoCategory.findByCategoryId(category_id);
		if (new_category == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");

		Product producto = repo.getProductByGTIN(gtin);
		if (producto == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");

		repo.updateProductCategory(gtin, category_id);
		return new ApiResponse("product category updated", HttpStatus.OK);
	}

	private List<SimpleProduct> makeItSimple(List<Product> products) {
		ArrayList<SimpleProduct> productos_simples = new ArrayList<SimpleProduct>();
		for (Product p : products) {
			productos_simples.add(new SimpleProduct(p));
		}
		return productos_simples;
	}
}
