package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Category;
import com.product.api.repository.RepoCategory;
import com.product.exception.ApiException;

@Service
public class SvcCategoryImp implements SvcCategory {

    @Autowired
    RepoCategory repo;

    @Override
    public List<Category> getCategorys() {
        return repo.findByStatus(1);
    }

    @Override
    public Category getCategory(Integer category_id) {
        Category saved_category  = repo.findByCategoryId(category_id);
        if (saved_category == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "category does not exist");
        return saved_category;
    }

    @Override
    public ApiResponse createCategory(Category category) {
        
        Category categorySave = (Category) repo.findByCategory(category.getCategory());

        if (categorySave != null) {
            if (categorySave.isActive()) {
               throw new ApiException(HttpStatus.BAD_REQUEST, "category alredy exists");
            }
            repo.activateCategory(categorySave.getID());
            return new ApiResponse("category has been activated",HttpStatus.OK);
        }

        repo.creatCategory(category.getCategory());
        return new ApiResponse("category created",HttpStatus.CREATED);
    }

    @Override
    public ApiResponse updateCategory(Integer category_id, Category category) {
        Category categorySave = (Category) repo.findByCategoryId(category_id);

        if (categorySave == null) 
            throw new ApiException(HttpStatus.NOT_FOUND,"category does no exist");
        if (!categorySave.isActive()) 
            throw new ApiException(HttpStatus.BAD_REQUEST, "category is not activate");

        categorySave = (Category) repo.findByCategory(category.getCategory());
        if (categorySave != null) 
            throw new ApiException(HttpStatus.BAD_REQUEST, "category already exist");

        repo.updateCategory(category_id, category.getCategory());
        return new ApiResponse("category update", HttpStatus.OK);
    }

    @Override
    public ApiResponse deleteCategory(Integer category_id) {
        Category categorySave = (Category) repo.findByCategoryId(category_id);

        if (categorySave == null) {
            throw new ApiException(HttpStatus.NOT_FOUND,"category does no exist");
        }
        repo.deleteById(category_id);
        return new ApiResponse("category removed", HttpStatus.OK);
    }

}
