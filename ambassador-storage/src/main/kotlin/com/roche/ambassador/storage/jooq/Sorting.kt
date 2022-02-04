package com.roche.ambassador.storage.jooq

import com.roche.ambassador.storage.InvalidSortFieldException
import org.jooq.Field
import org.jooq.SortField
import org.jooq.Table
import org.jooq.TableField
import org.springframework.data.domain.Sort
import java.util.*

object Sorting {

    fun within(table: Table<*>): SortingBuilder {
        return SortingBuilder(table)
    }

    class SortingBuilder(private val table: Table<*>) {

        fun by(specification: Sort): List<SortField<*>> {
            return specification.map { createSortField(table, it) }.toList()
        }

        fun by(fieldName: String, direction: Sort.Direction): List<SortField<*>> = by(Sort.by(direction, fieldName))

        fun by(field: Field<*>, direction: Sort.Direction): List<SortField<*>> {
            return listOf(convertTableFieldToSortField(field, direction))
        }

        private fun createSortField(table: Table<*>, order: Sort.Order): SortField<*> {
            val sortFieldName = order.property
            val sortDirection = order.direction
            val tableField = getTableField(table, sortFieldName)
            return convertTableFieldToSortField(tableField, sortDirection)
        }

        private fun getTableField(table: Table<*>, sortFieldName: String): TableField<*, *> {
            return try {
                table.javaClass
                    .getField(sortFieldName.uppercase(Locale.getDefault()))
                    .get(table) as TableField<*, *>
            } catch (ex: NoSuchFieldException) {
                throw InvalidSortFieldException(sortFieldName, "Unable to sort by non existent field", ex)
            } catch (ex: IllegalAccessException) {
                throw InvalidSortFieldException(sortFieldName, "Unable to sort by non existent field", ex)
            }
        }

        private fun convertTableFieldToSortField(tableField: Field<*>, sortDirection: Sort.Direction): SortField<*> {
            return if (sortDirection == Sort.Direction.ASC) {
                tableField.asc()
            } else {
                tableField.desc()
            }
        }
    }
}
