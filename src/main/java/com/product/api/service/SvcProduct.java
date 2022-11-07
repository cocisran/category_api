package com.product.api.service;

import java.util.List;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Product;
import com.product.api.entity.SimpleProduct;

public interface SvcProduct {

	public Product getProduct(String gtin);

	public List<SimpleProduct> getListProductsByCategory(Integer category_id);

	public ApiResponse createProduct(Product in);

	public ApiResponse updateProduct(Product in, Integer id);

	public ApiResponse updateProductStock(String gtin, Integer stock);

	public ApiResponse updateProductCategory(String gtin, Integer category_id);

	public ApiResponse deleteProduct(Integer id);

}
