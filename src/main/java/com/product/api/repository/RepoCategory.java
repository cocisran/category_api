package com.product.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.product.api.entity.Category;

@Repository
public interface RepoCategory extends JpaRepository<Category, Integer> {

    /**
     * Realiza una consulta a la tabla category filtrando por status
     * 
     * @param status status seleccionado [1,0]
     * @return Una lista con el resultado de la consulta
     */
    @Query(value = "SELECT * FROM category WHERE status = :status", nativeQuery = true)
    List<Category> findByStatus(@Param("status") Integer status);

    /**
     * Realiza una consulta a la tabla category filtrando por id
     * 
     * @param category_id id a consultar
     * @return Una lista con el resultado de la consulta
     */
    @Query(value = "SELECT * FROM category WHERE category_id = :category_id AND status = 1", nativeQuery = true)
    Category findByCategoryId(@Param("category_id") Integer category_id);

    /**
     * Realiza una consulta a la tabla category filtrando por nombre de categoria
     * 
     * @param category nombre de la categoria
     * @return una lista de todas las categorias que coincidan con el nombre pasado
     *         por parametro
     */
    @Query(value = "SELECT * FROM category WHERE category = :category", nativeQuery = true)
    Category findByCategory(@Param("category") String category);

    /**
     * Inserta una nueva categoria a la tabla category
     * 
     * @param category nombre de la nueva categoria
     * @return la categoria insertada en la tabla
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO category (category,status) VALUES (:category,1)", nativeQuery = true)
    void creatCategory(@Param("category") String category);

    /**
     * Actualiza una categoria existente
     * 
     * @param category_id id de la categoria que se desea actualizar
     * @param category    nuevo valor para el nombre de la categoria
     * @return numero de entradas actualizadas
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET category = :category WHERE category_id = :category_id", nativeQuery = true)
    Integer updateCategory(@Param("category_id") Integer category_id, @Param("category") String category);

    /**
     * Activa una categoria
     * 
     * @param category_id id de la categoria a activar
     * @return numero de entradas actualizadas
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET status = 1 WHERE category_id = :category_id", nativeQuery = true)
    Integer activateCategory(@Param("category_id") Integer category_id);

    /**
     * Borra logicamente una categoria
     * 
     * @param category_id categoria a borrar
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE category SET status = 0 WHERE category_id = :category_id", nativeQuery = true)
    void deleteById(@Param("category_id") Integer category_id);

}