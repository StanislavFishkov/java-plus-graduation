package ru.practicum.ewm.event.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.categories.model.Category;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
}
