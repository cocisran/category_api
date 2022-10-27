package com.product.api.service;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
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
		Product product = repo.getProductByGTIN(gtin); // sustituir null por la llamada al método implementado en el
														// repositorio
		if (product != null) {
			product.setCategory(repoCategory.findByCategoryId(product.getCategory_id()));
			return product;
		} else
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
	}

	/*
	 * 4. Implementar el método createProduct considerando las siguientes
	 * validaciones:
	 * 
	 * 
	 * 3.
	 */
	@Override
	public ApiResponse createProduct(Product in) {
		Category product_category = in.getCategory();
		Category db_category = repoCategory.findByCategoryId(product_category.getID());

		// 1. validar que la categoría del nuevo producto exista
		if (db_category == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");

		// 2.1 el código GTIN es unico
		Product db_product_instance = repo.getAllProductByGTIN(in.getGtin());
		if (db_product_instance != null) {
			if (db_product_instance.getStatus() == 1)
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");

			/*
			 * si al intentar realizar un nuevo registro ya existe un producto con el mismo
			 * GTIN pero tiene estatus 0,
			 * entonces se debe cambiar el estatus del producto existente a 1 y actualizar
			 * sus datos con los del nuevo registro
			 */
			in.setStatus(1);
			ApiResponse update_response = updateProduct(in, in.getProduct_id());
			return new ApiResponse("product activated", update_response.getStatus());
		}

		// 2.2 el nombre del producto son únicos
		db_product_instance = repo.getProductByName(in.getProduct());
		if (db_product_instance != null)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");

		// create category
		repo.createProduct(in.getGtin(), 
							in.getProduct(), 
							in.getDescription(), 
							in.getPrice(),
							in.getStock(), 
							in.getCategory_id());
		return new ApiResponse("product created",HttpStatus.OK);
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
}
