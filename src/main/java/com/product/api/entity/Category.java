package com.product.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "category")
public class Category {
    public final static int ACTIVE = 1, DISABLE = 0;
    /* Category id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer category_id;

    /* Category name */
    @NotNull
    @Column(name = "category")
    private String category;

    /* Logical state of the object */
    @JsonIgnore
    @Column(name = "status")
    @Min(value = 0, message = "status must be 0 or 1")
    @Max(value = 1, message = "status must be 0 or 1")
    private Integer status;

    public Category(){}

    /**
     * Creates a new instance of a category object
     * default category status is active
     * 
     * @param id   unique identifier of the category
     * @param name name of the category
     */
    public Category(int id, String name) {
        this.category = name;
        this.category_id = id;
        this.status = ACTIVE;
    }

    /**
     * Category name
     * 
     * @return name of the category object
     */
    public String getCategory() {
        return category;
    }

    /**
     * Changes the current category name to param
     * 
     * @param category new category name
     */
    public void setCategoryName(String category) {
        this.category = category;
    }

    /**
     * Category id
     * 
     * @return name of the category id
     */
    public int getID() {
        return category_id;
    }

    /**
     * Disable this object
     */
    public void disable() {
        this.status = DISABLE;
    }

    /**
     * Enable this object
     */
    public void enable() {
        this.status = ACTIVE;
    }

    /**
     * Show if the current object is active
     * 
     * @return true if is active, false otherwise
     */
    @JsonIgnore
    public boolean isActive() {
        return this.status == ACTIVE;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != Category.class)
            return false;
        Category o = (Category) obj;
        return this.category.equals(o.category) && this.category_id == o.category_id;
    }

    @Override
    public String toString() {
        return "[" + "category_id: " + category_id + ", category: " + category + "]";
    }
}
