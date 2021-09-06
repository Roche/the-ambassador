package com.filipowm.ambassador.storage

import org.jooq.SortField
import org.jooq.Table
import org.jooq.TableField
import org.springframework.data.domain.Sort

object Sorting {

    fun within(table: Table<*>): SortingBuilder {
        return SortingBuilder(table)
    }

    class SortingBuilder(private val table: Table<*>) {

        fun by(specification: Sort): List<SortField<*>> {
            return specification.map { createSortField(table, it) }.toList()
        }

        private fun createSortField(table: Table<*>, order: Sort.Order): SortField<*> {
            val sortFieldName = order.property
            val sortDirection = order.direction
            val tableField = getTableField(table, sortFieldName)
            return convertTableFieldToSortField(tableField, sortDirection)
        }

        private fun getTableField(table: Table<*>, sortFieldName: String): TableField<*, *> {
            return try {
                val felds = table.javaClass.fields
                table.javaClass
                    .getField(sortFieldName.toUpperCase())
                    .get(table) as TableField<*, *>
            } catch (ex: NoSuchFieldException) {
                val errorMessage = String.format("Unable to sort by non existent field")
                throw InvalidSortFieldException(sortFieldName, errorMessage, ex)
            } catch (ex: IllegalAccessException) {
                val errorMessage = String.format("Unable to sort by non existent field")
                throw InvalidSortFieldException(sortFieldName, errorMessage, ex)
            }
        }

        private fun convertTableFieldToSortField(tableField: TableField<*, *>, sortDirection: Sort.Direction): SortField<*> {
            return if (sortDirection == Sort.Direction.ASC) {
                tableField.asc()
            } else {
                tableField.desc()
            }
        }
    }
}
