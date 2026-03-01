package com.burixer85.aipedia.home.domain.usecase

import com.burixer85.aipedia.core.domain.model.Category
import com.burixer85.aipedia.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return homeRepository.getAllCategories()
    }
}