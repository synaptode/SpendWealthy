package com.spendwealthy.app.data.mapper

import com.spendwealthy.app.db.CategoryEntity
import com.spendwealthy.app.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isDefault = isDefault
    )
}
