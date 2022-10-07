package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.product.api.entity.Category;
import com.product.api.repository.RepoCategory;

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
        return repo.findByCategoryId(category_id);
    }

    @Override
    public String createCategory(Category category) {
        
        Category categorySave = (Category) repo.findByCategory(category.getCategory());

        if (categorySave != null) {
            if (categorySave.isActive()) {
                return "category already exists";
            }
            repo.activateCategory(categorySave.getID());
            return "category has been activated";
        }

        repo.creatCategory(category.getCategory());
        return "category created";
    }

    @Override
    public String updateCategory(Integer category_id, Category category) {
        Category categorySave = (Category) repo.findByCategoryId(category_id);

        if (categorySave == null) {
            return "category does no exist";
        }
        if (!categorySave.isActive()) {
            return "category is not activate";
        }

        categorySave = (Category) repo.findByCategory(category.getCategory());
        if (categorySave != null) {
            return "category already exist";
        }

        repo.updateCategory(category_id, category.getCategory());
        return "category update";

    }

    @Override
    public String deleteCategory(Integer category_id) {
        Category categorySave = (Category) repo.findByCategoryId(category_id);

        if (categorySave == null) {
            return "category does no exist";
        }
        if (!categorySave.isActive()) {
            return "category does no exist";
        }
        repo.deleteById(category_id);
        return "category removed";
    }

}
